section .text
global encrypt

encrypt:
    ; init
    push rbp
    mov rbp, rsp

    push rax
    push rbx
    push rcx
    push rdx
    push rsi
    push rdi

    ; char* str
    mov rdi, rdi
    ; long length
    mov rcx, rsi
    ; rbx = 0
    xor rbx, rbx
link_start:
    ; if rbx >= rcx goto end
    cmp rbx, rcx
    jge magic
    ; al = str[rdi+rbx]
    mov al, byte [rdi+rbx]
    ; al = al - 2
    sub al, 0x02
    ; al = al ^ 11h
    xor al, 0x11
    ; al = ~al
    not al
    ; al = al + 1
    add al, 0x01
    ; al = al ^ 22
    xor al, 0x22
    ; str[rdi+rbx] = al
    mov byte [rdi+rbx], al
    ; ebx ++
    inc rbx
    ; loop
    jmp link_start
magic:
    ; magic
    mov al, 0xca
    mov byte [rdi+0x00], al
    mov al, 0xfe
    mov byte [rdi+0x01], al
    mov al, 0xba
    mov byte [rdi+0x02], al
    mov al, 0xbe
    mov byte [rdi+0x03], al
    ; signature
    mov rsi, rcx
    sub rsi, 0x01
    mov al, byte [rdi+rsi]
    mov ah, byte [rdi+0x04]
    mov byte [rdi+0x04], al
    mov byte [rdi+rsi], ah
    ; reset
    xor ah, ah
    xor al, al
    xor rsi, rsi

    pop rdi
    pop rsi
    pop rdx
    pop rcx
    pop rbx
    pop rax

    ; recover rbp
    pop rbp
    ret
