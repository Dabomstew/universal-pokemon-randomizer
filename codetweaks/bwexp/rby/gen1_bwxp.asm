; requires de = current party mon struct
BWXP_EXPCalculation::
	ld a, [BWXP_ENEMY_LEVEL]
	cp BWXP_MAX_LEVEL + 1
	jr c, .calc2LPlus10
	ld a, BWXP_MAX_LEVEL
	ld [BWXP_ENEMY_LEVEL], a
.calc2LPlus10
; start with 2L + 10 part
	add a
	add 10
; (2L+10)^2.5
	call BWXP_Power25Calculator
; *1.5 for trainer battle
	ld a, [BWXP_BATTLE_TYPE]
	dec a
	call nz, BWXP_BoostEXP
; *L again
	ld a, [BWXP_ENEMY_LEVEL]
	ld [BWXP_MULTIPLIER], a
	call BWXP_INBUILT_MULTIPLY
; divide by s (num of pokes used)
	push bc
	ld a, [BWXP_SCRATCH1B]
	ld [BWXP_DIVISOR], a
	ld b, $4
	call BWXP_INBUILT_DIVIDE
; divide by 2 if EXP ALL in bag
    ld b, EXP__ALL
	call BWXP_INBUILT_ISITEMINBAG
    jr z, .divideConstant
    ld a, 2
    ld [BWXP_DIVISOR], a
    ld b, $4
    call BWXP_INBUILT_DIVIDE
.divideConstant
; divide by 5 (constant)
	ld a, $5
	ld [BWXP_DIVISOR], a
	ld b, $4
	call BWXP_INBUILT_DIVIDE
	pop bc
; get # participants and store it for later as we need to use scratch1b for other stuff now
	ld a, [BWXP_SCRATCH1B]
	push af
; copy the result so far into scratch 1
	ld hl, BWXP_SCRATCH5B_1
	ld a, [BWXP_MULTIPLICAND]
	ld [hli], a
	ld a, [BWXP_MULTIPLICAND+1]
	ld [hli], a
	ld a, [BWXP_MULTIPLICAND+2]
	ld [hli], a
	ld a, [BWXP_MULTIPLICAND+3]
	ld [hl], a
; exp yield - done in two parts
; first multiplier - least significant byte
	ld bc, BWXP_Gen1_ExpYieldTable
	ld h, $0
	ld a, [BWXP_ENEMY_SPECIES]
	ld l, a
	add hl, hl
	add hl, bc
	ld a, [hl]
	ld [BWXP_MULTIPLIER], a
	call BWXP_BigMult
; store the result in the stack
	ld a, [BWXP_BIG_MULTIPLICAND]
	push af
	ld a, [BWXP_BIG_MULTIPLICAND + 1]
	push af
	ld a, [BWXP_BIG_MULTIPLICAND + 2]
	push af
	ld a, [BWXP_BIG_MULTIPLICAND + 3]
	push af
	ld a, [BWXP_BIG_MULTIPLICAND + 4]
	push af
; get back the original base
	ld hl, BWXP_SCRATCH5B_1
	ld a, [hli]
	ld [BWXP_MULTIPLICAND], a
	ld a, [hli]
	ld [BWXP_MULTIPLICAND + 1], a
	ld a, [hli]
	ld [BWXP_MULTIPLICAND + 2], a
	ld a, [hl]
	ld [BWXP_MULTIPLICAND + 3], a
; second multiplier - most significant byte
	ld bc, BWXP_Gen1_ExpYieldTable + 1
	ld h, $0
	ld a, [BWXP_ENEMY_SPECIES]
	ld l, a
	add hl, hl
	add hl, bc
	ld a, [hl]
	ld [BWXP_MULTIPLIER], a
	call BWXP_BigMult
