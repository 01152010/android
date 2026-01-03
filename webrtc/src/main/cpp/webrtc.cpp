#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_webrtc_Utils_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

int add(int i,int j){
    return i+j;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_webrtc_Utils_add(JNIEnv *env, jobject thiz, jint i, jint j) {
    return  add(i,j);
}