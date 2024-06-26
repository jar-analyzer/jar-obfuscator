.code
rd_rand_supported PROC
    mov eax, 1
    cpuid
    bt ecx, 30
    jnc not_supported
    mov eax, 1
    ret
not_supported:
    mov eax, 0
    ret
rd_rand_supported ENDP

get_rand_int PROC
    mov rdi, rdi
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
get_rand_int ENDP

END