; shift the result up one byte
	ld a, [BWXP_BIG_MULTIPLICAND + 1]
	ld [BWXP_BIG_MULTIPLICAND], a
	ld a, [BWXP_BIG_MULTIPLICAND + 2]
	ld [BWXP_BIG_MULTIPLICAND + 1], a
	ld a, [BWXP_BIG_MULTIPLICAND + 3]
	ld [BWXP_BIG_MULTIPLICAND + 2], a
	ld a, [BWXP_BIG_MULTIPLICAND + 4]
	ld [BWXP_BIG_MULTIPLICAND + 3], a
; add the old result back
; 5th byte of the new result is 0, so just use the old one directly
	pop af
	ld [BWXP_BIG_MULTIPLICAND + 4], a
; add the rest
	pop bc
	ld a, [BWXP_BIG_MULTIPLICAND + 3]
	add b
	ld [BWXP_BIG_MULTIPLICAND + 3], a
	pop bc
	ld a, [BWXP_BIG_MULTIPLICAND + 2]
	adc b
	ld [BWXP_BIG_MULTIPLICAND + 2], a
	pop bc
	ld a, [BWXP_BIG_MULTIPLICAND + 1]
	adc b
	ld [BWXP_BIG_MULTIPLICAND + 1], a
	pop bc
	ld a, [BWXP_BIG_MULTIPLICAND]
	adc b
	ld [BWXP_BIG_MULTIPLICAND], a
; now (L+Lp+10)
	ld a, [BWXP_ENEMY_LEVEL]
	ld b, a
; deal with our own level (and cap it if need be)
	ld hl, BWXP_LEVEL_OFFSET_IN_PARTYMON
	add hl, de ; de = own mon offset set by old code
	ld a, [hl]
	cp BWXP_MAX_LEVEL + 1
	jr c, .calcLLpPlus10
	ld a, BWXP_MAX_LEVEL
.calcLLpPlus10
	add b
	add 10
	ld b, a
; store the multiplication result in CFFE and dehl
	push de ; we still need this offset later
	ld a, [BWXP_BIG_MULTIPLICAND + 1]
	ld d, a
	ld a, [BWXP_BIG_MULTIPLICAND + 2]
	ld e, a
	ld a, [BWXP_BIG_MULTIPLICAND + 3]
	ld h, a
	ld a, [BWXP_BIG_MULTIPLICAND + 4]
	ld l, a
	ld a, [BWXP_BIG_MULTIPLICAND]
	ld [BWXP_SCRATCH1B], a
; now we can move on and do the 2.5 power of L+Lp+10
	ld a, b
	call BWXP_Power25Calculator
	call BWXP_SwapRamWithDEHL
; get the old MSB back from storage, the divisor here will never be 40-bit
	ld a, [BWXP_SCRATCH1B]
	ld [BWXP_BIG_MULTIPLICAND], a
; do the big division (BWXP_BIG_MULTIPLICAND / dehl)
	call BWXP_BigDivision
; finally, trade flags etc
; start by putting (exp+1) into BWXP_MULTIPLICAND
	xor a
	ld [BWXP_MULTIPLICAND], a
	ld a, c
	add 1
	ld [BWXP_MULTIPLICAND + 3], a
	ld a, $0
	adc b
	ld [BWXP_MULTIPLICAND + 2], a
	ld a, [BWXP_SCRATCH1B]
	adc $0
	ld [BWXP_MULTIPLICAND + 1], a
; now we need that offset into partymon again
; respect trade flag
	pop de
	ld hl, BWXP_TID_OFFSET_IN_PARTYMON
	add hl, de
	ld b, [hl]
	inc hl
	ld a, [BWXP_PLAYER_TID]
	cp b
	jr nz, .boostedEXP
	ld b, [hl]
	ld a, [BWXP_PLAYER_TID + 1]
	cp b
	ld a, $0
	jr z, .writeBoostedFlag

.boostedEXP
	call BWXP_BoostEXP
	ld a, $1

.writeBoostedFlag
	ld [BWXP_BOOSTED_EXP_FLAG], a
