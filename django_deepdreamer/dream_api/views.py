from django.shortcuts import render, redirect
from django.template import RequestContext
from .serializers import SleepySerializer
from django.http import HttpResponse
from rest_framework import viewsets
from .models import Sleepy

# Create your views here.
class SleepyViewSet(viewsets.ModelViewSet):
    queryset = Sleepy.objects.all()
    serializer_class = SleepySerializer
    
    def post(self, request, *args, **kwargs):
        img = request.data['img']
        done = request.data['done']
        Sleepy.objects.create(img=img, done=done) 
                 
        return HttpResponse({'message': 'Falling asleep'}, status=200)

        