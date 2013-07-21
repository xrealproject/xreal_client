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
 *
 */
#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>

#include <string>
#include <vector>
#include <list>

#include <android/log.h>

extern "C" {
/*
 * Class:     om_example_hellojni_HelloJni
 * Method:    stringFromJNI
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv * jenv, jobject, jlong imageGray, jlong imageRGB, jlong outImage );


#define LOG_TAG "INITIAL PRAT TEST "
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
double calcClearity( Mat& image );
class SessionData
{
public:
	Mat mRGB;
	Mat mGray;
	double clearity;
	SessionData( Mat &iRGB, Mat &iGray )
	{
		mRGB = iRGB.clone();
		mGray = iGray.clone();
		clearity = calcClearity( iGray );
	}
};

static vector<SessionData *> matV;
static const int Mat_Vec_Size = 50;

double calcClearity( Mat& image )
{
	IplImage iplImage = image;
	double secondOrderDerivative = 0 ;
	for( int i=0; i<iplImage.height;i=i+4 )
		for(int j=0;j<iplImage.width;j=j+4 ){
			unsigned char *ptr = (unsigned char *) iplImage.imageData;
			int wS = iplImage.widthStep;
			int nC = iplImage.nChannels;
			secondOrderDerivative += 8 * *(ptr+i*wS+j*nC)-*(ptr+(i-1)*wS+(j-1)*nC) -*(ptr+(i-1)*wS+(j)*nC) -*(ptr+(i-1)*wS+(j+1)*nC)\
					-*(ptr+(i)*wS+(j-1)*nC) -*(ptr+(i)*wS+(j+1)*nC) \
					-*(ptr+(i+1)*wS+(j-1)*nC) -*(ptr+(i+1)*wS+(j)*nC) -*(ptr+(i+1)*wS+(j+1)*nC);
		}
	return secondOrderDerivative;
}

SessionData* getClearestImageFromVector( vector<SessionData *>& matV ){
	SessionData* maxSession = *(matV.begin());
	double maxClearityVal = maxSession->clearity;
	for( vector<SessionData *>::iterator matIter = matV.begin(); matIter < matV.end(); ++matIter  ){
		SessionData* currSession = *matIter;
		double clearityVal = currSession->clearity;
		if( clearityVal >  maxClearityVal ){
			maxSession = currSession;
			maxClearityVal = clearityVal;
		}
	}
	return( maxSession );
}

void ForcedCopyTo( Mat& src, Mat& dst )
{
	Size srcSize = src.size();
	for( int i=0; i<src.size().height; ++i ){
		for( int j=0; j<src.size().width;++j ){
			unsigned char *ptrDst = dst.ptr(i);
			unsigned char *ptrSrc = src.ptr(i);
			for( int k=0;k<src.channels();k++ ){
				ptrDst[ j*src.channels()+k ] = ptrSrc[ j*src.channels()+k ];
			}
		}
	}
}

JNIEXPORT void JNICALL Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv * jenv, jobject, jlong imageGray, jlong imageRGB, jlong outImage )
{
	LOGD("PRAT NATIVE DETECT STARTED");
	Mat& mGr  = *((Mat*)imageGray);
	Mat& mRgb = *((Mat*)imageRGB);
	Mat& oImage = *((Mat*)outImage);

	SessionData* newSession = new SessionData(mRgb, mGr);

	matV.push_back(newSession);
	if( matV.size() > Mat_Vec_Size ){
		(*matV.begin())->mRGB.~Mat();
		(*matV.begin())->mGray.~Mat();
		matV.erase(matV.begin());
	}

	SessionData* clearestSessions = getClearestImageFromVector( matV );

	stringstream ss;
	ss << oImage.channels();
	const char *channelsRGB = (ss.str()).c_str();
	LOGD( "Prat Channels" );
	LOGD( channelsRGB );
	//ForcedCopyTo( clearestImage, oImage);
	//mGr.copyTo( oImage );

	oImage = clearestSessions->mGray;
	//circle(oImage, Point(20, 20), 10, Scalar(255,0,0,0));
	//mRgb = mGr;
	LOGD("PRAT NATIVE DETECT STOPED");

    //return (*env)->NewStringUTF(env, "Hello from JNI !");
}

JNIEXPORT void JNICALL Java_com_example_hellojni_HelloJni_nativeDetect( JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong outImage )
{
	LOGD("PRAT NATIVE DETECT STARTED");
	Mat& mGr  = *(Mat*)imageGray;
	Mat& mRgb = *(Mat*)outImage;
	mRgb = mGr.clone();
	LOGD("PRAT NATIVE DETECT STOPED");
}

}
