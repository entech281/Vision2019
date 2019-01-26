#!/bin/bash
set -e
echo "Deploying to Pi..."
sshpass -p 'raspberry' ssh pi@frcvision.local "sudo /bin/mount -o remount,rw / && sudo /bin/mount -o remount,rw /boot"
sshpass -p 'raspberry' scp src/*  pi@frcvision.local:/home/pi
sshpass -p 'raspberry' ssh pi@frcvision.local "sudo /bin/mount -o remount,ro / && sudo /bin/mount -o remount,ro /boot"
sshpass -p 'raspberry' ssh pi@frcvision.local "sudo svc -t /service/camera"
echo "Done!"