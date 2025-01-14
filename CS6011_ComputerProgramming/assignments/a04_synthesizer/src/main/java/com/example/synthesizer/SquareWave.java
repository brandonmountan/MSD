package com.example.synthesizer;

public class SquareWave implements AudioComponent {
    private double frequency;

    public SquareWave(double frequency) {
        this.frequency = frequency;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        double maxValue = Short.MAX_VALUE;

        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            double phase = (frequency * i / AudioClip.SAMPLE_RATE) % 1;
            int sample = (phase > 0.5) ? (int)maxValue : (int)-maxValue;
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
        throw new UnsupportedOperationException("SquareWave does not accept inputs.");
    }
}
