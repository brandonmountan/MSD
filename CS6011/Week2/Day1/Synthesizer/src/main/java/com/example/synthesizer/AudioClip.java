package com.example.synthesizer;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AudioClip {
//    static constants for the duration, and sample rate (2.0 seconds, and 44100 respectively)
    public static final double duration = 2.0;
    public static final double sampleRate = 44100;
    public static final int TOTAL_SAMPLES = 44100;
    //    a member variable that contains the actual byte array
    byte[] audioData = new byte[(int)(duration*sampleRate*2)];
    //    methods int getSample( index ) and setSample( index, value ) that return/set the sample passed as an int.
    //    You will need to use bitwise operators to perform these conversions! The ints that are passed/returned should
    //    be in the range of shorts. These are the closest thing we can do in Java to overloading operator[].
    int getSample(int index) {
        int sample = (audioData[index*2 + 1] << 8) | (audioData[index*2] & 0xff);
        return sample;
    };

    int setSample(int index, int value) {
        byte leftByte = (byte)value;
        byte rightByte = (byte)(value >> 8);
        audioData[index*2 + 1] = rightByte;
        audioData[index*2] = leftByte;
        return value;
    };

//    A method byte[] getData() that returns our array (returning a copy isn't a bad idea to avoid issues of aliasing,
//    check out Arrays.copyOf). We need this method because the Java library that actually plays sounds expects our data
//    as an array of bytes.
    byte[] getData() {
        return Arrays.copyOf(audioData, (int) (duration*sampleRate*2));
    };
}
