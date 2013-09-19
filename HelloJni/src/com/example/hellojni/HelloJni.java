/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.WindowManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.highgui.Highgui;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import com.example.hellojni.R;

import com.example.hellojni.ImageProcessingActivity;


public class HelloJni extends Activity implements CvCameraViewListener2
{
	private static final String    TAG                 = "OCVSample::Activity";
	private ImageProcessingActivity localIPActicity; 	
    private CameraBridgeViewBase   mOpenCvCameraView;
    private Mat                    mRgba;
    private Mat                    mGray;
    private Mat                    outImage;
    private Mat 				   clearestImage = null;
    
    
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Loadprivate Mat 				   clearestImage; native library after(!) OpenCV initialization
                    System.loadLibrary("hello-jni");
                    // load cascade file from application resources
                	localIPActicity = new ImageProcessingActivity();
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_image_processing);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
    }
   
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();    
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat(height, width, CvType.CV_8UC1);
        outImage = new Mat(height, width, CvType.CV_8UC1);
        mRgba = new Mat(height, width, CvType.CV_8UC3);
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }
    
    
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        //mRgba = inputFrame.rgba();
        //mGray = inputFrame.gray();
        //outImage.create( mGray.size(), CvType.CV_8UC1 );
        //Log.i( TAG, Integer.toString( mRgba.channels() ) );
        //stringFromJNI( mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(), outImage.getNativeObjAddr() );
        //return outImage; 
    	getFrames( "/sdcard/myvideo.mp4", 50 );
		Highgui.imwrite( "/sdcard/Xreal.jpg", clearestImage);
		
		Log.i( TAG, "PRAT : Clearest Image Found " );
		//clearestImage.adjustROI(0, inputFrame.gray().size().height, 0, inputFrame.gray().size().width);
    	return clearestImage;
    }
    
    private void getFrames(String path, int noOfFrames) {
			
		MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
		mRetriever.setDataSource(path);                    
		for (int i = 0; i < noOfFrames; i++) {
			Bitmap bFrame = mRetriever.getFrameAtTime( 33*1000, 
					   MediaMetadataRetriever.OPTION_CLOSEST);
			if( bFrame==null )
				continue;
			Log.i( TAG, "Processing Frame - " + i );
			Mat img = new Mat();
			Utils.bitmapToMat( bFrame, img);
			if( clearestImage == null ){
				Log.i( TAG, "Processing Creating Clearest Image - " + i );
				clearestImage = new Mat();
				clearestImage.create( img.size(), CvType.CV_8UC1 );
			}
			Log.i( TAG, "Processing Frame 2 - " + i );
			Mat imgGray = new Mat( img.size(), CvType.CV_8UC1 );
			Imgproc.cvtColor( img, imgGray, Imgproc.COLOR_BGR2GRAY);
			stringFromJNI( imgGray.getNativeObjAddr(), img.getNativeObjAddr(), clearestImage.getNativeObjAddr() );
			GetNumberPlate( clearestImage.getNativeObjAddr(), clearestImage.getNativeObjAddr() );
			img.release();
			imgGray.release();
			bFrame.recycle();
		}
	}

    
    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native void stringFromJNI(long grayImage, long rgbImage, long output);

    /* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI(long inputImage, long outImage);
    public native void nativeDetect( long inputImage, long outImage);
    public native void GetNumberPlate( long imageGray, long outImage );
}
