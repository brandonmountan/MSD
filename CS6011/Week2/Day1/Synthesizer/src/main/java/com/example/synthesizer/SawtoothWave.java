package com.example.synthesizer;

public class SawtoothWave implements AudioComponent {
    private double frequency;

    public SawtoothWave(double frequency) {
        this.frequency = frequency;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        double maxValue = Short.MAX_VALUE;
        double period = AudioClip.SAMPLE_RATE / frequency;

        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            double x = (i % period) / period;
            int sample = (int)(maxValue * (2 * x - 1));
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
        throw new UnsupportedOperationException("SawtoothWave does not accept inputs.");
    }
}
