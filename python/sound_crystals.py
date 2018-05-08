import pygame as pg
import time

pg.mixer.init(frequency=44100, channels=1, buffer=1024, size=-16)
pg.init()

a1Note = pg.mixer.Sound("sounds/1.wav")
a2Note = pg.mixer.Sound("sounds/2.wav")

pg.mixer.set_num_channels(50)

for i in range(25):
    a1Note.play()
    time.sleep(0.3)