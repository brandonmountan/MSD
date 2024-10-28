package com.example.synthesizer;

import java.util.Arrays;

public class AudioClip {
    public static final int DURATION = 2;
    public static final int SAMPLE_RATE = 44100;
    public static final int TOTAL_SAMPLES = DURATION * SAMPLE_RATE;

    private byte[] data;

    public AudioClip() {
        data = new byte[TOTAL_SAMPLES * 2];
    }
    public int getSample( int index ) {
        int lowByte = data[2 * index] & 0xff;
        int highByte = data[2 * index + 1] & 0xff;
        return (highByte << 8) | lowByte;
    };

    public void setSample( int index, int value ) {
        value = Math.max( Short.MIN_VALUE, Math.min(Short.MAX_VALUE, value) ); // Clamping
        data[2 * index] = ( byte ) ( value & 0xFF ); // Low byte
        data[2 * index + 1] = ( byte ) ( (value >> 8) & 0xFF ); // High byte
    };

    public byte[] getData() {
        return Arrays.copyOf( data, data.length );
    };
}
