package com.example.synthesizer;

import java.util.Random;

public class WhiteNoise implements AudioComponent {
    private double maxValue;
    private Random random = new Random();

    public WhiteNoise(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();

        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int sample = (int)(random.nextDouble() * 2 * maxValue - maxValue); // Random between -maxValue and maxValue
            clip.setSample(i, sample);
        }
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent input) {
        throw new UnsupportedOperationException("WhiteNoise does not accept inputs.");
    }
}
