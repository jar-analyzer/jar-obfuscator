section .text
global decrypt

decrypt:
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
    ; char* str -> rdi
    mov rdi, rdi
    ; long length -> rsi
    mov rsi, rsi
    mov rcx, rsi
    ; rbx = 0
    xor rbx, rbx
    ; rbx = rbx + 4
    add rbx, 004h
    ; signature
    mov rsi, rcx
    sub rsi, 001h
    mov al, byte [rdi+rsi]
    mov ah, byte [rdi+004h]
    mov byte [rdi+004h], al
    mov byte [rdi+rsi], ah
    ; reset
    xor ah, ah
    xor al, al
    xor rsi, rsi
link_start:
    ; if ebx >= ecx goto end
    cmp rbx, rcx
    jge magic
    ; al = str[rdi+rbx]
    mov al, byte [rdi+rbx]
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
    mov byte [rdi+rbx], al
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
