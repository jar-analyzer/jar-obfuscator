#include "jni_jrandom.h"
#include <stdlib.h>

extern int rd_rand_supported(void);

extern int get_rand_int(unsigned int *num);

JNIEXPORT jint JNICALL Java_me_n1ar4_jrandom_core_JRandom_checkRDRAND
        (JNIEnv *env, jclass clazz) {
    if (!rd_rand_supported()) {
        return 0;
    } else {
        return 1;
    }
}

JNIEXPORT jlong JNICALL Java_me_n1ar4_jrandom_core_JRandom_getRandInt
        (JNIEnv *env, jclass clazz) {
    unsigned int *num = calloc(1, sizeof(unsigned int));
    if (num == NULL) {
        return 0;
    }
    if (get_rand_int(num) == 1) {
        jlong result = (jlong) *num;
        free(num);
        return result;
    } else {
        free(num);
        return 0;
    }
}