; it's time to actually give them the new EXP
; also store it in BWXP_SCRATCH5B_1 to be printed in text later
; last byte of EXP is +3 from second byte of TID
	inc hl
	inc hl
	inc hl
	ld b, [hl]
	ld a, [BWXP_MULTIPLICAND + 3]
	ld [BWXP_SCRATCH5B_1 + 2], a
	add b
	ld [hld], a
	ld b, [hl]
	ld a, [BWXP_MULTIPLICAND + 2]
	ld [BWXP_SCRATCH5B_1 + 1], a
	adc b
	ld [hld], a
	ld b, [hl]
	ld a, [BWXP_MULTIPLICAND + 1]
	ld [BWXP_SCRATCH5B_1], a
	adc b
	ld [hl], a
	jr nc, .expNotMaxedOut
; maxed out exp, write FFFFFF
	ld a, $ff
	ld [hli], a
	ld [hli], a
	ld [hl], a
	jr .done

.expNotMaxedOut
	inc hl
	inc hl

.done
; store num of participants for later
	pop af
	ld [BWXP_SCRATCH1B], a
	jp BWXP_RETURN_POINT

BWXP_Power25Calculator::
; calc (a^2.5), stored in the multiplication bytes
    ld [BWXP_MULTIPLICAND+3], a
    ld [BWXP_MULTIPLIER], a
    push af
    xor a
    ld [BWXP_MULTIPLICAND], a
    ld [BWXP_MULTIPLICAND+1], a
    ld [BWXP_MULTIPLICAND+2], a
    call BWXP_INBUILT_MULTIPLY
    pop af
	push hl
	ld h, a
	ld l, $0
	call BWXP_SqrtHL
	pop hl
	ld [BWXP_MULTIPLIER], a
	call BWXP_INBUILT_MULTIPLY
	push bc
	ld a, $10
	ld [BWXP_DIVISOR], a
	ld b, $4
	call BWXP_INBUILT_DIVIDE
	pop bc
	ret

BWXP_SqrtHL::
; sqrt hl, return a
; uses de
    push de
    ld a, $FF
    ld de, $0001
.loop
    inc a
    dec e
    dec de
    add hl, de
    jr c, .loop
    
    pop de
    ret

; boost exp by 1.5x for stuff like traded or trainer mons    
BWXP_BoostEXP::
    push bc
	ld a, $3
	ld [BWXP_MULTIPLIER], a
	call BWXP_INBUILT_MULTIPLY
	ld a, $2
	ld [BWXP_DIVISOR], a
	ld b, $4
	call BWXP_INBUILT_DIVIDE
	pop bc
	ret

;****************
; bigmul
; FF95-98 argument
; FF99 multiplier
; output to FF94-98
; uses CFFE as temp storage for 5th byte
;****************    
BWXP_BigMult:
	push bc
	ld b, 8
	xor a
	ld [BWXP_BIG_MULTIPLICAND], a
	ld [BWXP_SCRATCH1B], a
	ld [BWXP_MULTIPLIER_STOR], a
	ld [BWXP_MULTIPLIER_STOR + 1], a
	ld [BWXP_MULTIPLIER_STOR + 2], a
	ld [BWXP_MULTIPLIER_STOR + 3], a
.loop
	ld a, [BWXP_MULTIPLIER]
	srl a
	ld [BWXP_MULTIPLIER], a
	jr nc, .next
	ld a, [BWXP_MULTIPLIER_STOR + 3]
	ld c, a
	ld a, [BWXP_BIG_MULTIPLICAND + 4]
	add c
	ld [BWXP_MULTIPLIER_STOR + 3], a
	ld a, [BWXP_MULTIPLIER_STOR + 2]
	ld c, a
	ld a, [BWXP_BIG_MULTIPLICAND + 3]
	adc c
	ld [BWXP_MULTIPLIER_STOR + 2], a
	ld a, [BWXP_MULTIPLIER_STOR + 1]
	ld c, a
	ld a, [BWXP_BIG_MULTIPLICAND + 2]
	adc c
	ld [BWXP_MULTIPLIER_STOR + 1], a
	ld a, [BWXP_MULTIPLIER_STOR]
	ld c, a
	ld a, [BWXP_BIG_MULTIPLICAND + 1]
	adc c
	ld [BWXP_MULTIPLIER_STOR], a
	ld a, [BWXP_SCRATCH1B]
	ld c, a
	ld a, [BWXP_BIG_MULTIPLICAND]
	adc c
	ld [BWXP_SCRATCH1B], a

