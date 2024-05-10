#include "xxtea_common.h"

unsigned int convert(const unsigned char *arr) {
    unsigned int result = 0;
    int i;
    for (i = 0; i < 4; i++) {
        result |= (arr[i] << (8 * i));
    }
    return result;
}

void revert(unsigned int value, unsigned char *arr) {
    for (int i = 0; i < 4; i++) {
        arr[i] = (value >> (8 * i)) & 0xFF;
    }
}