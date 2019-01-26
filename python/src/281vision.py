
import json
import time
import sys
import cv2
import numpy
import cscore as cs
from cscore import CameraServer, VideoSource
from networktables import NetworkTablesInstance

#   JSON format:
#   {
#       "team": <team number>,
#       "ntmode": <"client" or "server", "client" if unspecified>
#       "cameras": [
#           {
#               "name": <camera name>
#               "path": <path, e.g. "/dev/video0">
#               "pixel format": <"MJPEG", "YUYV", etc>   // optional
#               "width": <video mode width>              // optional
#               "height": <video mode height>            // optional
#               "fps": <video mode fps>                  // optional
#               "brightness": <percentage brightness>    // optional
#               "white balance": <"auto", "hold", value> // optional
#               "exposure": <"auto", "hold", value>      // optional
#               "properties": [                          // optional
#                   {
#                       "name": <property name>
#                       "value": <property value>
#                   }
#               ]
#           }
#       ]
#   }

configFile = "/boot/frc.json"
RECOGNIZED_TARGET_COLOR = ( 255, 0, 0 )
UNRECOGNIZED_CONTOUR_COLOR = ( 0, 0, 255) 
LOWER_FILTER= [55,167,177]
UPPER_FILTER = [85,255,255]
lower_range = numpy.array(LOWER_FILTER)
upper_range = numpy.array(UPPER_FILTER)
class CameraConfig: pass

team = 281
server = False
cameraConfigs = []

"""Report parse error."""
def parseError(str):
    print("config error in '" + configFile + "': " + str, file=sys.stderr)

"""Read single camera configuration."""
def readCameraConfig(config):
    cam = CameraConfig()

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read camera name")
        return False

    # path
    try:
        cam.path = config["path"]
    except KeyError:
        parseError("camera '{}': could not read path".format(cam.name))
        return False

    cam.config = config

    cameraConfigs.append(cam)
    return True

"""Read configuration file."""
def readConfig():
    global team
    global server

    # parse file
    try:
        with open(configFile, "rt") as f:
            j = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(configFile, err), file=sys.stderr)
        return False

    # top level must be an object
    if not isinstance(j, dict):
        parseError("must be JSON object")
        return False

    # team number
    try:
        team = j["team"]
    except KeyError:
        parseError("could not read team number")
        return False

    # ntmode (optional)
    if "ntmode" in j:
        str = j["ntmode"]
        if str.lower() == "client":
            server = False
        elif str.lower() == "server":
            server = True
        else:
            parseError("could not understand ntmode value '{}'".format(str))

    # cameras
    try:
        cameras = j["cameras"]
    except KeyError:
        parseError("could not read cameras")
        return False
    for camera in cameras:
        if not readCameraConfig(camera):
            return False

    return True

"""Start running the camera."""
def startCamera(config):
    print("Starting camera '{}' on {}".format(config.name, config.path))
    camera = CameraServer.getInstance() \
        .startAutomaticCapture(name=config.name, path=config.path)

    camera.setConfigJson(json.dumps(config.config))

    return camera

def draw_found_polygons(img):
    #image = cv2.cvtColor(image,cv2.COLOR_YUV2BGR)
    filtered = cv2.cvtColor(img,cv2.COLOR_BGR2HSV)
    
    filtered = cv2.inRange(filtered,lower_range, upper_range)
    

    
    contours = cv2.findContours(filtered,cv2.RETR_EXTERNAL ,cv2.CHAIN_APPROX_SIMPLE)[1] 
    
    selected_contours = contours
    #print("Found %d contours" % len(contours))
    cv2.drawContours(img, selected_contours, -1, RECOGNIZED_TARGET_COLOR, 4)    
    #return edged
    #return selected_contours
    return filtered

def find_rectangles(contour_list):
    selected_contours = []
    for contour in contour_list:
        # approximate the contour
        peri = cv2.arcLength(contour, True)
        approx = cv2.approxPolyDP(contour, 0.015 * peri, True)
    
        # if our approximated contour has four points, then
        # we can assume that we have found our screen
        if len(approx) > 3:
            selected_contours.append(contour)
    
    return selected_contours

if __name__ == "__main__":
    if len(sys.argv) >= 2:
        configFile = sys.argv[1]

    # read configuration
    if not readConfig():
        sys.exit(1)

    # start NetworkTables
    ntinst = NetworkTablesInstance.getDefault()
    if server:
        print("Setting up NetworkTables server")
        ntinst.startServer()
    else:
        print("Setting up NetworkTables client for team {}".format(team))
        ntinst.startClientTeam(team)

    # start cameras
    cameras = []
    for cameraConfig in cameraConfigs:
        cameras.append(startCamera(cameraConfig))

    first_camera = cameras[0]
    source = CameraServer.getInstance().getVideo()
    sink = CameraServer.getInstance().putVideo("Target",320,240)

    cvSource = cs.CvSource("cvsource", cs.VideoMode.PixelFormat.kMJPEG, 320, 240, 30)
    cvMjpegServer = cs.MjpegServer("cvhttpserver", 8082)
    cvMjpegServer.setSource(cvSource)
    print("OpenCV output mjpg server listening at http://0.0.0.0:8082")

    # Allocating new images is very expensive, always try to preallocate
    img = numpy.zeros(shape=(240, 320, 3), dtype=numpy.uint8)

    # loop forever
    while True:
        # Tell the CvSink to grab a frame from the camera and put it
        # in the source image.  If there is an error notify the output.
        time, img = source.grabFrame(img)
        if time == 0:
            # Send the output the error.
            sink.notifyError(source.getError());
            # skip the rest of the current iteration
            continue

        #
        # Insert your image processing logic here!
        #
        #img = draw_crosshairs(img)

        sink.putFrame(img)
        cvSource.putFrame(draw_found_polygons(img))
        #cvSource.putFrame(img)
