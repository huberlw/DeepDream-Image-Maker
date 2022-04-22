import os
from django.conf import settings
from django.db import models
from . import dreamify
from io import BytesIO
from django.core.files import File
from django.db import models

# Create your models here.
def upload_path(instance, filename):
    return '/'.join("img", [str(instance.name), filename])

class Sleepy(models.Model):
    name = models.CharField(max_length=32, blank=False)
    img = models.ImageField(blank=False, null=True)
    
    def save(self, *args, **kwargs):
        pillow = dreamify.sweet_dreams(self.img)
        
        try:
            os.remove(os.path.join(settings.MEDIA_ROOT, 'dream.png')) 
        except Exception:
            pass
        
        buffer = BytesIO()
        pillow.save(buffer, 'PNG')
        self.img.save('dream.png', File(buffer), False)
        
        
        