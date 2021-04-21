; Reverse engineered from Kaphotic's original IPS patch.
; Names taken from the pokediamond decompilation.

    .nds
    .thumb
    .open "pkmnplatinum.bin", "pkmnplatinum_instant_text.bin", 0x02000000

    INSTRUCTION_TO_OVERWRTITE equ 0x020023FC           ; Don't think this has been decompiled
    RUN_TEXT_PRINTER equ 0x0201D97C
    GENERATE_FONT_HALF_ROW_LOOKUP_TABLE equ 0x0201D9FC
    RENDER_FONT equ 0x0201D9E8
    COPY_WINDOW_TO_VRAM equ 0x00201A954
    FREE_FUNCTION equ 0x0201D6B0                       ; Not named by decomp, but calls "FreeToHeap" so it seems appropriate

    .org INSTRUCTION_TO_OVERWRTITE
    .area 2
    mov     r1, #0x0
    .endarea

    .org RUN_TEXT_PRINTER
    .area 108

    push    { r4, lr }
    add     r4, r1, #0x0
@@start:
    ldr     r0,=#0x021C04D8
    ldrb    r0, [r0, #0x0]
    cmp     r0, #0x0
    bne     @@return
    add     r0, r4, #0x0
    add     r0, #0x2D
    ldrb    r0, [r0, #0x0]
    cmp     r0, #0x0
    bne     @@execute_callback
    mov     r0, #0x0
    strh    r0, [r4, #0x2E]
    ldrb    r0, [r4, #0x15]
    ldrb    r1, [r4, #0x16]
    ldrb    r2, [r4, #0x17]
    bl      GENERATE_FONT_HALF_ROW_LOOKUP_TABLE
    add     r0, r4, #0x0
    bl      RENDER_FONT
    cmp     r0, #0x0
    beq     @@copy_to_vram_and_execute_callback
    cmp     r0, #0x1
    beq     @@call_free_function
    pop     { r4, pc }
@@return_to_start:
    sub     r4, #0x2D
    b       @@start
@@copy_to_vram_and_execute_callback:
    ldr     r0, [r4, #0x4]
    bl      COPY_WINDOW_TO_VRAM
    ldr     r2, [r4, #0x1C]
    cmp     r2, #0x0
    beq     @@start
    ldrh    r1, [r4, #0x2E]
    add     r0, r4, #0x0
    blx     r2
    add     r4, #0x2D
    strb    r0, [r4, #0x0]
    b       @@return_to_start
@@call_free_function:
    add     r4, #0x2C
    ldrb    r0, [r4, #0x0]
    bl      FREE_FUNCTION
    pop     { r4, pc }
@@execute_callback:
    ldrh    r1, [r4, #0x2E]
    ldr     r2, [r4, #0x1C]
    add     r0, r4, #0x0
    blx     r2
    add     r4, #0x2D
    strb    r0, [r4, #0x0]
@@return:
    pop     { r4, pc }

    .pool
    .endarea

    .close