# Generated by Django 4.0.4 on 2022-04-21 22:46

from django.db import migrations, models
import django.utils.timezone
import dream_api.models


class Migration(migrations.Migration):

    dependencies = [
        ('dream_api', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='sleepy',
            name='sleey_img',
            field=models.ImageField(default=django.utils.timezone.now, upload_to=dream_api.models.upload_path),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='sleepy',
            name='img',
            field=models.ImageField(null=True, upload_to=''),
        ),
    ]