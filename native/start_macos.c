#include <mach-o/dyld.h>
#include <mach-o/nlist.h>
#include <jvmti.h>
#include <stdlib.h>
#include <string.h>

#include "xxtea_de.h"
#include "core_de.h"

// PACKAGE
char *PACKAGE_NAME;
// KEY
char *KEY;

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

void internal(unsigned char *_data, int start) {
    unsigned char first[4];
    for (int i = start; i < start + 4; i++) {
        first[i - start] = _data[i];
    }
    unsigned char second[4];
    for (int i = start + 4; i < start + 8; i++) {
        second[i - start - 4] = _data[i];
    }
    uint32_t v[2] = {convert(first), convert(second)};

    unsigned char *key_part1 = (unsigned char *) KEY;
    unsigned char *key_part2 = (unsigned char *) KEY + 4;
    unsigned char *key_part3 = (unsigned char *) KEY + 8;
    unsigned char *key_part4 = (unsigned char *) KEY + 12;

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
            internal(_data, 10 + i * 8);
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
    strncpy(modified_str, options, modified_str_size - 1);
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
            PACKAGE_NAME = (char *) malloc(strlen((char *) v1) + 1);
            strcpy(PACKAGE_NAME, (char *) v1);
        } else {
            printf("ERROR");
            return 0;
        }

        tokens = split_string((char *) key, &num_tokens, "=");
        if (strcmp((char *) tokens[0], "KEY") == 0) {
            v2 = tokens[1];
            KEY = (char *) malloc(strlen((char *) v2) + 1);
            strcpy(KEY, (char *) v2);
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

    void *gHotSpotVMStructs;
    for (uint32_t i = 0; i < _dyld_image_count(); i++) {
        const char* image_name = _dyld_get_image_name(i);
        if (strstr(image_name, "libjvm.dylib") != NULL) {
            const struct mach_header_64* header = (const struct mach_header_64*)_dyld_get_image_header(i);
            const char* image_base = (const char*)header;
            const struct load_command* cmd = (const struct load_command*)(image_base + sizeof(struct mach_header_64));
            
            uint64_t extraSize = 0;

            for (uint32_t j = 0; j < header->ncmds; j++) {
                if (cmd->cmd == LC_SEGMENT_64) {
                    const struct segment_command_64 *segmentCmd = (const struct segment_command_64 *) cmd;
                    if (strcmp(segmentCmd->segname, SEG_DATA) == 0) {
                        extraSize = segmentCmd->vmsize - segmentCmd->filesize;
                    }
                } else if (cmd->cmd == LC_SYMTAB) {
                    const struct symtab_command* symtab = (const struct symtab_command*)cmd;
                    const struct nlist_64* symbols = (const struct nlist_64*)(image_base + symtab->symoff + extraSize);
                    const char* strtab = image_base + symtab->stroff + extraSize;

                    for (uint32_t k = 0; k < symtab->nsyms; k++) {
                        uint32_t strx = symbols[k].n_un.n_strx;
                        if (strcmp("_gHotSpotVMStructs", strtab + symbols[k].n_un.n_strx) == 0) {
                            if (symbols[k].n_value != 0) {
                                gHotSpotVMStructs = (void*)(symbols[k].n_value + _dyld_get_image_vmaddr_slide(i));
                                // printf("gHotSpotVMStructs=%p\n", gHotSpotVMStructs);
                                *(size_t *) gHotSpotVMStructs = 0;
                                DE_LOG("HACK JVM FINISH");
                                return JNI_OK;
                            }
                        }
                    }
                }
                cmd = (const struct load_command*)((const char*)cmd + cmd->cmdsize);
            }
        }
    }

    printf("ERROR: HACK JVM FAILED!\n");
    return 1;
}