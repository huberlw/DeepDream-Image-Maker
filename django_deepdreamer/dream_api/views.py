from .serializers import SleepySerializer
from django.http import HttpResponse
from rest_framework import viewsets
from .models import Sleepy
import time
import os

def signal():
    print("hello world")

# Create your views here.
class SleepyViewSet(viewsets.ModelViewSet):
    queryset = Sleepy.objects.all()
    serializer_class = SleepySerializer
    
    def post(self, request, *args, **kwargs):
        img = request.data['img']
        done = request.data['done']
        Sleepy.objects.create(img=img, done=done)
        
        cont = True
        while (cont):
            if os.path.exists('../images/' + img.name):
                cont = False
            else:
                time.sleep(2.5)
            
        return HttpResponse({'message': 'Falling asleep'}, status=200)
        