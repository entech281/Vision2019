#!/bin/sh
docker run --device=/dev/video0:/dev/video0 -v /tmp/.X11-unix:/tmp/.X11-unix -e DISPLAY=$DISPLAY -v /home/pi/scripts:/scripts -p 5000:5000 -ti spmallick/opencv-docker:opencv /bin/bash
