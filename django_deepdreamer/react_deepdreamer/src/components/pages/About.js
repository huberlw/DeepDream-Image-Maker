import React from 'react'
import './About.css'

export default function About() {
  return (
        <div>
            <div className='about-container'>
                <div className='about-info'>
                  <h1>ABOUT</h1>
              
                  <p>DeepDreamer uses machine learning to convert a normal image into something new and exciting! 
                      Machine learning describes a type of algorithm that learns to predict something.
                      We are using Google's DeepDream algorithm, which can classify faces, animals, and other items. 
                      DeepDream takes an input image and gives it to a series of "layers" that will classify the image. 
                      Each layer recognizes different types of features in the input image. 
                      These features can be simple, like lines, or complex, like faces or trees.<br/><br/>
                      So, what happens when we tell DeepDream to amplify specific features in an image?
                      To try it for yourself, click on the "Dreamify" tab!<br/><br/>
                      Our website is a simple example of what you can do with DeepDream. 
                      To make a variety of dreamlike images with advanced options, download our desktop app from the "Download" tab.<br/><br/>
                      Authors:<br/>Dillon Bridgewater, Harlow Huber, Lincoln Huber, Dane Marohl, Gabriel Williams</p>
                </div>
            </div>
        </div>
  )
}