.next
	dec b
	jr z, .done
    push hl
    ld hl, BWXP_BIG_MULTIPLICAND + 4
    sla [hl]
    dec hl
    rl [hl]
    dec hl
    rl [hl]
    dec hl
    rl [hl]
    dec hl
    rl [hl]
    pop hl
	jr .loop

.done
	ld a, [BWXP_MULTIPLIER_STOR + 3]
	ld [BWXP_BIG_MULTIPLICAND + 4], a
	ld a, [BWXP_MULTIPLIER_STOR + 2]
	ld [BWXP_BIG_MULTIPLICAND + 3], a
	ld a, [BWXP_MULTIPLIER_STOR + 1]
	ld [BWXP_BIG_MULTIPLICAND + 2], a
	ld a, [BWXP_MULTIPLIER_STOR]
	ld [BWXP_BIG_MULTIPLICAND + 1], a
	ld a, [BWXP_SCRATCH1B]
	ld [BWXP_BIG_MULTIPLICAND], a
	pop bc
	ret

;******
; swapramanddehl
; inputs: FF95-98 and dehl
; swap them
; use CFED-CFF0 as temp storage
;******    
BWXP_SwapRamWithDEHL:
    push bc
	ld b, h
	ld c, l
; swap FF95-FF98 and debc
; backup debc
	ld hl, BWXP_SCRATCH5B_1
	ld a, d
	ld [hli], a
	ld a, e
	ld [hli], a
	ld a, b
	ld [hli], a
	ld [hl], c
; move FF95-98 into debc
	ld a, [BWXP_MULTIPLICAND]
	ld d, a
	ld a, [BWXP_MULTIPLICAND + 1]
	ld e, a
	ld a, [BWXP_MULTIPLICAND + 2]
	ld b, a
	ld a, [BWXP_MULTIPLICAND + 3]
	ld c, a
; move backup into FF95-98
	ld hl, BWXP_SCRATCH5B_1
	ld a, [hli]
	ld [BWXP_MULTIPLICAND], a
	ld a, [hli]
	ld [BWXP_MULTIPLICAND + 1], a
	ld a, [hli]
	ld [BWXP_MULTIPLICAND + 2], a
	ld a, [hl]
	ld [BWXP_MULTIPLICAND + 3], a
; move bc back into hl and return
	ld h, b
	ld l, c
	pop bc
	ret

;************************************
; bigdivision
; 40-bit by 32-bit bitwise long division
; Inputs
; BWXP_BIG_MULTIPLICAND: 40bit top
; de:hl : 32bit bottom
; Scratch space: BWXP_SCRATCH5B_1 scratch1
; BWXP_SCRATCH5B_2 scratch2
; BWXP_SCRATCH1B:bc result
; translation to the ARM:
; R0 is BWXP_SCRATCH1B:bc
; R1 is [BWXP_BIG_MULTIPLICAND-BWXP_BIG_MULTIPLICAND+4]
; R2 is [BWXP_SCRATCH5B_1-BWXP_SCRATCH5B_1+4]
; R3 is [BWXP_SCRATCH5B_2-BWXP_SCRATCH5B_2+4]
;************************************
BWXP_BigDivision::
; Initialize result
	ld bc, $0000
	xor a
	ld [BWXP_SCRATCH1B], a
; Check for div/0 and don't divide at all if it happens
	ld a, l
	and a
	jr nz, .dontquit
	ld a, h
	and a
	jr nz, .dontquit
	ld a, e
	and a
	jr nz, .dontquit
	ld a, d
	and a
	jr nz, .dontquit
	ret
