#ifndef NATIVE_CORE_DE_H
#define NATIVE_CORE_DE_H

#define DE_LOG(msg) printf("[JVMTI-LOG] %s\n", msg)

// SEE decrypt_windows.asm
extern void decrypt(unsigned char *, long);

#endif //NATIVE_CORE_DE_H