    .gba
    .thumb
    .open "pokefirered.gba", "pokefirered_marowak.gba", 0x08000000

    START_MAROWAK_BATTLE_HOOK_ADDR equ 0x0807F92A
    START_MAROWAK_BATTLE_CONTINUED equ 0x0807F940
    CREATE_MON_WITH_GENDER_NATURE_LETTER equ 0x0803DE00
    NEW_SUBR_ADDR equ 0x08A80000

    ; Hook that jumps to our new subroutine
    .org START_MAROWAK_BATTLE_HOOK_ADDR
    bl      org() + 6
    b       START_MAROWAK_BATTLE_CONTINUED
    ldr     r2,=#(NEW_SUBR_ADDR + 1)
    bx      r2
    .pool

    ; New subroutine that can call CreateMonWithGenderNatureLetter with a pc-relative loaded constant
    ; Note that this function is out of range of using bl, so we call it with an alternate method
    ; Also note that r0 is a parameter; it's a pointer to gEnemyParty loaded by StarMarowakBattle
    .org NEW_SUBR_ADDR
    .area 46

    push    { r4, lr }
    sub     sp, #0xC
    mov     r1, #0xFE              ; Female
    str     r1, [sp, #0x0]
    mov     r1, #0xC               ; Serious Nature
    str     r1, [sp, #0x4]
    mov     r1, #0x0               ; Random Unown Letter
    str     r1, [sp, #0x8]
    ldr     r1,=#0x69              ; Marowak
    mov     r2, #0x1E              ; Level 30
    mov     r3, #0x1F              ; All IVs are 31
    bl      org() + 6
    b       org() + 6
    ldr     r4,=#(CREATE_MON_WITH_GENDER_NATURE_LETTER + 1)
    bx      r4
    add     sp, #0xC
    pop     { r4, pc }
    .pool
    .endarea

    .close