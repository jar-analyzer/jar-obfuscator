#ifndef NATIVE_CORE_EN_H
#define NATIVE_CORE_EN_H

#define EN_LOG(msg) printf("[ENCRYPT] %s\n", msg)

// SEE encrypt_windows.asm
extern void encrypt(unsigned char *, long);

#endif //NATIVE_CORE_EN_H