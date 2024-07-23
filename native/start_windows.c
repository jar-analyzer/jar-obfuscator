#include <jvmti.h>
#include <windows.h>
#include <stdlib.h>
#include <string.h>

#include "xxtea_de.h"
#include "core_de.h"

// PACKAGE
char *PACKAGE_NAME;
// KEY
unsigned char *KEY;

unsigned char **split_string(const char *str, int *num_tokens, const char *sp) {
    unsigned char **tokens = NULL;
    char copy[100];
    char *token;
    int count = 0;

    strncpy(copy, str, sizeof(copy) - 1);
    copy[sizeof(copy) - 1] = '\0';

    token = strtok(copy, sp);
    while (token != NULL) {
        count++;
        token = strtok(NULL, sp);
    }

    tokens = (unsigned char **) malloc(count * sizeof(unsigned char *));
    if (tokens == NULL) {
        fprintf(stderr, "memory allocation failed\n");
        return NULL;
    }

    strncpy(copy, str, sizeof(copy) - 1);
    copy[sizeof(copy) - 1] = '\0';

    token = strtok(copy, sp);
    count = 0;
    while (token != NULL) {
        tokens[count] = (unsigned char *) malloc(strlen(token) + 1);
        if (tokens[count] == NULL) {
            fprintf(stderr, "memory allocation failed\n");
            for (int i = 0; i < count; i++) {
                free(tokens[i]);
            }
            free(tokens);
            return NULL;
        }
        strncpy((char *) tokens[count], token, strlen(token) + 1);
        count++;
        token = strtok(NULL, sp);
    }

    *num_tokens = count;

    return tokens;
}

