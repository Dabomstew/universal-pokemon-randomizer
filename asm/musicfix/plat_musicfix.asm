    .nds
    .thumb
    .open "pkmnplatinum.bin", "pkmnplatinum_musicfix.bin", 0x02000000

    NEW_INDEX_TO_MUSIC_SUBR_HOOK equ 0x02051CE2
    OLD_INDEX_TO_MUSIC_SUBR_END equ 0x02051D84
    NEW_INDEX_TO_MUSIC_SUBR equ 0x02102380
    NEW_INDEX_TO_MUSIC_SUBR_ICTM equ 0x01FF8660
    
    ; Hook that jumps to our new subroutine
    .org NEW_INDEX_TO_MUSIC_SUBR_HOOK
    mov     r1,r4
    bl      org() + 6
    b       OLD_INDEX_TO_MUSIC_SUBR_END
    ldr     r2,=#(NEW_INDEX_TO_MUSIC_SUBR_ICTM + 1)
    bx      r2
    .pool
    
    ; New subroutine. Checks each relevant index one-by-one and picks the correct music+intro
    .org NEW_INDEX_TO_MUSIC_SUBR
    .area 240
    
    push    r14
    ldr     r2,=#0x90               ; Articuno
    cmp     r0,r2
    beq     @@music_legendary_bird    
    ldr     r2,=#0x91               ; Zapdos
    cmp     r0,r2
    beq     @@music_legendary_bird    
    ldr     r2,=#0x92               ; Moltres
    cmp     r0,r2
    beq     @@music_legendary_bird    
    ldr     r2,=#0x179              ; Regirock
    cmp     r0,r2
    beq     @@music_regi
    ldr     r2,=#0x17A              ; Regice
    cmp     r0,r2
    beq     @@music_regi
    ldr     r2,=#0x17B              ; Registeel
    cmp     r0,r2
    beq     @@music_regi
    ldr     r2,=#0x1DF              ; Rotom
    cmp     r0,r2
    beq     @@music_legendary
    ldr     r2,=#0x1E0              ; Uxie
    cmp     r0,r2
    beq     @@music_lake_trio
    ldr     r2,=#0x1E1              ; Mesprit
    cmp     r0,r2
    beq     @@music_mesprit
    ldr     r2,=#0x1E2              ; Azelf
    cmp     r0,r2
    beq     @@music_lake_trio
    ldr     r2,=#0x1E3              ; Dialga
    cmp     r0,r2
    beq     @@music_dialga_palkia
    ldr     r2,=#0x1E4              ; Palkia
    cmp     r0,r2
    beq     @@music_dialga_palkia
    ldr     r2,=#0x1E5              ; Heatran
    cmp     r0,r2
    beq     @@music_legendary
    ldr     r2,=#0x1E6              ; Regigigas
    cmp     r0,r2
    beq     @@music_legendary
    ldr     r2,=#0x1E7              ; Giratina
    cmp     r0,r2
    beq     @@music_giratina
    ldr     r2,=#0x1E8              ; Cresselia
    cmp     r0,r2
    beq     @@music_cresselia
    ldr     r2,=#0x1EB              ; Darkrai
    cmp     r0,r2
    beq     @@music_legendary
    ldr     r2,=#0x1EC              ; Shaymin
    cmp     r0,r2
    beq     @@music_shaymin
    ldr     r2,=#0x1ED              ; Arceus
    cmp     r0,r2
    beq     @@music_arceus
@@music_regular:
    mov     r0,r1
    b       @@subr_end
@@music_legendary_bird:
    mov     r0,#0x15
    b       @@subr_end
@@music_regi:
    mov     r0,#0x17
    b       @@subr_end
@@music_legendary:
    mov     r0,#0x13
    b       @@subr_end
@@music_lake_trio:
    mov     r0,#0x10
    b       @@subr_end
@@music_mesprit:
    mov     r0,#0x11
    b       @@subr_end
@@music_dialga_palkia:
    mov     r0,#0xF
    b       @@subr_end
@@music_giratina:
    mov     r0,#0x16
    b       @@subr_end
@@music_cresselia:
    mov     r0,#0x14
    b       @@subr_end
@@music_shaymin:
    mov     r0,#0xE
    b       @@subr_end
@@music_arceus:
    mov     r0,#0x12
    b       @@subr_end
@@subr_end:
    pop     r1
    bx      r1

    .pool
    .endarea
    
    .close