.dontquit
; clear temp storage
	xor a
	push hl
	ld hl, BWXP_SCRATCH5B_1
	ld [hli], a
	ld [hli], a
	ld [hli], a
	ld [hli], a
	ld [hl], a
	ld hl, BWXP_SCRATCH5B_2
	ld [hli], a
	ld [hli], a
	ld [hli], a
	ld [hli], a
; final byte in scratch2 should be 1 to be the shifter
	ld a, $1
	ld [hl], a
; get back original HL
	pop hl
; copy initial value of de:hl into the lower 4 bytes of scratch1
	ld a, l
	ld [BWXP_SCRATCH5B_1 + 4], a
	ld a, h
	ld [BWXP_SCRATCH5B_1 + 3], a
	ld a, e
	ld [BWXP_SCRATCH5B_1 + 2], a
	ld a, d
	ld [BWXP_SCRATCH5B_1 + 1], a
; setup for the division
.setup
	ld hl, BWXP_SCRATCH5B_1
	ld de, BWXP_BIG_MULTIPLICAND
	call BWXP_FortyBitCompare
	jr nc, .loop
	ld hl, BWXP_SCRATCH5B_1 + 4
	call BWXP_FortyBitLeftShift
	ld hl, BWXP_SCRATCH5B_2 + 4
	call BWXP_FortyBitLeftShift
	jr .setup

.loop
	ld hl, BWXP_SCRATCH5B_1
	ld de, BWXP_BIG_MULTIPLICAND
	call BWXP_FortyBitCompare
	jr nc, .aftersubtract
	ld de, BWXP_BIG_MULTIPLICAND + 4
	ld hl, BWXP_SCRATCH5B_1 + 4
	call BWXP_FortyBitSubtract
	call BWXP_BigDiv_AccumulateAnswer

.aftersubtract
	ld hl, BWXP_SCRATCH5B_2
	call BWXP_FortyBitRightShift
	jr c, .done ; if carry is set, the accumulator finished so we're done.
	ld hl, BWXP_SCRATCH5B_1
	call BWXP_FortyBitRightShift
	jr .loop

.done
	ret
    
;*****
; 40-bit subtract value ending at [hl] from value ending at [de]
;*****
BWXP_FortyBitSubtract::
	ld a, [de]
	sub [hl]
	ld [de], a
    
    rept 4
	dec de
	dec hl
	ld a, [de]
	sbc [hl]
	ld [de], a
    endr

	ret
    
;*****
; set the appropriate answer bit when we do a division step
;*****
BWXP_BigDiv_AccumulateAnswer::
	push de
	ld a, [BWXP_SCRATCH5B_2 + 2]
	and a
	jr z, .checkSecondByte
	ld d, a
	ld a, [BWXP_SCRATCH1B]
	or d
	ld [BWXP_SCRATCH1B], a
	jr .done

.checkSecondByte
	ld a, [BWXP_SCRATCH5B_2 + 3]
	and a
	jr z, .checkThirdByte
	ld d, a
	ld a, b
	or d
	ld b, a
	jr .done

.checkThirdByte
	ld a, [BWXP_SCRATCH5B_2 + 4]
	and a
	jr z, .done
	ld d, a
	ld a, c
	or d
	ld c, a

.done
	pop de
	ret

;***********
; 40-bit <=
; sets carryflag if value starting at [hl] <= value starting at [de]
; uses b as temp storage along with a
; clears otherwise
;***********
BWXP_FortyBitCompare::
	ld a, [de]
	cp [hl]
	jr c, .returnFalse
	jr nz, .returnTrue
	inc de
    inc hl
	ld a, [de]
	cp [hl]
	jr c, .returnFalse
	jr nz, .returnTrue
	inc de
    inc hl
    ld a, [de]
	cp [hl]
	jr c, .returnFalse
	jr nz, .returnTrue
	inc de
    inc hl
    ld a, [de]
	cp [hl]
	jr c, .returnFalse
	jr nz, .returnTrue
	inc de
    inc hl
	ld a, [de]
	cp [hl]
	jr c, .returnFalse
