    .gba
    .thumb
    .open "pokeruby.gba", "pokeruby_firstbattle.gba", 0x08000000

    SET_UP_BATTLE_VARS_AND_BIRCH_POOCHYENA_HOOK_ADDR equ 0x0800B8EA
    SET_UP_BATTLE_VARS_AND_BIRCH_POOCHYENA_CONTINUED equ 0x0800B90E
    CREATE_MON equ 0x0803A798
    SET_MON_DATA equ 0x0803D1FC
    NEW_SUBR_ADDR equ 0x08FE0000

    ; Hook that jumps to our new subroutine
    .org SET_UP_BATTLE_VARS_AND_BIRCH_POOCHYENA_HOOK_ADDR
    bl      org() + 6
    b       SET_UP_BATTLE_VARS_AND_BIRCH_POOCHYENA_CONTINUED
    ldr     r2,=#(NEW_SUBR_ADDR + 1)
    bx      r2
    .pool

    ; New subroutine that can call CreateMon and SetMonData with a pc-relative loaded constant
    ; Note that these functions are out of range of using bl, so we call them with an alternate method
    .org NEW_SUBR_ADDR
    .area 72

    push    { r4-r6, lr }
    sub     sp, #0x14
    ldr     r4,=#0x030045C0
    ldr     r1,=#0x11E         ; Poochyena
    mov     r2, #0x2           ; Level 2
    mov     r5, #0x0
    str     r5, [sp]
    str     r5, [sp, #0x4]
    str     r5, [sp, #0x8]
    str     r5, [sp, #0xC]
    add     r0, r4, #0x0
    mov     r3, #0x20
    bl      org() + 6
    b       org() + 6
    ldr     r6,=#(CREATE_MON + 1)
    bx      r6
    str     r5, [sp, #0x10]
    add     r0, r4, #0x0
    mov     r1, #0xC
    add     r2, sp, #0x10
    bl      org() + 6
    b       org() + 6
    ldr     r6,=#(SET_MON_DATA + 1)
    bx      r6
    add     sp, #0x14
    pop    { r4-r6, pc }
    .pool
    .endarea

    .close