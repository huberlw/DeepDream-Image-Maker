import os
from django.conf import settings
from django.db import models
from django.shortcuts import redirect, render
from django.template import Template, Context
from urllib3 import HTTPResponse
from . import dreamify
from io import BytesIO
from django.core.files import File
from django.db import models
from django.http import HttpResponseRedirect

# Create your models here.
class Sleepy(models.Model):
    img = models.ImageField(blank=False, null=True)
    done = models.BooleanField(null=False)
    
    def save(self, *args, **kwargs):
        pillow = dreamify.sweet_dreams(self.img)
        
        try:
            os.remove(os.path.join(settings.MEDIA_ROOT, 'dream.png')) 
        except Exception:
            pass
        
        buffer = BytesIO()
        pillow.save(buffer, 'PNG')
        
        self.img.save('dream.png', File(buffer), False)
        self.done = True
        