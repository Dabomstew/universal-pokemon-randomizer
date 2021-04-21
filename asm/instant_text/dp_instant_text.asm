; Names taken from the pokediamond decompilation.

    .nds
    .thumb
    .open "pkmndiamond.bin", "pkmndiamond_instant_text.bin", 0x02000000

    INSTRUCTION_TO_OVERWRTITE equ 0x02002494           ; Don't think this has been decompiled
    RUN_TEXT_PRINTER equ 0x0201BFDC
    GENERATE_FONT_HALF_ROW_LOOKUP_TABLE equ 0x0201C05C
    RENDER_FONT equ 0x0201C048
    COPY_WINDOW_TO_VRAM equ 0x020191D0
    FREE_FUNCTION equ 0x0201BCFC                       ; Not named by decomp, but calls "FreeToHeap" so it seems appropriate

    .org INSTRUCTION_TO_OVERWRTITE
    .area 2
    mov     r1, #0x0
    .endarea

    .org RUN_TEXT_PRINTER
    .area 108

    push    { r4, lr }
    add     r4, r1, #0x0
@@start:
    ldr     r0,=#0x021C570C
    ldrb    r0, [r0, #0x0]
    cmp     r0, #0x0
    bne     @@return
    add     r0, r4, #0x0
    add     r0, #0x29
    ldrb    r0, [r0, #0x0]
    cmp     r0, #0x0
    bne     @@execute_callback
    mov     r0, #0x0
    strh    r0, [r4, #0x2A]
    ldrb    r0, [r4, #0x11]
    ldrb    r1, [r4, #0x12]
    ldrb    r2, [r4, #0x13]
    bl      GENERATE_FONT_HALF_ROW_LOOKUP_TABLE
    add     r0, r4, #0x0
    bl      RENDER_FONT
    cmp     r0, #0x0
    beq     @@copy_to_vram_and_execute_callback
    cmp     r0, #0x1
    beq     @@call_free_function
    pop     { r4, pc }
@@return_to_start:
    sub     r4, #0x29
    b       @@start
@@copy_to_vram_and_execute_callback:
    ldr     r0, [r4, #0x4]
    bl      COPY_WINDOW_TO_VRAM
    ldr     r2, [r4, #0x18]
    cmp     r2, #0x0
    beq     @@start
    ldrh    r1, [r4, #0x2A]
    add     r0, r4, #0x0
    blx     r2
    add     r4, #0x29
    strb    r0, [r4, #0x0]
    b       @@return_to_start
@@call_free_function:
    add     r4, #0x28
    ldrb    r0, [r4, #0x0]
    bl      FREE_FUNCTION
    pop     { r4, pc }
@@execute_callback:
    ldrh    r1, [r4, #0x2A]
    ldr     r2, [r4, #0x18]
    add     r0, r4, #0x0
    blx     r2
    add     r4, #0x29
    strb    r0, [r4, #0x0]
@@return:
    pop     { r4, pc }

    .pool
    .endarea

    .close