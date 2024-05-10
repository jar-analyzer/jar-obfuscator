#ifndef NATIVE_XXTEA_DE_H
#define NATIVE_XXTEA_DE_H

#include <stdio.h>
#include <stdint.h>

#include "xxtea_common.h"

void tea_decrypt(uint32_t *v, const uint32_t *k);

#endif //NATIVE_XXTEA_DE_H