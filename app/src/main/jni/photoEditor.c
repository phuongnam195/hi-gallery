#include <jni.h>

JNIEXPORT void JNICALL
Java_com_example_higallery_activities_EditActivity_blackAndWhite(JNIEnv *env, jclass clazz,
                                                                 jintArray pixels, jint width,
                                                                 jint height) {
    jint *pixels_ = (*env)->GetIntArrayElements(env, pixels, NULL);

    //0 -- blue
    //1 -- green
    //2 -- red

    char* colors = (char*)pixels_;
    int pixelCount = height*width*4;
    int i = 0;
    while(i<pixelCount){
        unsigned char average = (colors[i] + colors[i + 1] + colors[i + 2])/3;
        colors[i] = average;
        colors[i+1]=average;
        colors[i+2]=average;
        i+=4;
    }
    (*env)->ReleaseIntArrayElements(env, pixels, pixels_, 0);
}


JNIEXPORT void JNICALL
Java_com_example_higallery_activities_EditActivity_BrightnessDown(JNIEnv *env, jclass clazz,
                                                                  jintArray pixels, jint width,
                                                                  jint height) {
    jint *pixels_ = (*env)->GetIntArrayElements(env, pixels, NULL);

    //0 -- blue
    //1 -- green
    //2 -- red

    unsigned char * colors = (unsigned char*)pixels_;
    int pixelCount = height*width*4;
    int i = 0;
    while(i<pixelCount){
        if(colors[i] > 3){
            colors[i] -= 4;
        }
        if(colors[i + 1] > 3){
            colors[i + 1] -= 4;
        }
        if(colors[i + 2] > 3){
            colors[i + 2] -= 4;
        }


        i+=4;
    }
    (*env)->ReleaseIntArrayElements(env, pixels, pixels_, 0);
}

JNIEXPORT void JNICALL
Java_com_example_higallery_activities_EditActivity_BrightnessUp(JNIEnv *env, jclass clazz,
                                                                jintArray pixels, jint width,
                                                                jint height) {
    jint *pixels_ = (*env)->GetIntArrayElements(env, pixels, NULL);

    //0 -- blue
    //1 -- green
    //2 -- red

    unsigned char * colors = (unsigned char*)pixels_;
    int pixelCount = height*width*4;
    int i = 0;
    while(i<pixelCount){
        if(colors[i] < 252){
            colors[i] += 4;
        }
        if(colors[i + 1] < 252){
            colors[i + 1] += 4;
        }
        if(colors[i + 2] < 252){
            colors[i + 2] += 4;
        }

        i+=4;
    }
    (*env)->ReleaseIntArrayElements(env, pixels, pixels_, 0);
}

JNIEXPORT void JNICALL
Java_com_example_higallery_activities_EditActivity_negative(JNIEnv *env, jclass clazz,
                                                            jintArray pixels, jint width,
                                                            jint height) {
    jint *pixels_ = (*env)->GetIntArrayElements(env, pixels, NULL);

    unsigned char * colors = (unsigned char*)pixels_;
    int pixelCount = height*width*4;
    int i = 0;
    while(i<pixelCount){
        if (i % 4 != 3){
            colors[i] = 255 - colors[i];
        }

        i+=1;
    }
    (*env)->ReleaseIntArrayElements(env, pixels, pixels_, 0);
}

JNIEXPORT void JNICALL
Java_com_example_higallery_activities_EditActivity_WarmDown(JNIEnv *env, jclass clazz,
                                                            jintArray pixels, jint width,
                                                            jint height) {
    jint *pixels_ = (*env)->GetIntArrayElements(env, pixels, NULL);

    unsigned char * colors = (unsigned char*)pixels_;
    int pixelCount = height*width*4;
    int i = 0;
    while(i<pixelCount){
        if(colors[i+2] > 5)
        {
            colors[i+2] -= 4;
        }

        i+=4;
    }
    (*env)->ReleaseIntArrayElements(env, pixels, pixels_, 0);
}

JNIEXPORT void JNICALL
Java_com_example_higallery_activities_EditActivity_WarmUp(JNIEnv *env, jclass clazz,
                                                          jintArray pixels, jint width,
                                                          jint height) {
    jint *pixels_ = (*env)->GetIntArrayElements(env, pixels, NULL);

    unsigned char * colors = (unsigned char*)pixels_;
    int pixelCount = height*width*4;
    int i = 0;
    while(i<pixelCount){
        if(colors[i+2]<250){
            colors[i+2] += 4;
        }

        i+=4;
    }
    (*env)->ReleaseIntArrayElements(env, pixels, pixels_, 0);
}
