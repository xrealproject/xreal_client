package com.example.hellojni;

import org.opencv.core.Mat;

public class ImageProcessingActivity {
	
	public void detect(Mat mGray, Mat mGrayOut){
		nativeDetect( mGray.getNativeObjAddr(), mGrayOut.getNativeObjAddr() );
	}
	private native void nativeDetect( long inputImage, long outImage);
	
}
