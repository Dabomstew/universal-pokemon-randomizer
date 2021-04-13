BWXP_Bootstrap::
    push hl
    push bc
    ld hl, BWXP_EXPCalculation
    ld a, BANK(BWXP_EXPCalculation)
    rst $08
    pop bc
    pop hl
    jp BWXP_MAIN_RETURN_POINT