package com.g4mesoft.net;

import java.util.UUID;

public class PacketByteBuffer {

	private static final int DEFAULT_CAPACITY = 10;
	
	private byte[] data;
	
	private int pos;
	private int size;
	
	public PacketByteBuffer() {
		this(DEFAULT_CAPACITY);
	}
	
	public PacketByteBuffer(byte[] data) {
		this(data, 0, data.length, true);
	}
	
	public PacketByteBuffer(byte[] data, int pos, int length, boolean newBuffer) {
		if (!newBuffer) {
			this.data = data;
			this.pos = pos;
			size = length + pos;
		} else {
			this.data = new byte[length];
			this.pos = 0;
			size = length;
			
			System.arraycopy(data, pos, this.data, 0, length);
		}
	}
	
	public PacketByteBuffer(int initialCapacity) {
		data = new byte[initialCapacity];
		
		pos = 0;
		size = 0;
	}

	private void doubleCapacity() {
		setCapacity(data.length << 1);
	}
	
	private void setCapacity(int newCapacity) {
		byte[] tmp = new byte[newCapacity];
		System.arraycopy(data, 0, tmp, 0, data.length);
		data = tmp;
	}
	
	public void putBytes(byte[] data) {
		putBytes(data, 0, data.length);
	}

	public void putBytes(byte[] data, int pos, int length) {
		if (this.pos + length >= this.data.length)
			setCapacity(pos + length + 1);
		
		System.arraycopy(data, pos, this.data, this.pos, length);
		this.pos += length;
		size += length;
	}
	
	public byte[] getBytes(byte[] data) {
		return getBytes(data, 0, data.length);
	}

	public byte[] getBytes(byte[] data, int length) {
		return getBytes(data, 0, length);
	}

	public byte[] getBytes(byte[] data, int pos, int length) {
		if (this.pos + length > size)
			throw new IndexOutOfBoundsException("Position out of bounds!");
			
		System.arraycopy(this.data, this.pos, data, pos, length);
		this.pos += length;
		
		return data;
	}
	
	public void putByte(byte value) {
		if (pos + 1 >= data.length)
			doubleCapacity();
			
		data[pos] = value;
		
		pos++;
		size++;
	}
	
	public byte getByte() {
		if (pos >= size)
			throw new IndexOutOfBoundsException("Position out of bounds!");
		return data[pos++];
	}
	
	public void putShort(short value) {
		putByte((byte)(value >> 0));
		putByte((byte)(value >> 8));
	}

	public short getShort() {
		return (short)((((int)getByte() & 0xFF) << 0) |
		               (((int)getByte() & 0xFF) << 8));
	}
	
	public void putInt(int value) {
		putByte((byte)(value >>  0));
		putByte((byte)(value >>  8));
		putByte((byte)(value >> 16));
		putByte((byte)(value >> 24));
	}

	public int getInt() {
		return (((int)getByte() & 0xFF) <<  0) |
		       (((int)getByte() & 0xFF) <<  8) |
		       (((int)getByte() & 0xFF) << 16) |
		       (((int)getByte() & 0xFF) << 24);
	}

	public void putLong(long value) {
		putByte((byte)(value >>  0L));
		putByte((byte)(value >>  8L));
		putByte((byte)(value >> 16L));
		putByte((byte)(value >> 24L));
		putByte((byte)(value >> 32L));
		putByte((byte)(value >> 40L));
		putByte((byte)(value >> 48L));
		putByte((byte)(value >> 56L));
	}
	
	public long getLong() {
		return (((long)getByte() & 0xFF) <<  0L) |
		       (((long)getByte() & 0xFF) <<  8L) |
		       (((long)getByte() & 0xFF) << 16L) |
		       (((long)getByte() & 0xFF) << 24L) |
		       (((long)getByte() & 0xFF) << 32L) |
		       (((long)getByte() & 0xFF) << 40L) |
		       (((long)getByte() & 0xFF) << 48L) |
		       (((long)getByte() & 0xFF) << 56L);
	}
	
	public void putFloat(float value) {
		putInt(Float.floatToIntBits(value));
	}

	public float getFloat() {
		return Float.intBitsToFloat(getInt());
	}
	
	public void putDouble(double value) {
		putLong(Double.doubleToLongBits(value));
	}

	public double getDouble() {
		return Double.longBitsToDouble(getLong());
	}
	
	public void putUUID(UUID value) {
		putLong(value.getLeastSignificantBits());
		putLong(value.getMostSignificantBits());
	}
	
	public UUID getUUID() {
		long least = getLong();
		long most = getLong();
		return new UUID(most, least);
	}
	
	public boolean getBoolean() {
		return getByte() != 0;
	}
	
	public void putBoolean(boolean value) {
		putByte((byte)(value ? 0x1 : 0x0));
	}
	
	public int getPos() {
		return pos;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getCapacity() {
		return data.length;
	}

	public int remaining() {
		return size - pos;
	}
	
	public void reset() {
		pos = 0;
		size = 0;
	}
	
	public void resetPos() {
		pos = 0;
	}

	public byte[] getData() {
		return data;
	}
}
