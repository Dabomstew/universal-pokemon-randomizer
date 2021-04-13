    .nds
    .thumb
    .open "pkmnheartgold.bin", "pkmnheartgold_catching_tutorialfix.bin", 0x02000000

    NEW_CATCHING_TUT_SUBR_HOOK equ 0x02051B68
    OLD_CATCHING_TUT_SUBR_CONTINUED equ 0x02051BB0
    
    ALLOCATE_POKEMON_MEMORY equ 0x0206DD2C
    INSTANTIATE_POKEMON equ 0x0206DE38
    ADD_POKEMON_TO_PARTY equ 0x02074524
    FREE_MEMORY equ 0x0201AB0C

    ITCM_SRC_START equ 0x02111860
    ITCM_DEST_START equ 0x01FF8000
    ITCM_OLD_SIZE equ 0x620

    NEW_CATCHING_TUT_SUBR equ ITCM_SRC_START + ITCM_OLD_SIZE
    NEW_CATCHING_TUT_SUBR_ITCM equ ITCM_DEST_START + ITCM_OLD_SIZE
    BL_OFFSET equ (NEW_CATCHING_TUT_SUBR) - (NEW_CATCHING_TUT_SUBR_ITCM)

    ; Hook that jumps to our new subroutine
    .org    NEW_CATCHING_TUT_SUBR_HOOK
    mov     r1, r4
    bl      org() + 6
    b       OLD_CATCHING_TUT_SUBR_CONTINUED
    ldr     r2,=#(NEW_CATCHING_TUT_SUBR_ITCM + 1)
    bx      r2
    .pool

    ; New subroutine for setting up the player/enemy parties for the catching tutorial.
    ; Most of this is copied from the original assembly, but modified to allow easy modification of
    ; Marill/Rattata's species and Rattata's level. Marill is also level 10 to mitigate softocks.
    ; r0 is param_1 from the original function (used as a parameter for some sort of allocator)
    ; r1 is a pointer to a struct that stores the player and enemy parties
    .org NEW_CATCHING_TUT_SUBR
    .area 92

    push    { r4-r6, lr }
    sub     sp, #0x10
    mov     r4, r1
    bl      BL_OFFSET + ALLOCATE_POKEMON_MEMORY
    mov     r2, #0x0
    str     r2, [sp]
    str     r2, [sp, #0x4]
    mov     r1, #0x2
    str     r1, [sp, #0x8]
    str     r2, [sp, #0xC]
    ldr     r1,=#0xB7              ; Marill
    mov     r2, #0xA               ; Level 10
    mov     r3, #0x20
    add     r6, r0, #0x0
    bl      BL_OFFSET + INSTANTIATE_POKEMON
    ldr     r0, [r4, #0x4]         ; Pointer to player party
    add     r1, r6, #0x0
    bl      BL_OFFSET + ADD_POKEMON_TO_PARTY
    mov     r0, #0x0
    str     r0, [sp]
    str     r0, [sp, #0x4]
    mov     r2, #0x2
    str     r2, [sp, #0x8]
    str     r0, [sp, #0xC]
    add     r0, r6, #0x0
    ldr     r1,=#0x13              ; Rattata
    mov     r2, #0x2               ; Level 2
    mov     r3, #0x20
    bl      BL_OFFSET + INSTANTIATE_POKEMON
    ldr     r0, [r4, #0x8]         ; Pointer to enemy party
    add     r1, r6, #0x0
    bl      BL_OFFSET + ADD_POKEMON_TO_PARTY
    add     r0, r6, #0x0
    bl      BL_OFFSET + FREE_MEMORY
    add     sp, #0x10
    pop     { r4-r6, pc }
    .pool
    .endarea

    .close