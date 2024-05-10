#include <string.h>
#include <stdlib.h>

#include "jni_encryptor.h"
#include "xxtea_en.h"
#include "core_en.h"

void internal(unsigned char *chars, int start, unsigned char *key) {
    unsigned char first[4];
    for (int i = start; i < start + 4; i++) {
        first[i - start] = chars[i];
    }
    unsigned char second[4];
    for (int i = start + 4; i < start + 8; i++) {
        second[i - start - 4] = chars[i];
    }
    uint32_t v[2] = {convert(first), convert(second)};

    unsigned char *key_part1 = key;
    unsigned char *key_part2 = key + 4;
    unsigned char *key_part3 = key + 8;
    unsigned char *key_part4 = key + 12;

    uint32_t const k[4] = {
            (unsigned int) convert(key_part1),
            (unsigned int) convert(key_part2),
            (unsigned int) convert(key_part3),
            (unsigned int) convert(key_part4),
    };

    tea_encrypt(v, k);
    unsigned char first_arr[4];
    unsigned char second_arr[4];
    revert(v[0], first_arr);
    revert(v[1], second_arr);
    for (int i = start; i < start + 4; i++) {
        chars[i] = first_arr[i - start];
    }
    for (int i = start + 4; i < start + 8; i++) {
        chars[i] = second_arr[i - start - 4];
    }
}

// ClassFile {
//    u4             magic; (ignore)
//    u2             minor_version; (ignore)
//    u2             major_version; (ignore)
//    u2             constant_pool_count; (ignore)
//    cp_info        constant_pool[constant_pool_count-1];
//    ...
// }
// start index: 4+2+2+2=10
// 1. asm encrypt
// 2. tea encrypt: {[10:14],[14:18]} {[18:22],[22:26]} {[26:30],[30:34]}
JNIEXPORT jbyteArray JNICALL Java_me_n1ar4_jar_obfuscator_jvmti_CodeEncryptor_encrypt
        (JNIEnv *env, jclass cls, jbyteArray text, jint length, jbyteArray key) {
    jbyte *data = (*env)->GetByteArrayElements(env, text, NULL);
    unsigned char *chars = (unsigned char *) malloc(length);
    memcpy(chars, data, length);

    // 1. asm encrypt
    encrypt(chars, length);
    EN_LOG("ASM ENCRYPT FINISH");

    // 2. tea encrypt
    if (length < 18) {
        EN_LOG("ERROR: BYTE CODE TOO SHORT");
        return text;
    }

    jbyte *j_tea_key = (*env)->GetByteArrayElements(env, key, NULL);
    unsigned char *tea_key = (unsigned char *) malloc(16);
    memcpy(tea_key, j_tea_key, 16);
    printf("KEY: %s\n", tea_key);

    EN_LOG("ALL TEA ENCRYPT");
    int total = (length - 10) / 8;
    for (int i = 0; i < total; i++) {
        internal(chars, 10 + i * 8, tea_key);
    }

    (*env)->SetByteArrayRegion(env, text, 0, length, (jbyte *) chars);
    return text;
}