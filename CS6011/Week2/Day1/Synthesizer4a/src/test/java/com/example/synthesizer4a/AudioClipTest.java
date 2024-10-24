package com.example.synthesizer4a;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AudioClipTest {
    @Test
    public void testSetGetSample() {
        AudioClip clip = new AudioClip();
        clip.setSample(0, 1000);
        assertEquals(1000, clip.getSample(0));

        clip.setSample(0, Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, clip.getSample(0));

        clip.setSample(0, Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, clip.getSample(0));
    }

    @Test
    public void testBoundaryConditions() {
        AudioClip clip = new AudioClip();
        assertThrows(IndexOutOfBoundsException.class, () -> clip.getSample(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> clip.setSample(AudioClip.TOTAL_SAMPLES, 0));
    }
}
