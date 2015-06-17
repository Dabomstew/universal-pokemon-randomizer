BWXP_DivideExpDataHook::
    ld [BWXP_SCRATCH1B], a
	cp $2
	ret c
	ld [BWXP_NUM_PARTICIPANTS], a
	jp BWXP_DIVIDEEXP_RETURN_POINT