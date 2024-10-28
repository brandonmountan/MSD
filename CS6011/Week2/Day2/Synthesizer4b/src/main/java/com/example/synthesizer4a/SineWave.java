package com.example.synthesizer4a;

public class SineWave implements AudioComponent {
    private int frequency;
    private AudioClip clip;

    public SineWave(int frequency) {
        this.frequency = frequency;
        clip = new AudioClip();
        generateSineWave();
    }

    private void generateSineWave() {
        double maxValue = Short.MAX_VALUE;
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            double sample = maxValue * Math.sin(2 * Math.PI * frequency * i / AudioClip.SAMPLE_RATE);
            clip.setSample(i, (int) sample);
        }
    }

    @Override
    public AudioClip getClip() {
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false; // No input for SineWave
    }

    @Override
    public void connectInput(AudioComponent input) {
        throw new UnsupportedOperationException("SineWave does not accept input");
    }
}
