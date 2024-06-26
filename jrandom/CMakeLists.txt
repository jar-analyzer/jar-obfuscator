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
            jrandom_asm ALL
            COMMAND ml64 /c /Fo${CMAKE_CURRENT_BINARY_DIR}/jrandom_asm.obj
            ${CMAKE_CURRENT_SOURCE_DIR}/jrandom_windows.asm
            BYPRODUCTS ${CMAKE_CURRENT_BINARY_DIR}/jrandom_asm.obj
            WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
    )

    add_library(
            jrandom SHARED
            jni_jrandom.h
            jni_jrandom.c
    )

    set_property(TARGET jrandom_asm PROPERTY
            MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>"
    )

    add_dependencies(jrandom jrandom_asm)

    target_link_libraries(jrandom
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/jrandom_asm.obj
    )
else ()
    MESSAGE("BUILD LINUX")

    enable_language(ASM_NASM)

    add_custom_target(
            jrandom_asm ALL
            COMMAND nasm -f elf64 ${CMAKE_CURRENT_SOURCE_DIR}/jrandom_linux.asm
            -o ${CMAKE_CURRENT_BINARY_DIR}/jrandom_asm.obj
            BYPRODUCTS ${CMAKE_CURRENT_BINARY_DIR}/jrandom_asm.obj
            WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
    )

    add_library(
            jrandom SHARED
            jni_jrandom.h
            jni_jrandom.c
    )

    add_dependencies(jrandom jrandom_asm)

    target_link_libraries(jrandom
            PRIVATE ${CMAKE_CURRENT_BINARY_DIR}/jrandom_asm.obj
    )
endif ()