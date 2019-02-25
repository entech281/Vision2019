=======================
Building locally on rPi
=======================

1) Run "./gradlew build"
2) Run "./install.sh" (replaces /home/pi/runCamera)
3) Run "./runInteractive" in /home/pi or "sudo svc -t /service/camera" to
   restart service.


===================
Building on desktop
===================

One time setup
--------------

Copy the .jar files from /home/pi/javalibs on the Pi to the source directory.

Building
--------

1) Run "./gradlew build"
2) Copy build/libs/java-multiCameraServer-all.jar and runCamera to /home/pi on
   the Pi.  Note: the .jar filename may be different; if it is, either rename
   when copying to the Pi or edit runCamera to reflect the new jar name.
3) On the Pi, run "./runInteractive" in /home/pi or
   "sudo svc -t /service/camera" to restart service.

=================
How it works
=================

1) Has a generated GRIP pipeline to filter and find specific contours
2) Takes the contours and runs algorithms specified in the filters folder code that eliminate random rectangles
3) Uses the average area of the rotated rectangles to calculate the distance to the target
4) Uses the average distance to the center of the frame of the rotated rectangle to determine how off centered the robot is
5) Updates the network tables with data calculated to be used elsewhere

=================
Tricks to increase frame rate
=================

1) Has a class called Framerate tracker that creates an object that has the capability to return the framerate and the framecount
   a) Use this object to find the code that takes the most time
2) Typically, resizing the stream tends to help increase frame rate
3) 
