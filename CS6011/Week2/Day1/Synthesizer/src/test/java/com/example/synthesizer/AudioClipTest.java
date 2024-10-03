package com.example.synthesizer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AudioClipTest {

    @Test
    public void runAllTests() {
        AudioClip audioClip = new AudioClip();
        audioClip.setSample(0, 1);
        audioClip.setSample(1, 5);
        Assertions.assertEquals(audioClip.getSample(0), 1);
        Assertions.assertEquals(audioClip.getSample(1), 5);

        audioClip.setSample(2, Short.MAX_VALUE);
        Assertions.assertEquals(audioClip.getSample(2), Short.MAX_VALUE);
        audioClip.setSample(3, Short.MIN_VALUE);
        Assertions.assertEquals(audioClip.getSample(3), Short.MIN_VALUE);

        audioClip.setSample(4, -8);
        Assertions.assertEquals(audioClip.getSample(4), -8);



    }
}