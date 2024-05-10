.code
; FUNCTION decrypt
decrypt PROC
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
    ; rbx = rbx + 4
    add rbx, 004h
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
link_start:
    ; if ebx >= ecx goto end
    cmp rbx, rcx
    jge magic
    ; al = str[rdi+rbx]
    mov al, byte ptr [rdi+rbx]
    ; al = al ^ 22
    xor al, 022h
    ; al = al -1
    sub al, 001h
    ; al = ~al
    not al
    ; al = al ^ 11h
    xor al, 011h
    ; al = al + 2
    add al, 002h
    ; str[rdi+rbx] = al
    mov byte ptr [rdi+rbx], al
    ; ebx ++
    inc ebx
    ; loop
    jmp link_start
magic:
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
decrypt ENDP

end
