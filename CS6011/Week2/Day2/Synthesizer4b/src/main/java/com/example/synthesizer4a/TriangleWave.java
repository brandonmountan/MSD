package com.example.synthesizer4a;

public class TriangleWave implements AudioComponent {
    private final int frequency;
    private AudioClip clip;

    public TriangleWave(int frequency) {
        this.frequency = frequency;
        clip = new AudioClip();
        generateTriangleWave();
    }

    private void generateTriangleWave() {
        double maxValue = Short.MAX_VALUE;
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            double normalized = (2.0 * frequency * i / AudioClip.SAMPLE_RATE) % 2;
            int sample = (int) (maxValue * (1 - Math.abs(normalized - 1))); // Create triangle wave shape
            sample = (normalized < 1) ? sample : -sample;
            clip.setSample(i, sample);
        }
    }

    @Override
    public AudioClip getClip() {
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false; // No input for TriangleWave
    }

    @Override
    public void connectInput(AudioComponent input) {
        throw new UnsupportedOperationException("TriangleWave does not accept input");
    }
}
