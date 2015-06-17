BWXP_EXPAdderHook::
; copy back yield to multiplier fields
    ld a, [BWXP_SCRATCH5B_1 + 2]
    ld [BWXP_MULTIPLICAND + 3], a
    ld a, [BWXP_SCRATCH5B_1 + 1]
    ld [BWXP_MULTIPLICAND + 2], a
    ld a, [BWXP_SCRATCH5B_1]
    ld [BWXP_MULTIPLICAND + 1], a
    
; unknown functions from original code, call them as is
    pop bc
    call BWXP_UNKNOWNFUNC1
    push bc
    call BWXP_UNKNOWNFUNC2
    pop bc
; set hl = 3rd byte of party mon exp value (+10 from current bc)
    ld hl, $a
    add hl, bc
; add new exp
    ld d, [hl]
    ld a, [BWXP_MULTIPLICAND + 3]
    add d
    ld [hld], a
    
    ld d, [hl]
    ld a, [BWXP_MULTIPLICAND + 2]
    adc d
    ld [hld], a
    
    ld d, [hl]
    ld a, [BWXP_MULTIPLICAND + 1]
    adc d
    ld [hl], a
    jr nc, .done
; maxed exp, set it to FFFFFF
    ld a, $ff
    ld [hli], a
    ld [hli], a
    ld [hl], a

.done
    jp BWXP_EXPADDER_RETURN_POINT
    