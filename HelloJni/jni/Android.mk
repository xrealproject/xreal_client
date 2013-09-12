LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_CAMERA_MODULES:=on
#OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=SHARED
include /home/prat/installs/opencv-2.4.5/android/build/install/sdk/native/jni/OpenCV.mk

LOCAL_MODULE := hello-jni
LOCAL_SRC_FILES := hello-jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)
include $(LD_LIBRARY_PATH)
