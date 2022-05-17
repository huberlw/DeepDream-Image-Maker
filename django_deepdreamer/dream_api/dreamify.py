#COPYRIGHT 2019 The TensorFlow Authors.

#@title Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

###
# THIS FILE HAS BEEN MODIFIED FOR OUR USE AND DOES NOT REPRESENT THE ORIGINAL AUTHORS
###

from io import BytesIO
import tensorflow as tf
import numpy as np
import IPython.display as display
import PIL.Image
from PIL import Image
import imghdr


# Normalize an image
def deprocess(img):
    img = 255 * (img + 1.0) / 2.0
    return tf.cast(img, tf.uint8)


def random_roll(img, maxroll):
    # Randomly shift the image to avoid tiled boundaries.
    shift = tf.random.uniform(shape=[2], minval=-maxroll, maxval=maxroll, dtype=tf.int32)
    img_rolled = tf.roll(img, shift=shift, axis=[0, 1])
    return shift, img_rolled


def calc_loss(img, model):
    # Pass forward the image through the model to retrieve the activations.
    # Converts the image into a batch of size 1.
    img_batch = tf.expand_dims(img, axis=0)
    layer_activations = model(img_batch)
    if len(layer_activations) == 1:
        layer_activations = [layer_activations]

    losses = []
    for act in layer_activations:
        loss = tf.math.reduce_mean(act)
        losses.append(loss)

    return tf.reduce_sum(losses)


class TiledGradients(tf.Module):
    def __init__(self, model):
        self.model = model

    @tf.function(
        input_signature=(
                tf.TensorSpec(shape=[None, None, 3], dtype=tf.float32),
                tf.TensorSpec(shape=[2], dtype=tf.int32),
                tf.TensorSpec(shape=[], dtype=tf.int32),)
    )
    def __call__(self, img, img_size, tile_size=512):
        shift, img_rolled = random_roll(img, tile_size)

        # Initialize the image gradients to zero.
        gradients = tf.zeros_like(img_rolled)

        # Skip the last tile, unless there's only one tile.
        xs = tf.range(0, img_size[1], tile_size)[:-1]
        if not tf.cast(len(xs), bool):
            xs = tf.constant([0])
        ys = tf.range(0, img_size[0], tile_size)[:-1]
        if not tf.cast(len(ys), bool):
            ys = tf.constant([0])

        for x in xs:
            for y in ys:
                # Calculate the gradients for this tile.
                with tf.GradientTape() as tape:
                    # This needs gradients relative to `img_rolled`.
                    # `GradientTape` only watches `tf.Variable`s by default.
                    tape.watch(img_rolled)

                    # Extract a tile out of the image.
                    img_tile = img_rolled[y:y + tile_size, x:x + tile_size]
                    loss = calc_loss(img_tile, self.model)

                # Update the image gradients for this tile.
                gradients = gradients + tape.gradient(loss, img_rolled)

        # Undo the random shift applied to the image and its gradients.
        gradients = tf.roll(gradients, shift=-shift, axis=[0, 1])

        # Normalize the gradients.
        gradients /= tf.math.reduce_std(gradients) + 1e-8

        return gradients


def run_deep_dream_with_octaves(get_tiled_gradients, img, steps_per_octave=100, step_size=0.01, octaves=[-4, 0], octave_scale=1.5):
    img = tf.keras.utils.img_to_array(img)
   
    base_shape = tf.shape(img)
    
    img = tf.keras.applications.mobilenet_v2.preprocess_input(img)

    initial_shape = img.shape[:-1]
    img = tf.image.resize(img, initial_shape)
    for octave in octaves:
        # Scale the image based on the octave
        new_size = tf.cast(tf.convert_to_tensor(base_shape[:-1]), tf.float32) * (octave_scale ** octave)
        new_size = tf.cast(new_size, tf.int32)
        img = tf.image.resize(img, new_size)

        for step in range(steps_per_octave):
            gradients = get_tiled_gradients(img, new_size)
            img = img + gradients * step_size
            img = tf.clip_by_value(img, -1, 1)

            if step % 10 == 0:
                display.clear_output(wait=True)

    result = deprocess(img)
    return result

""" MAIN LOOP HERE """
def sweet_dreams(image):
    # layers: 0 - 10
    # concatenated_layers = ['block_2_add', 'block_4_add', 'block_5_add', 'block_7_add', 'block_8_add', 'block_9_add', 'block_11_add', 'block_12_add', 'block_14_add', 'block_15_add']

    # image to dreamify 
    original_img = Image.open(image)
    if original_img.format != 'JPEG':
        original_img = original_img.convert('RGB')
        buffer = BytesIO()
        original_img.save(buffer, format='JPEG')
        original_img = Image.open(buffer)

    # layers whose activations to maximize
    names = ['block_2_add', 'block_4_add']

    # Convolutional Neural Network Model
    base_model = tf.keras.applications.MobileNetV2(include_top=False, weights='imagenet')

    layers = [base_model.get_layer(name).output for name in names]

    # Create the feature extraction model
    dream_model = tf.keras.Model(inputs=base_model.input, outputs=layers)

    # create constant tensor (multidimensional array)
    img = tf.constant(np.array(original_img))
    base_shape = tf.shape(img)[:-1]

    # dreamify
    get_tiled_gradients = TiledGradients(dream_model)
    img = run_deep_dream_with_octaves(get_tiled_gradients, img=original_img, step_size=0.01)

    # make image original size
    img = tf.image.resize(img, base_shape)
    img = tf.image.convert_image_dtype(img / 255.0, dtype=tf.uint8)
    
    array = np.array(img)
    pillow_img = Image.fromarray(array)
    
    return pillow_img
    