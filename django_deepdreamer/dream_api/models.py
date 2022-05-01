import os
from django.conf import settings
from django.db import models
from . import dreamify
from io import BytesIO
from django.core.files import File

# Create your models here.
class Sleepy(models.Model):
    img = models.ImageField(blank=False, null=True)
    done = models.BooleanField(null=False)
    
    def save(self, *args, **kwargs):
        pillow = dreamify.sweet_dreams(self.img)
        
        try:
            os.remove(os.path.join(settings.MEDIA_ROOT, self.img.name)) 
        except Exception:
            pass
        
        buffer = BytesIO()
        if self.img.name.endswith('.jpg'):
            pillow.save(buffer, 'JPEG')
        else:
            pillow.save(buffer, 'PNG')
        
        self.img.save(self.img.name, File(buffer), False)
        self.done = True
