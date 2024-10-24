package com.example.synthesizer4a;

import java.util.Arrays;

public class AudioClip {
    public static final int DURATION = 2; // seconds
    public static final int SAMPLE_RATE = 44100; // samples per second
    public static final int TOTAL_SAMPLES = DURATION * SAMPLE_RATE; // total samples

    private byte[] data;

    public AudioClip() {
        data = new byte[TOTAL_SAMPLES * 2]; // 2 bytes per sample (16 bits)
    }

    public int getSample(int index) {
        if (index < 0 || index >= TOTAL_SAMPLES) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return (data[2 * index] & 0xFF) | (data[2 * index + 1] << 8);
    }

    public void setSample(int index, int value) {
        if (index < 0 || index >= TOTAL_SAMPLES) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        value = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, value)); // Clamping
        data[2 * index] = (byte) (value & 0xFF);
        data[2 * index + 1] = (byte) ((value >> 8) & 0xFF);
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length); // Return a copy to avoid aliasing
    }
}