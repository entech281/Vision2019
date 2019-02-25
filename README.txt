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
3) Also note that by using the process acknowledged in step 1, we realized the code created by FIRST was time consuming, so we got rid of it and manually wrote the portions we wanted (These portions will have a "MANUAL REPLACEMENT INSTEAD OF FIRST CODE" comment above and below them to allow you to identify them

================
Important camera settings that help camera identify targets
================

1) "exposure_time_absolute" is set quite low, for instance currently 13
2) "contrast" should be set quite high, currently 100
3) "saturation" should be kind of high, currently 76

NOTE:
   a) These settings can be found in... 
        Vision2019/team281-camera.json
      ... and can be modified, either by manually changing the values in the file or by generating a new file and saving the              json into /boot/frc.json
   b) It is helpful to have code, which is currently written, to let you know if the targets are even getting recognized prior      to the algorithms being run. 
   
        Vision2019/src/main/java/frc/robot/VisionProcessor.java
         Lines 134, 135, 136
         
         
===============
GENERAL OVERVIEW
===============

1) Most of the code for the analysis and calculations based of the frame is stored in the VisionProcessor class so that is a good place to start

===============
QUESTIONS AND CONCERNS
==============

1) If you have any questions don't be afraid to post it as a github issue and we will get back to you soon with an answer


GOOD LUCK FROM TEAM 281 and hope this is helpful
