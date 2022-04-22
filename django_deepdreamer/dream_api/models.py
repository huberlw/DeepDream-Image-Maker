from django.db import models
from . import dreamify
from io import BytesIO
from django.core.files import File
from django.db import models

class Sleepy(models.Model):
    name = models.CharField(max_length=32, blank=False)
    img = models.ImageField(blank=False, null=True)
    
    def save(self, *args, **kwargs):
        pillow = dreamify.sweet_dreams(self.img)
        
        buffer = BytesIO()
        pillow.save(buffer, 'PNG')
        self.img.save('dream.png', File(buffer), False)
        