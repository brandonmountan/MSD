package com.example.synthesizer4a;

public class SquareWave implements AudioComponent {
    private final int frequency;
    private AudioClip clip;

    public SquareWave(int frequency) {
        this.frequency = frequency;
        clip = new AudioClip();
        generateSquareWave();
    }

    private void generateSquareWave() {
        double maxValue = Short.MAX_VALUE;
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            double value = (frequency * i / AudioClip.SAMPLE_RATE) % 1;
            int sample = value > 0.5 ? (int) maxValue : (int) -maxValue;
            clip.setSample(i, sample);
        }
    }

    @Override
    public AudioClip getClip() {
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false; // No input for SquareWave
    }

    @Override
    public void connectInput(AudioComponent input) {
        throw new UnsupportedOperationException("SquareWave does not accept input");
    }
}
