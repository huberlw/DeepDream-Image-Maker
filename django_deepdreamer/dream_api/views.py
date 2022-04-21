from django.shortcuts import render
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
        name = request.data['name']
        Sleepy.objects.create(name=name, img=img)
        return HttpResponse({'message': 'Falling asleep'}, status=200)
        