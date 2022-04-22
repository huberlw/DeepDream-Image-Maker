from django.db import models
from . import dreamify

# Create your models here.
def upload_path(instance, filename):
    return '/'.join([str(instance.name), filename])

class Sleepy(models.Model):
    name = models.CharField(max_length=32, blank=False)
    img = models.ImageField(blank=False, null=True)
    sleey_img = models.ImageField(null=True, upload_to=upload_path)
    
    def save(self, *args, **kwargs):
        self.sleepy_img = dreamify.sweet_dreams(self.img)
        super().save(*args, **kwargs)