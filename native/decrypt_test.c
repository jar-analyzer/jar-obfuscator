#include <stdio.h>

#include "core_de.h"
#include "core_en.h"

void printHex(const unsigned char *arr, int length) {
    for (int i = 0; i < length; i++) {
        printf("%02X", arr[i]);
    }
    printf("\n");
}

int main() {
    unsigned char code[12] = {
            0xca, 0xfe, 0xba, 0xbe,
            0x00, 0x00, 0x00, 0x05,
            0x01, 0x02, 0x03, 0x04,
    };
    encrypt(code, 12);
    printHex(code, 12);
    decrypt(code, 12);
    printHex(code, 12);
}