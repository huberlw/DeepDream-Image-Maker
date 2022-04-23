from rest_framework import serializers
from .models import Sleepy

class SleepySerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Sleepy
        fields = ['img', 'done']
        