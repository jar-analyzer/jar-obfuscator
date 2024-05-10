cmake_minimum_required(VERSION 3.25)

################ JNI CONFIG START ################

project(native C)

set(CMAKE_C_STANDARD 11)

# INCLUDE JNI

find_package(JNI REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})

################ JNI CONFIG END ##################

if (WIN32)
    MESSAGE("BUILD WINDOWS")

    enable_language(ASM_MASM)

    add_custom_target(
            native_encrypt_asm ALL
            COMMAND ml64 /c /Fo${CMAKE_CURRENT_BINARY_DIR}/native_encrypt_asm.obj
            ${CMAKE_CURRENT_SOURCE_DIR}/encrypt_windows.asm
            BYPRODUCTS ${CMAKE_CURRENT_BINARY_DIR}/native_encrypt_asm.obj
            WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
    )

    add_custom_target(
            native_decrypt_asm ALL
            COMMAND ml64 /c /Fo${CMAKE_CURRENT_BINARY_DIR}/native_decrypt_asm.obj
            ${CMAKE_CURRENT_SOURCE_DIR}/decrypt_windows.asm
            BYPRODUCTS ${CMAKE_CURRENT_BINARY_DIR}/native_decrypt_asm.obj
            WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
    )

    add_library(
            encryptor SHARED
            core_en.h
            jni_encryptor.h
            jni_encryptor.c

            xxtea_common.c
            xxtea_common.h
            xxtea_en.h
            xxtea_de.h
            xxtea.c
    )

    add_library(
            decrypter SHARED
            core_de.h
            start_windows.c

            xxtea_common.c
            xxtea_common.h
            xxtea_en.h
            xxtea_de.h
            xxtea.c
    )

    set_property(TARGET native_encrypt_asm PROPERTY
            MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>"
    )

    set_property(TARGET native_decrypt_asm PROPERTY
            MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>"
    )

    add_dependencies(encryptor native_encrypt_asm)

    add_dependencies(decrypter native_decrypt_asm)

    target_link_libraries(encryptor
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/native_encrypt_asm.obj
    )

    target_link_libraries(decrypter
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/native_decrypt_asm.obj
    )

    add_executable(
            xxtea_test
            xxtea_common.h
            xxtea_common.c
            xxtea_en.h
            xxtea_de.h
            xxtea.c
            xxtea_test.c
    )

    add_executable(
            decrypt_test
            core_de.h
            core_en.h
            decrypt_test.c
    )

    target_link_libraries(decrypt_test
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/native_decrypt_asm.obj
    )

    target_link_libraries(decrypt_test
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/native_encrypt_asm.obj
    )

else ()
    MESSAGE("BUILD LINUX")

    enable_language(ASM_NASM)

    add_custom_target(
            native_encrypt_asm ALL
            COMMAND nasm -f elf64 ${CMAKE_CURRENT_SOURCE_DIR}/encrypt_linux.asm
            -o ${CMAKE_CURRENT_BINARY_DIR}/encrypt_linux.obj
            BYPRODUCTS ${CMAKE_CURRENT_BINARY_DIR}/encrypt_linux.obj
            WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
    )

    add_custom_target(
            native_decrypt_asm ALL
            COMMAND nasm -f elf64 ${CMAKE_CURRENT_SOURCE_DIR}/decrypt_linux.asm
            -o ${CMAKE_CURRENT_BINARY_DIR}/decrypt_linux.obj
            BYPRODUCTS ${CMAKE_CURRENT_BINARY_DIR}/decrypt_linux.obj
            WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
    )

    MESSAGE("BUILD ENCRYPT DECRYPT ASM FINISH")

    add_library(
            encryptor SHARED
            core_en.h
            jni_encryptor.h
            jni_encryptor.c

            xxtea_common.c
            xxtea_common.h
            xxtea_en.h
            xxtea_de.h
            xxtea.c
    )

    add_library(
            decrypter SHARED
            core_de.h
            start_linux.c

            xxtea_common.c
            xxtea_common.h
            xxtea_en.h
            xxtea_de.h
            xxtea.c
    )

    add_dependencies(encryptor native_encrypt_asm)

    add_dependencies(decrypter native_decrypt_asm)

    target_link_libraries(encryptor
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/encrypt_linux.obj
    )

    target_link_libraries(decrypter
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/decrypt_linux.obj
    )

    add_executable(
            xxtea_test
            xxtea_common.h
            xxtea_common.c
            xxtea_en.h
            xxtea_de.h
            xxtea.c
            xxtea_test.c
    )

    add_executable(
            decrypt_test
            core_de.h
            core_en.h
            decrypt_test.c
    )

    target_link_libraries(decrypt_test
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/decrypt_linux.obj
    )

    target_link_libraries(decrypt_test
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/encrypt_linux.obj
    )

endif ()
