package compressors;

/**
 * Pokemon Gen 1 sprite decompressor Source:
 * https://github.com/pret/pokemon-reverse-engineering-tools/blob/master/pokemontools/pic.py 
 * (and gfx.py for flatten())
 * Ported to Java by Dabomstew
 *
 */
public class Gen1Decmp {

    private static int bitflip(int x, int n) {
        int r = 0;
        while (n > 0) {
            r = (r << 1) | (x & 1);
            x >>= 1;
            n -= 1;
        }
        return r;
    }

    private static int[] table1, table3;
    private static int[][] table2 = { { 0x0, 0x1, 0x3, 0x2, 0x7, 0x6, 0x4, 0x5, 0xf, 0xe, 0xc, 0xd, 0x8, 0x9, 0xb, 0xa },
            { 0xf, 0xe, 0xc, 0xd, 0x8, 0x9, 0xb, 0xa, 0x0, 0x1, 0x3, 0x2, 0x7, 0x6, 0x4, 0x5 },
            { 0x0, 0x8, 0xc, 0x4, 0xe, 0x6, 0x2, 0xa, 0xf, 0x7, 0x3, 0xb, 0x1, 0x9, 0xd, 0x5 },
            { 0xf, 0x7, 0x3, 0xb, 0x1, 0x9, 0xd, 0x5, 0x0, 0x8, 0xc, 0x4, 0xe, 0x6, 0x2, 0xa }, };

    static {
        table1 = new int[16];
        table3 = new int[16];

        for (int i = 0; i < 16; i++) {
            table1[i] = (2 << i) - 1;
            table3[i] = bitflip(i, 4);
        }
    }

    private static int tilesize = 8;

    private BitStream bs;
    private boolean mirror, planar;
    private byte[] data;
    private int sizex, sizey, size;
    private int ramorder;

    public Gen1Decmp(byte[] input, int baseOffset) {
        this(input, baseOffset, false, true);
    }

    private Gen1Decmp(byte[] input, int baseOffset, boolean mirror, boolean planar) {
        this.bs = new BitStream(input, baseOffset);
        this.mirror = mirror;
        this.planar = planar;
        this.data = null;
    }

    public void decompress() {
        byte[][] rams = new byte[2][];

        this.sizex = this.readint(4) * tilesize;
        this.sizey = this.readint(4);

        this.size = this.sizex * this.sizey;

        this.ramorder = this.readbit();

        int r1 = this.ramorder;
        int r2 = this.ramorder ^ 1;

        fillram(rams, r1);
        int mode = this.readbit();
        if (mode == 1) {
            mode += this.readbit();
        }
        fillram(rams, r2);

        rams[0] = this.bitgroups_to_bytes(rams[0]);
        rams[1] = this.bitgroups_to_bytes(rams[1]);

        if (mode == 0) {
            this.decode(rams[0]);
            this.decode(rams[1]);
        } else if (mode == 1) {
            this.decode(rams[r1]);
            this.xor(rams[r1], rams[r2]);
        } else if (mode == 2) {
            this.decode(rams[r2], false);
            this.decode(rams[r1]);
            this.xor(rams[r1], rams[r2]);
        }

        if (this.planar) {
            data = new byte[this.size * 2];
            for (int i = 0; i < rams[0].length; i++) {
                data[i * 2] = rams[0][i];
                data[i * 2 + 1] = rams[1][i];
            }
        } else {
            byte[] tmpdata = new byte[this.size * 8];
            BitStream r0S = new BitStream(rams[0]);
            BitStream r1S = new BitStream(rams[1]);
            for (int i = 0; i < tmpdata.length; i++) {
                tmpdata[i] = (byte) (r0S.next() | (r1S.next() << 1));
            }
            data = this.bitgroups_to_bytes(tmpdata);
        }

    }

    public void transpose() {
        if (data == null) {
            return;
        }
        int tiles = data.length / 16;
        int width = this.sizex / tilesize;
        int height = this.sizey;

        byte[] newData = new byte[data.length];
        for (int tile = 0; tile < tiles; tile++) {
            int oldTileX = tile % width;
            int oldTileY = tile / width;
            int newTileNum = oldTileX * height + oldTileY;
            System.arraycopy(data, tile * 16, newData, newTileNum * 16, 16);
        }
        data = newData;

    }

    public byte[] getData() {
        return data;
    }

    public byte[] getFlattenedData() {
        return flatten(data);
    }

    public int getWidth() {
        return this.sizex;
    }

    public int getHeight() {
        return this.sizey * tilesize;
    }

