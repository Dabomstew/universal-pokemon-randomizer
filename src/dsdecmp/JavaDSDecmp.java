package dsdecmp;

//Part of DSDecmp-Java
//License is below

//Copyright (c) 2010 Nick Kraayenbrink
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

import java.io.EOFException;
import java.io.IOException;

public class JavaDSDecmp {

	public static int[] Decompress(HexInputStream his) throws IOException {
		switch (his.readU8()) {
		case 0x11:
			return Decompress11LZ(his);
		default:
			return null;
		}
	}

	private static int getLength(HexInputStream his) throws IOException {
		int length = 0;
		for (int i = 0; i < 3; i++)
			length = length | (his.readU8() << (i * 8));
		if (length == 0) // 0 length? then length is next 4 bytes
			length = his.readlS32();
		return length;
	}

	private static int[] Decompress11LZ(HexInputStream his) throws IOException {
		int[] outData = new int[getLength(his)];

		int curr_size = 0;
		int flags;
		boolean flag;
		int b1, bt, b2, b3, len, disp, cdest;

		while (curr_size < outData.length) {
			try {
				flags = his.readU8();
			} catch (EOFException ex) {
				break;
			}

			for (int i = 0; i < 8 && curr_size < outData.length; i++) {
				flag = (flags & (0x80 >> i)) > 0;
				if (flag) {
					try {
						b1 = his.readU8();
					} catch (EOFException ex) {
						throw new InvalidFileException("Incomplete data");
					}

					switch (b1 >> 4) {
					case 0:
						// ab cd ef
						// =>
						// len = abc + 0x11 = bc + 0x11
						// disp = def

						len = b1 << 4;
						try {
							bt = his.readU8();
						} catch (EOFException ex) {
							throw new InvalidFileException("Incomplete data");
						}
						len |= bt >> 4;
						len += 0x11;

						disp = (bt & 0x0F) << 8;
						try {
							b2 = his.readU8();
						} catch (EOFException ex) {
							throw new InvalidFileException("Incomplete data");
						}
						disp |= b2;
						break;

					case 1:
						// ab cd ef gh
						// =>
						// len = bcde + 0x111
						// disp = fgh
						// 10 04 92 3F => disp = 0x23F, len = 0x149 + 0x11 =
						// 0x15A

						try {
							bt = his.readU8();
							b2 = his.readU8();
							b3 = his.readU8();
						} catch (EOFException ex) {
							throw new InvalidFileException("Incomplete data");
						}

						len = (b1 & 0xF) << 12; // len = b000
						len |= bt << 4; // len = bcd0
						len |= (b2 >> 4); // len = bcde
						len += 0x111; // len = bcde + 0x111
						disp = (b2 & 0x0F) << 8; // disp = f
						disp |= b3; // disp = fgh
						break;

					default:
						// ab cd
						// =>
						// len = a + threshold = a + 1
						// disp = bcd

						len = (b1 >> 4) + 1;

						disp = (b1 & 0x0F) << 8;
						try {
							b2 = his.readU8();
						} catch (EOFException ex) {
							throw new InvalidFileException("Incomplete data");
						}
						disp |= b2;
						break;
					}

					if (disp > curr_size)
						throw new InvalidFileException(
								"Cannot go back more than already written");

					cdest = curr_size;

					for (int j = 0; j < len && curr_size < outData.length; j++)
						outData[curr_size++] = outData[cdest - disp - 1 + j];

					if (curr_size > outData.length)
						break;
				} else {
					try {
						outData[curr_size++] = his.readU8();
					} catch (EOFException ex) {
						break;
					}// throw new Exception("Incomplete data"); }

					if (curr_size > outData.length)
						break;
				}
			}

		}
		return outData;
	}

}
