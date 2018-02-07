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
		this(data, 0, data.length);
	}
	
	public PacketByteBuffer(byte[] data, int pos, int length) {
		this(length);
		
		System.arraycopy(data, pos, this.data, 0, length);
		size = length;
	}
	
	public PacketByteBuffer(int initialCapacity) {
		data = new byte[initialCapacity];
		
		pos = 0;
		size = 0;
	}

	private void doubleCapacity() {
		byte[] tmp = new byte[data.length << 1];
		System.arraycopy(data, 0, tmp, 0, data.length);
		data = tmp;
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
	
	public int getPos() {
		return pos;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getCapacity() {
		return data.length;
	}
	
	public void reset() {
		pos = 0;
		size = 0;
	}
	
	public byte[] getData() {
		return data;
	}
}