    private void fillram(byte[][] rams, int rOffset) {
        int size = this.size * 4;
        rams[rOffset] = new byte[size];
        boolean rleMode = this.readbit() == 0;
        int written = 0;
        while (written < size) {
            if (rleMode) {
                written += this.read_rle_chunk(rams[rOffset], written);
            } else {
                written += this.read_data_chunk(rams[rOffset], written, size);
            }
            rleMode = !rleMode;
        }
        rams[rOffset] = deinterlace_bitgroups(rams[rOffset]);
    }

    private byte[] deinterlace_bitgroups(byte[] bits) {
        byte[] l = new byte[bits.length];
        int offs = 0;
        for (int y = 0; y < this.sizey; y++) {
            for (int x = 0; x < this.sizex; x++) {
                int i = 4 * y * this.sizex + x;
                for (int j = 0; j < 4; j++) {
                    l[offs++] = bits[i];
                    i += this.sizex;
                }
            }
        }
        return l;
    }

    private int read_rle_chunk(byte[] ram, int baseOffset) {
        int i = 0;
        while (this.readbit() == 1) {
            i++;
        }

        int n = table1[i];
        int a = this.readint(i + 1);
        n += a;

        for (i = 0; i < n; i++) {
            ram[baseOffset + i] = 0;
        }
        return n;
    }

    private int read_data_chunk(byte[] ram, int baseOffset, int size) {
        int written = 0;
        while (true) {
            int bitgroup = this.readint(2);
            if (bitgroup == 0) {
                break;
            }
            ram[baseOffset + written] = (byte) bitgroup;
            written++;

            if (baseOffset + written >= size) {
                break;
            }
        }
        return written;
    }

    private int readbit() {
        return bs.next();
    }

    private int readint(int count) {
        return readint(bs, count);
    }

    private int readint(BitStream strm, int count) {
        int n = 0;
        while (count > 0) {
            n <<= 1;
            n |= strm.next();
            count -= 1;
        }
        return n;
    }

    private byte[] bitgroups_to_bytes(byte[] bits) {
        int limiter = bits.length - 3;
        byte[] ret = new byte[bits.length / 4];
        for (int i = 0; i < limiter; i += 4) {
            int n = ((bits[i] << 6) | (bits[i + 1] << 4) | (bits[i + 2] << 2) | (bits[i + 3]));
            ret[i / 4] = (byte) n;
        }
        return ret;
    }

    private void decode(byte[] ram) {
        decode(ram, this.mirror);
    }

    private void decode(byte[] ram, boolean mirror) {
        for (int x = 0; x < this.sizex; x++) {
            int bit = 0;
            for (int y = 0; y < this.sizey; y++) {
                int i = y * this.sizex + x;
                int a = ((ram[i] & 0xFF) >> 4) & 0x0F;
                int b = ram[i] & 0x0F;

                a = table2[bit][a];
                bit = a & 1;
                if (mirror) {
                    a = table3[a];
                }

                b = table2[bit][b];
                bit = b & 1;
                if (mirror) {
                    b = table3[b];
                }

                ram[i] = (byte) ((a << 4) | b);
            }
        }
    }

    private void xor(byte[] ram1, byte[] ram2) {
        xor(ram1, ram2, this.mirror);
    }

    private void xor(byte[] ram1, byte[] ram2, boolean mirror) {
        for (int i = 0; i < ram2.length; i++) {
            if (mirror) {
                int a = ((ram2[i] & 0xFF) >> 4) & 0x0F;
                int b = ram2[i] & 0x0F;
                a = table3[a];
                b = table3[b];
                ram2[i] = (byte) ((a << 4) | b);
            }

            ram2[i] = (byte) ((ram2[i] & 0xFF) ^ (ram1[i] & 0xFF));
        }
    }

    private static class BitStream {
        private byte[] data;
        private int offset;
        private int bitsLeft;
        private int bufVal;

        private static final int bmask[] = { 0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff,
                0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff,
                0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff,
                0xffffffff };

        public BitStream(byte[] data) {
            this(data, 0);
        }

        public BitStream(byte[] data, int baseOffset) {
            this.data = data;
            this.offset = baseOffset - 1;
            this.bitsLeft = 0;
            this.bufVal = -1;
        }

        public int next() {
            if (bitsLeft == 0) {
                offset++;
                bufVal = data[offset] & 0xFF;
                if (offset >= data.length) {
                    return -1;
                }
                bitsLeft = 8;
            }

            int retval = bufVal >> (bitsLeft - 1);
            bufVal &= bmask[bitsLeft - 1];
            bitsLeft--;

            return retval;
        }
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
