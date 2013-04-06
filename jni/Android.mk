LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := MiSwitcherJNI
LOCAL_SRC_FILES := \
		  JNIMain.cpp

LOCAL_LDLIBS    := -llog

LOCAL_C_INCLUDES +=  $(LOCAL_PATH)/include

include $(BUILD_SHARED_LIBRARY)
