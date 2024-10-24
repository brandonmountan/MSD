package com.example.synthesizer4a;

import java.util.Random;

public class WhiteNoise implements AudioComponent {
    private AudioClip clip;
    private Random random = new Random();

    public WhiteNoise() {
        clip = new AudioClip();
        generateWhiteNoise();
    }

    private void generateWhiteNoise() {
        double maxValue = Short.MAX_VALUE;
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int sample = random.nextInt((int) maxValue * 2 + 1) - (int) maxValue; // Random between -maxValue and maxValue
            clip.setSample(i, sample);
        }
    }

    @Override
    public AudioClip getClip() {
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false; // No input for WhiteNoise
    }

    @Override
    public void connectInput(AudioComponent input) {
        throw new UnsupportedOperationException("WhiteNoise does not accept input");
    }
}
