package dsdecmp;

// Part of DSDecmp-Java
// License is below

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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class HexInputStream {

	/** The InputStream this stream is based on. */
	private volatile InputStream dis;
	/** The current position of this stream. */
	private volatile long currPos;

	/** Get the current position of this stream. */
	public long getPosition() {
		return this.currPos;
	}

	/**
	 * Sets the position of this stream.
	 * 
	 * @param newPos
	 *            The desired position of the stream.
	 * @throws IOException
	 *             when the given position cannot be reached
	 */
	public void setPosition(long newPos) throws IOException {
		this.skip(newPos - currPos);
	}

	/** Convenience method for {@link #setPosition(long)}. */
	public void goTo(long pos) throws IOException {
		this.setPosition(pos);
	}

	/** The stack of saved positions for this stream. */
	private Stack<Long> positionStack;

	/**
	 * Creates a new HexInputStream, based off another InputStream. The
	 * 0-position of the new stream is the current position of the given stream.
	 * 
	 * @param baseInputStream
	 *            The InputStream to base the new HexInputStream on.
	 */
	public HexInputStream(InputStream baseInputStream) {
		this.dis = baseInputStream;
		this.currPos = 0;
		this.positionStack = new Stack<Long>();
	}

	/**
	 * Creates a new HexInputStream for reading a file.
	 * 
	 * @param String
	 *            The name of the file to read.
	 * @throws FileNotFoundException
	 *             If the given file does not exist.
	 */
	public HexInputStream(String filename) throws FileNotFoundException {
		this.dis = new DataInputStream(new FileInputStream(new File(filename)));
		this.currPos = 0;
		this.positionStack = new Stack<Long>();
	}

	/**
	 * Returns an estimate of the number of bytes left to read until the EOF.
	 * See {@link InputStream#available}
	 */
	public int available() throws IOException {
		return dis.available();
	}

	/**
	 * Read the next byte from a file. If the EOF has been reached, -1 is
	 * returned
	 */
	public int read() throws IOException {
		int b = dis.read();
		if (b != -1)
			currPos++;
		return b;
	}

	/** Read an array of bytes from this stream */
	public int[] readBytes(int length) throws IOException {
		int[] data = new int[length];
		for (int i = 0; i < length; i++)
			data[i] = readU8();
		return data;
	}

	/** Read a byte from this stream */
	public int readU8() throws IOException {
		int b = dis.read();
		currPos++;
		return b;
	}

	/** Read a BigEndian s16 from this stream */
	public short readS16() throws IOException {
		short word = 0;
		for (int i = 0; i < 2; i++)
			word = (short) (word | (readU8() << (8 * i)));
		return word;
	}

	/** Read a LittleEndian s16 from this stream */
	public short readlS16() throws IOException {
		short word = 0;
		for (int i = 0; i < 2; i++)
			word = (short) ((word << 8) | readU8());
		return word;
	}

	/** Read a BigEndian u16 from this stream */
	public int readU16() throws IOException {
		int word = 0;
		for (int i = 0; i < 2; i++)
			word = word | (readU8() << (8 * i));
		return word;
	}

	/** Read a LittleEndian u16 from this stream */
	public int readlU16() throws IOException {
		int word = 0;
		for (int i = 0; i < 2; i++)
			word = (word << 8) | readU8();
		return word;
	}

	/** Read a BigEndian s32 from this stream (a signed int) */
	public int readS32() throws IOException {
		int dword = 0;
		for (int i = 0; i < 4; i++)
			dword = dword | (readU8() << (8 * i));
		return dword;
	}

	/** Read a LittleEndian s32 from this stream (a signed int) */
	public int readlS32() throws IOException {
		int dword = 0;
		for (int i = 0; i < 4; i++)
			dword = (dword << 8) | readU8();
		return dword;
	}

	/** Read a BigEndian u32 from this stream (an unsigned int) */
	public long readU32() throws IOException {
		long dword = 0;
		for (int i = 0; i < 4; i++)
			dword = dword | (readU8() << (8 * i));

		return dword;
	}

	/** Read a LittleEndian u32 from this stream (an unsigned int) */
	public long readlU32() throws IOException {
		long dword = 0;
		for (int i = 0; i < 4; i++)
			dword = (dword << 8) | readU8();
		return dword;
	}

	/** Read a BigEndian s64 from this stream (a signed int) */
	public long readS64() throws IOException {
		long qword = 0;
		for (int i = 0; i < 8; i++)
			qword = qword | (readU8() << (8 * i));
		return qword;
	}

	/** Read a LittleEndian s64 from this stream (a signed int) */
	public long readlS64() throws IOException {
		long qword = 0;
		for (int i = 0; i < 8; i++)
			qword = (qword << 8) | readU8();
		return qword;
	}

	/** Close this stream */
	public void close() throws IOException {
		dis.close();
	}

	/** Skip n bytes */
	public void skip(long n) throws IOException {
		currPos += dis.skip(n);
	}

	/** Reset this stream to its base position. */
	public void reset() throws IOException {
		this.goTo(0);
	}

	/** Save the current position on the local stack */
	public void savePosition() {
		this.positionStack.push(this.currPos);
	}

	/**
	 * Pop the last saved position from the local stack and restore that
	 * position
	 */
	public void loadPosition() throws IOException {
		if (!this.positionStack.isEmpty()) {
			long pos = this.positionStack.peek();
			this.positionStack.pop();
			this.goTo(pos);
		}
	}

}
