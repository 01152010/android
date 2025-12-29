#include <jni.h>
#include "core/core.h"
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_core_Protocol_send55BB(JNIEnv *env, jobject thiz, jbyte address, jbyteArray cmd,
                                  jbyteArray data) {
    // TODO: implement config55BB()
    int cmdLen = (int)env->GetArrayLength(cmd);
    jbyte* pCmd = new jbyte[cmdLen];
    env->GetByteArrayRegion(cmd,0,cmdLen,pCmd);

    int dataLen = (int)env->GetArrayLength(data);
    jbyte* pData = new jbyte[dataLen];
    env->GetByteArrayRegion(data,0,dataLen,pData);

    char* pResultChar = send((char)0x55, (char)0xBB, address, cmdLen,
                               reinterpret_cast<char *>(pCmd), dataLen,
                               reinterpret_cast<char *>(pData));
    jbyteArray jbArray = env->NewByteArray(2+1+1+cmdLen+dataLen+1);
    env->SetByteArrayRegion(jbArray, 0, 2+1+1+cmdLen+dataLen+1,
                            reinterpret_cast<const jbyte *>(pResultChar));
    delete pCmd;
    delete pData;
    delete pResultChar;
    return jbArray;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_core_Protocol_send55CC(JNIEnv *env, jobject thiz, jbyte address, jbyteArray cmd,
                                         jbyteArray data) {
    // TODO: implement config55CC()
    int cmdLen = (int)env->GetArrayLength(cmd);
    auto pCmd = new jbyte[cmdLen];
    env->GetByteArrayRegion(cmd,0,cmdLen,pCmd);
    int dataLen = (int)env->GetArrayLength(data);
    auto pData = new jbyte[dataLen];
    env->GetByteArrayRegion(data,0,dataLen,pData);

    char* pResultChar = send((char)0x55, (char)0xCC, address, cmdLen,
                             reinterpret_cast<char *>(pCmd), dataLen,
                             reinterpret_cast<char *>(pData));
    jbyteArray jbArray = env->NewByteArray(2+1+1+cmdLen+dataLen+1);
    env->SetByteArrayRegion(jbArray, 0, 2+1+1+cmdLen+dataLen+1,
                            reinterpret_cast<const jbyte *>(pResultChar));
    delete[] pCmd;
    delete[] pData;
    delete[] pResultChar;
    return jbArray;
}