.returnTrue
	scf
	ret
.returnFalse
	and a
	ret
    
BWXP_FortyBitLeftShift::
; take hl = last address in memory
; shift it and the four preceding bytes left
    sla [hl]
    
    rept 4
    dec hl
    rl [hl]
    endr
    
    ret
    
BWXP_FortyBitRightShift::
; take hl = first address in memory
; shift it and the four following bytes right
    srl [hl]
    
    rept 4
    inc hl
    rr [hl]
    endr
    
    ret

; EXP Yields
; 1 word per pokemon
; in ingame ordering, starting with pokemon 0
BWXP_Gen1_ExpYieldTable:
	dw 0
	dw 170
	dw 172
	dw 55
	dw 113
	dw 52
	dw 66
	dw 223
	dw 172
	dw 142
	dw 182
	dw 77
	dw 65
	dw 65
	dw 225
	dw 55
	dw 223
	dw 64
	dw 69
	dw 187
	dw 194
	dw 270
	dw 189
	dw 61
	dw 67
	dw 62
	dw 100
	dw 68
	dw 239
	dw 175
	dw 87
	dw 0
	dw 0
	dw 70
	dw 77
	dw 155
	dw 50
	dw 63
	dw 140
	dw 137
	dw 395
	dw 142
	dw 161
	dw 159
	dw 159
	dw 153
	dw 142
	dw 64
	dw 66
	dw 218
	dw 0
	dw 173
	dw 0
	dw 172
	dw 163
	dw 68
	dw 0
	dw 61
	dw 65
	dw 53
	dw 172
	dw 0
	dw 0
	dw 0
	dw 123
	dw 61
	dw 270
	dw 0
	dw 0
	dw 0
	dw 62
	dw 60
	dw 159
	dw 261
	dw 261
	dw 261
	dw 101
	dw 58
	dw 65
	dw 0
	dw 0
	dw 0
	dw 60
	dw 177
	dw 105
	dw 214
	dw 0
	dw 0
	dw 60
	dw 147
	dw 71
	dw 173
	dw 59
	dw 154
	dw 0
	dw 0
	dw 60
	dw 158
	dw 71
	dw 173
	dw 95
	dw 191
	dw 65
	dw 184
	dw 184
	dw 184
	dw 61
	dw 49
	dw 58
	dw 57
	dw 135
	dw 225
	dw 39
	dw 72
	dw 173
	dw 0
	dw 161
	dw 159
	dw 142
	dw 158
	dw 166
	dw 0
	dw 0
	dw 39
	dw 72
	dw 173
	dw 227
	dw 0
	dw 175
	dw 169
	dw 159
	dw 306
	dw 189
	dw 40
	dw 0
	dw 0
	dw 175
	dw 0
	dw 166
	dw 184
	dw 0
	dw 168
	dw 213
	dw 172
	dw 154
	dw 149
	dw 0
	dw 142
	dw 62
	dw 221
	dw 122
	dw 211
	dw 182
	dw 64
	dw 236
	dw 180
	dw 0
	dw 64
	dw 158
	dw 0
	dw 0
	dw 0
	dw 0
	dw 82
	dw 175
	dw 51
	dw 145
	dw 128
	dw 128
	dw 60
	dw 79
	dw 180
	dw 0
	dw 65
	dw 0
	dw 0
	dw 62
	dw 63
	dw 142
	dw 142
	dw 240
	dw 0
	dw 0
	dw 0
	dw 0
	dw 64
	dw 138
	dw 216
	dw 60
	dw 137
	dw 216
; remaining slots 191-255
    rept 65
    dw 0
    endr
    
BWXP_DivideExpDataHook::
    ld [BWXP_SCRATCH1B], a
	cp $2
	ret c
	ld [BWXP_NUM_PARTICIPANTS], a
	jp BWXP_DIVIDEEXP_RETURN_POINT
