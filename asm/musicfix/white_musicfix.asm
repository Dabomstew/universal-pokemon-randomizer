    .nds
    .thumb
    .open "pkmnwhite_ovl21", "pkmnwhite_ovl21_musicfix", 0x02187F20

    NEW_INDEX_TO_MUSIC_SUBR_HOOK equ 0x021CF60C
    OLD_INDEX_TO_MUSIC_SUBR_CONTINUED equ 0x021CF6A8
    
    ITCM_SRC_START equ 0x020A9EA0
    ITCM_DEST_START equ 0x01FF8000
    ITCM_OLD_SIZE equ 0x820
    
    NEW_INDEX_TO_MUSIC_SUBR equ ITCM_SRC_START + ITCM_OLD_SIZE
    NEW_INDEX_TO_MUSIC_SUBR_ITCM equ ITCM_DEST_START + ITCM_OLD_SIZE
    
    ; Hook that jumps to our new subroutine
    .org NEW_INDEX_TO_MUSIC_SUBR_HOOK
    mov     r0,r6
    bl      org() + 6
    b       @@continue_old_subr
    ldr     r2,=#(NEW_INDEX_TO_MUSIC_SUBR_ITCM + 1)
    bx      r2
    .pool
@@continue_old_subr:
    cmp     r0,#0x0
    beq     @@subr_end
    add     sp,#0xC
    pop     {r4-r7,r15}
@@subr_end:
    b       OLD_INDEX_TO_MUSIC_SUBR_CONTINUED
    
    .close
    
    .open "pkmnwhite.bin", "pkmnwhite_musicfix.bin", 0x02004000
    ; New subroutine. Checks each relevant index one-by-one and picks the correct music+intro
    .org NEW_INDEX_TO_MUSIC_SUBR
    .area 252
    
    push    r14
    ldr     r2,=#0x1EE              ; Victini
    cmp     r0,r2
    beq     @@music_victini 
    ldr     r2,=#0x23B              ; Zoroark
    cmp     r0,r2
    beq     @@music_zoroark
    ldr     r2,=#0x27D              ; Volcarona
    cmp     r0,r2
    beq     @@music_volcarona
    ldr     r2,=#0x27E              ; Cobalion
    cmp     r0,r2
    beq     @@music_legendary
    ldr     r2,=#0x27F              ; Terrakion
    cmp     r0,r2
    beq     @@music_legendary
    ldr     r2,=#0x280              ; Virizion
    cmp     r0,r2
    beq     @@music_legendary
    ldr     r2,=#0x281              ; Tornadus
    cmp     r0,r2
    beq     @@music_genies
    ldr     r2,=#0x282              ; Thundurus
    cmp     r0,r2
    beq     @@music_genies
    ldr     r2,=#0x283              ; Reshiram
    cmp     r0,r2
    beq     @@music_reshiram
    ldr     r2,=#0x284              ; Zekrom
    cmp     r0,r2
    beq     @@music_zekrom
    ldr     r2,=#0x285              ; Landorus
    cmp     r0,r2
    beq     @@music_genies
    ldr     r2,=#0x286              ; Kyurem
    cmp     r0,r2
    beq     @@music_kyurem
    ldr     r2,=#0x287              ; Keldeo
    cmp     r0,r2
    beq     @@music_legendary
@@music_regular:
    mov     r0,#0x0
    b       @@subr_end
@@music_victini:
    ldr     r0,=#0x478
    strh    r0,[r4]
    mov     r0,#0x1F
    str     r0,[r5]
    b       @@return_1
@@music_zoroark:
    ldr     r0,=#0x469
    strh    r0,[r4]
    mov     r0,#0x23
    str     r0,[r5]
    b       @@return_1
@@music_volcarona:
    ldr     r0,=#0x468
    strh    r0,[r4]
    mov     r0,#0x1F
    str     r0,[r5]
    b       @@return_1
@@music_legendary:
    ldr     r0,=#0x478
    strh    r0,[r4]
    mov     r0,#0x22
    str     r0,[r5]
    b       @@return_1
@@music_genies:
    ldr     r0,=#0x477
    strh    r0,[r4]
    mov     r0,#0x21
    str     r0,[r5]
    b       @@return_1
@@music_reshiram:
    ldr     r0,=#0x474
    strh    r0,[r4]
    mov     r0,#0x20
    str     r0,[r5]
    b       @@return_1
@@music_zekrom:
    ldr     r0,=#0x475
    strh    r0,[r4]
    mov     r0,#0x20
    str     r0,[r5]
    b       @@return_1
@@music_kyurem:
    ldr     r0,=#0x476
    strh    r0,[r4]
    mov     r0,#0x20
    str     r0,[r5]
    b       @@return_1
@@return_1:
    mov     r0,#0x1
@@subr_end:
    pop     r1
    bx      r1

    .pool
    .endarea
    
    .close