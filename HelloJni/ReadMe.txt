Hello,

This file will explain the basic functionality of all the functions.
We will categories all the funtion with respect to files.

A) HelloJni.java - Initial Activity

1) private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) - This is required to be implmented for starting OpenCV callbacks and methods. This will initiate OpenCV manager on Android.
2)  public void onCreate(Bundle savedInstanceState) - This method is executed at start of the android activity. It will help android to set its variables and start OpenCV manager.
3) public void onPause() - Run by the activity when paused.
4) public void onCameraViewStarted(int width, int height) - This is executed just after camera view is started. 
5) public Mat onCameraFrame(CvCameraViewFrame inputFrame) - This will be executed when camera frame is recieved by the application. The inputFrame is a camera image.
6) private void getFrames(String path, int noOfFrames) - This is important funtion for our activity. All the image processing is done in this method.

B) hello-jni.cpp - Native class to enhance image processing.

1) Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv * jenv, jobject, jlong imageGray, jlong imageRGB, jlong outImage )
 	This function finds the clearest image among 50 Images. All the other methods implemented in this file is mostly linked to this method. 
 	Here we give grayColoured image and RGB image as input. The processing of second ordered derivative is done on this gray image. outImage is the output of this method.
 	This method will return RGB image which was found out to be clearest image for further processing.
 	
 2) jstring Java_com_example_hellojni_HelloJni_detectString( JNIEnv *env, jobject thiz, jlong imageGray )
 	this method will take input as gray colored image and use Tesseract an image processing library to find out the string and return back the string.
 	This funtion is still under progress.