#include "xxtea_en.h"
#include "xxtea_de.h"

void printHex(const unsigned char *arr, int length) {
    for (int i = 0; i < length; i++) {
        printf("%02X", arr[i]);
    }
    printf("\n");
}

int main() {
    unsigned char data1[] = {0xca, 0xfe, 0xba, 0xbe};
    printHex(data1, 4);
    unsigned char data2[] = {0x01, 0x02, 0x03, 0x04};
    printHex(data2, 4);
    uint32_t v[2] = {convert(data1), convert(data2)};

    // key: Y4Sec-Team-4ra1n
    // 59345365 632D5465 616D2D34 7261316E

    uint32_t const k[4] = {
            (unsigned int) 0x65533459, (unsigned int) 0x65542d63,
            (unsigned int) 0X342d6d61, (unsigned int) 0x6e316172,
    };

    printf("data: 0x%x 0x%x\n", v[0], v[1]);
    tea_encrypt(v, k);
    printf("encrypted: 0x%x 0x%x\n", v[0], v[1]);
    tea_decrypt(v, k);
    printf("decrypted: 0x%x 0x%x\n", v[0], v[1]);

    unsigned char first[4];
    unsigned char second[4];
    revert(v[0], first);
    printHex(first, 4);
    revert(v[1], second);
    printHex(second, 4);
    return 0;
}
