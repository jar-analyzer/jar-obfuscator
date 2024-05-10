.code
; FUNCTION encrypt
encrypt PROC
    ; init
    push rbp
    mov rbp, rsp
    ; save
    push rax
    push rbx
    push rcx
    push rdx
    push rsi
    push rdi
    ; char* str
    mov rdi, rcx
    ; long length
    mov rcx, rdx
    ; rbx = 0
    xor rbx, rbx
link_start:
    ; if rbx >= rcx goto end
    cmp rbx, rcx
    jge magic
    ; al = str[rdi+rbx]
    mov al, byte ptr [rdi+rbx]
    ; al = al - 2
    sub al, 002h
    ; al = al ^ 11h
    xor al, 011h
    ; al = ~al
    not al
    ; al = al + 1
    add al, 001h
    ; al = al ^ 22
    xor al, 022h
    ; str[rdi+rbx] = al
    mov byte ptr [rdi+rbx], al
    ; ebx ++
    inc rbx
    ; loop
    jmp link_start
magic:
    ; magic
    mov al, 0CAh
    mov byte ptr [rdi+000h], al
    mov al, 0FEh
    mov byte ptr [rdi+001h], al
    mov al, 0BAh
    mov byte ptr [rdi+002h], al
    mov al, 0BEh
    mov byte ptr [rdi+003h], al
    ; signature
    mov rsi, rcx
    sub rsi, 001h
    mov al, byte ptr [rdi+rsi]
    mov ah, byte ptr [rdi+004h]
    mov byte ptr [rdi+004h], al
    mov byte ptr [rdi+rsi], ah
    ; reset
    xor ah, ah
    xor al, al
    xor rsi, rsi
    ; recover
    pop rdi
    pop rsi
    pop rdx
    pop rcx
    pop rbx
    pop rax
    ; recover rbp
    pop rbp
    ret
encrypt ENDP

end
