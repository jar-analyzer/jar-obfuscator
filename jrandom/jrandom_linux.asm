section .text
global rd_rand_supported
global get_rand_int

rd_rand_supported:
    mov eax, 1
    cpuid
    bt ecx, 30
    jnc not_supported
    mov eax, 1
    ret

not_supported:
    mov eax, 0
    ret

get_rand_int:
    test rdi, rdi
    je fail
    rdrand eax
    jc success

fail:
    xor eax, eax
    ret

success:
    mov [rdi], eax
    mov eax, 1
    ret
