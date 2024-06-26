#include <jni.h>

#ifndef _Included_me_n1ar4_jrandom_core_JRandom
#define _Included_me_n1ar4_jrandom_core_JRandom
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_me_n1ar4_jrandom_core_JRandom_checkRDRAND
        (JNIEnv *, jclass);

JNIEXPORT jlong JNICALL Java_me_n1ar4_jrandom_core_JRandom_getRandInt
        (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
