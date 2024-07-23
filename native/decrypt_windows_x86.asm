.386
.model flat, c
option casemap:none

.code

; FUNCTION decrypt
decrypt PROC USES edi esi ebx, pStr:DWORD, strLength:DWORD
    ; char* str
    mov edi, pStr
    ; long length
    mov ecx, strLength
    ; ebx = 0
    xor ebx, ebx
    ; ebx = ebx + 4
    add ebx, 004h
    ; signature
    mov esi, ecx
    sub esi, 001h
    mov al, byte ptr [edi+esi]
    mov ah, byte ptr [edi+004h]
    mov byte ptr [edi+004h], al
    mov byte ptr [edi+esi], ah
    ; reset
    xor ah, ah
    xor al, al
    xor esi, esi
link_start:
    ; if ebx >= ecx goto end
    cmp ebx, ecx
    jge magic
    ; al = str[edi+ebx]
    mov al, byte ptr [edi+ebx]
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
    ; str[edi+ebx] = al
    mov byte ptr [edi+ebx], al
    ; ebx ++
    inc ebx
    ; loop
    jmp link_start
magic:
    ret
decrypt ENDP

END