void internal(unsigned char *_data, int start, unsigned char *key) {
    unsigned char first[4];
    for (int i = start; i < start + 4; i++) {
        first[i - start] = _data[i];
    }
    unsigned char second[4];
    for (int i = start + 4; i < start + 8; i++) {
        second[i - start - 4] = _data[i];
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

    tea_decrypt(v, k);
    unsigned char first_arr[4];
    unsigned char second_arr[4];
    revert(v[0], first_arr);
    revert(v[1], second_arr);
    for (int i = start; i < start + 4; i++) {
        _data[i] = first_arr[i - start];
    }
    for (int i = start + 4; i < start + 8; i++) {
        _data[i] = second_arr[i - start - 4];
    }
}

// 1. tea encrypt
// 2. asm encrypt
void JNICALL ClassDecryptHook(
        jvmtiEnv *jvmti_env,
        JNIEnv *jni_env,
        jclass class_being_redefined,
        jobject loader,
        const char *name,
        jobject protection_domain,
        jint class_data_len,
        const unsigned char *class_data,
        jint *new_class_data_len,
        unsigned char **new_class_data) {
    *new_class_data_len = class_data_len;
    (*jvmti_env)->Allocate(jvmti_env, class_data_len, new_class_data);
    unsigned char *_data = *new_class_data;
    if (name && strncmp(name, PACKAGE_NAME, strlen(PACKAGE_NAME)) == 0) {
        for (int i = 0; i < class_data_len; i++) {
            _data[i] = class_data[i];
        }
        if (class_data_len < 18) {
            return;
        }

        DE_LOG("START DECRYPT");
        // 1. all xxtea
        int total = (class_data_len - 10) / 8;
        for (int i = 0; i < total; i++) {
            internal(_data, 10 + i * 8, KEY);
        }

        // 2. asm encrypt
        decrypt((unsigned char *) _data, class_data_len);
    } else {
        for (int i = 0; i < class_data_len; i++) {
            _data[i] = class_data[i];
        }
    }
}

JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm) {
    DE_LOG("UNLOAD AGENT");
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
    // REPLACE . -> /
    char modified_str[256];
    size_t modified_str_size = sizeof(modified_str);
    strncpy_s(modified_str, modified_str_size,
              options, modified_str_size - 1);
    modified_str[modified_str_size - 1] = '\0';

    for (size_t i = 0; modified_str[i] != '\0'; ++i) {
        if (modified_str[i] == '.') {
            modified_str[i] = '/';
        }
    }

    unsigned char *v1 = NULL;
    unsigned char *v2 = NULL;
    int num_tokens;
    unsigned char **tokens = split_string(modified_str, &num_tokens, ",");
    if (tokens != NULL) {
        unsigned char *pack = tokens[0];
        unsigned char *key = tokens[1];

        tokens = split_string((char *) pack, &num_tokens, "=");
        if (strcmp((char *) tokens[0], "PACKAGE_NAME") == 0) {
            v1 = tokens[1];
        } else {
            printf("ERROR");
            return 0;
        }

        tokens = split_string((char *) key, &num_tokens, "=");
        if (strcmp((char *) tokens[0], "KEY") == 0) {
            v2 = tokens[1];
        } else {
            printf("ERROR");
            return 0;
        }
    }

    if (v1 == NULL) {
        DE_LOG("NEED PACKAGE_NAME PARAMS\n");
        return 0;
    }

    if (v2 == NULL) {
        DE_LOG("NEED KEY PARAMS\n");
        return 0;
    }

    // SET PACKAGE_NAME
    PACKAGE_NAME = (char *) malloc(strlen((char *) v1) + 1);
    strcpy(PACKAGE_NAME, (char *) v1);

    // SET KEY
    KEY = (unsigned char *) malloc(strlen((char *) v2) + 1);
    strcpy((char *) KEY, (char *) v2);

    jvmtiEnv *jvmti;
    DE_LOG("INIT JVMTI ENVIRONMENT");
    jint ret = (*vm)->GetEnv(vm, (void **) &jvmti, JVMTI_VERSION);
    if (JNI_OK != ret) {
        printf("ERROR: Unable to access JVMTI!\n");
        printf("PLEASE USE JVM VERSION 8");
        return ret;
    }
    DE_LOG("INIT JVMTI CAPABILITIES");
    jvmtiCapabilities capabilities;
    (void) memset(&capabilities, 0, sizeof(capabilities));

    capabilities.can_generate_all_class_hook_events = 1;

    DE_LOG("ADD JVMTI CAPABILITIES");
    jvmtiError error = (*jvmti)->AddCapabilities(jvmti, &capabilities);
    if (JVMTI_ERROR_NONE != error) {
        printf("ERROR: Unable to AddCapabilities JVMTI!\n");
        return error;
    }

    DE_LOG("INIT JVMTI CALLBACKS");
    jvmtiEventCallbacks callbacks;
    (void) memset(&callbacks, 0, sizeof(callbacks));

    DE_LOG("SET JVMTI CLASS FILE LOAD HOOK");
    callbacks.ClassFileLoadHook = &ClassDecryptHook;
    error = (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
    if (JVMTI_ERROR_NONE != error) {
        printf("ERROR: Unable to SetEventCallbacks JVMTI!\n");
        return error;
    }
    DE_LOG("SET EVENT NOTIFICATION MODE");
    error = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL);
    if (JVMTI_ERROR_NONE != error) {
        printf("ERROR: Unable to SetEventNotificationMode JVMTI!\n");
        return error;
    }

    DE_LOG("INIT JVMTI SUCCESS");

    error = (*vm)->GetEnv(vm, (void **) &jvmti, JVMTI_VERSION_1_0);
    if (error != JVMTI_ERROR_NONE) {
        return JNI_ERR;
    }

    HMODULE moduleHandle = LoadLibrary("jvm.dll");
    if (moduleHandle == NULL) {
        return 1;
    }

    FARPROC functionAddress = GetProcAddress(moduleHandle, "gHotSpotVMStructs");
    if (functionAddress == NULL) {
        return 1;
    }

    uintptr_t baseAddress = (uintptr_t) moduleHandle;
    uintptr_t functionRVA = (uintptr_t) functionAddress - baseAddress;

    printf("gHotSpotVMStructs RVA: 0x%016llx\n", functionRVA);
    printf("Function Addr: 0x%016llx\n", (uintptr_t) functionAddress);

    *(size_t *) functionAddress = 0;

    FreeLibrary(moduleHandle);

    DE_LOG("HACK JVM FINISH");

    return JNI_OK;
}