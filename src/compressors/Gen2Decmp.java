package compressors;

/**
 * Pokemon Gen 2 sprite decompressor Source:
 * https://github.com/pret/pokemon-reverse-engineering-tools/blob/master/pokemontools/lz.py 
 * (and gfx.py for flatten())
 * Ported to Java by Dabomstew
 *
 */
public class Gen2Decmp {

    private static final int LZ_END = 0xFF;
    private static final int INITIAL_BUF_SIZE = 0x1000;

    public byte[] data;
    private int address;
    private byte[] output;
    private int out_idx;
    private int cmd;
    private int len;
    private int offset;

    private static int[] bit_flipped;

    static {
        bit_flipped = new int[0x100];
        for (int b = 0; b < 0x100; b++) {
            for (int i = 0; i < 8; i++) {
                bit_flipped[b] += ((b >> i) & 1) << (7 - i);
            }
        }
    }

    public Gen2Decmp(byte[] input, int baseOffset, int tilesWide, int tilesHigh) {
        this.data = input;
        this.address = baseOffset;
        decompress();
        cutAndTranspose(tilesWide, tilesHigh);
    }

    public byte[] getData() {
        return output;
    }

    public byte[] getFlattenedData() {
        return flatten(output);
    }

    private void cutAndTranspose(int width, int height) {
        if (output == null) {
            return;
        }
        int tiles = width * height;

        byte[] newData = new byte[width * height * 16];
        for (int tile = 0; tile < tiles; tile++) {
            int oldTileX = tile % width;
            int oldTileY = tile / width;
            int newTileNum = oldTileX * height + oldTileY;
            System.arraycopy(output, tile * 16, newData, newTileNum * 16, 16);
        }
        output = newData;

    }

    private void decompress() {
        output = new byte[INITIAL_BUF_SIZE];
        while (true) {
            if (this.peek() == LZ_END) {
                this.next();
                break;
            }

            cmd = (this.peek() & 0xE0) >> 5;
            if (cmd == 7) {
                // LONG command
                cmd = (this.peek() & 0x1C) >> 2;
                len = (this.next() & 0x03) * 0x100 + this.next() + 1;
            } else {
                // Normal length
                len = (this.next() & 0x1F) + 1;
            }

            while (out_idx + len > output.length) {
                resizeOutput();
            }

            switch (cmd) {
            case 0:
                // Literal
                System.arraycopy(data, address, output, out_idx, len);
                out_idx += len;
                address += len;
                break;
            case 1:
                // Iterate
                byte repe = (byte) next();
                for (int i = 0; i < len; i++) {
                    output[out_idx++] = repe;
                }
                break;
            case 2:
                // Alternate
                byte[] alts = { (byte) next(), (byte) next() };
                for (int i = 0; i < len; i++) {
                    output[out_idx++] = alts[i & 1];
                }
                break;
            case 3:
                // Zero-fill
                // Easy, since Java arrays are initialized to 0.
                out_idx += len;
                break;
            case 4:
                // Default repeat
                repeat();
                break;
            case 5:
                repeat(1, bit_flipped);
                break;
            case 6:
                repeat(-1, null);
                break;
            }
        }

        byte[] finalOutput = new byte[out_idx];
        System.arraycopy(output, 0, finalOutput, 0, out_idx);
        output = finalOutput;

    }

    private void repeat() {
        repeat(1, null);
    }

    private void repeat(int direction, int[] table) {
        get_offset();
        for (int i = 0; i < len; i++) {
            int value = output[offset + i * direction] & 0xFF;
            output[out_idx++] = (byte) ((table == null) ? value : table[value]);
        }
    }

    private void get_offset() {
        if (this.peek() >= 0x80) {
            // Negative
            offset = this.next() & 0x7F;
            offset = out_idx - offset - 1;
        } else {
            // Positive, extended
            offset = this.next() * 0x100 + this.next();
        }
    }

    private void resizeOutput() {
        byte[] newOut = new byte[output.length * 2];
        System.arraycopy(output, 0, newOut, 0, out_idx);
        output = newOut;
    }

    private int peek() {
        return data[address] & 0xFF;
    }

    public int next() {
        return data[address++] & 0xFF;
    }

    private static byte[] flatten(byte[] planar) {
        byte[] strips = new byte[planar.length * 4];
        for (int j = 0; j < planar.length / 2; j++) {
            int bottom = planar[j * 2] & 0xFF;
            int top = planar[j * 2 + 1] & 0xFF;
            byte[] strip = new byte[8];
            for (int i = 7; i >= 0; i--) {
                strip[7 - i] = (byte) (((bottom >>> i) & 1) + ((top * 2 >>> i) & 2));
            }
            System.arraycopy(strip, 0, strips, j * 8, 8);
        }
        return strips;
    }

}
