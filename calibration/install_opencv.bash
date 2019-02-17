#!/bin/bash
apt-get -y clean
apt-get -y autoremove
CV_VERSION="masrer"
# Clean build directories
rm -rf opencv/build
rm -rf opencv_contrib/build
# Create directory for installation
mkdir installation
mkdir installation/OpenCV-"$cvVersion"

# Save current working directory
cwd=$(pwd)
apt -y update
apt -y upgrade

## Install dependencies
apt-get -y install build-essential checkinstall cmake pkg-config yasm
apt-get -y install git gfortran
apt-get -y install libjpeg8-dev libjasper-dev libpng12-dev
 
apt-get -y install libtiff5-dev
 
apt-get -y install libtiff-dev
 
apt-get -y install libavcodec-dev libavformat-dev libswscale-dev libdc1394-22-dev
apt-get -y install libxine2-dev libv4l-dev
cd /usr/include/linux
ln -s -f ../libv4l1-videodev.h videodev.h
cd $cwd
 
apt-get -y install libgstreamer0.10-dev libgstreamer-plugins-base0.10-dev
apt-get -y install libgtk2.0-dev libtbb-dev qt5-default
apt-get -y install libatlas-base-dev
apt-get -y install libmp3lame-dev libtheora-dev
apt-get -y install libvorbis-dev libxvidcore-dev libx264-dev
apt-get -y install libopencore-amrnb-dev libopencore-amrwb-dev
apt-get -y install libavresample-dev
apt-get -y install x264 v4l-utils
# Optional dependencies
apt-get -y install libprotobuf-dev protobuf-compiler
apt-get -y install libgoogle-glog-dev libgflags-dev
apt-get -y install libgphoto2-dev libeigen3-dev libhdf5-dev doxygen

apt-get -y install python3-dev python3-pip
pip3 install -U pip numpy
apt-get -y install python3-testresources

cd $cwd
# Install virtual environment
python3 -m venv OpenCV-"$cvVersion"-py3
echo "# Virtual Environment Wrapper" >> ~/.bashrc
echo "alias workoncv-$cvVersion=\"source $cwd/OpenCV-$cvVersion-py3/bin/activate\"" >> ~/.bashrc
source "$cwd"/OpenCV-"$cvVersion"-py3/bin/activate
#############

############ For Python 3 ############
# now install python libraries within this virtual environment
sed -i 's/CONF_SWAPSIZE=100/CONF_SWAPSIZE=1024/g' /etc/dphys-swapfile
/etc/init.d/dphys-swapfile stop
/etc/init.d/dphys-swapfile start
pip install numpy dlib
# quit virtual environment
deactivate

git clone https://github.com/opencv/opencv.git
cd opencv
git checkout $cvVersion
cd ..
 
git clone https://github.com/opencv/opencv_contrib.git
cd opencv_contrib
git checkout $cvVersion
cd ..

cd opencv
mkdir build
cd build

cmake -D CMAKE_BUILD_TYPE=RELEASE \
            -D CMAKE_INSTALL_PREFIX=$cwd/installation/OpenCV-"$cvVersion" \
            -D INSTALL_C_EXAMPLES=ON \
            -D INSTALL_PYTHON_EXAMPLES=ON \
            -D WITH_TBB=ON \
            -D WITH_V4L=ON \
            -D OPENCV_PYTHON3_INSTALL_PATH=$cwd/OpenCV-$cvVersion-py3/lib/python3.5/site-packages \
        -D WITH_QT=ON \
        -D WITH_OPENGL=ON \
        -D OPENCV_EXTRA_MODULES_PATH=../../opencv_contrib/modules \
        -D BUILD_EXAMPLES=ON ..

make -j$(nproc)
make install

sed -i 's/CONF_SWAPSIZE=1024/CONF_SWAPSIZE=100/g' /etc/dphys-swapfile
/etc/init.d/dphys-swapfile stop
/etc/init.d/dphys-swapfile start

	
echo "modprobe bcm2835-v4l2" >> ~/.profile

