package com.example.synthesizer4a;

public class Reverb implements AudioComponent {
    private final AudioComponent input;
    private final int delay;
    private final double scale;

    public Reverb(AudioComponent input, int delay, double scale) {
        this.input = input;
        this.delay = delay;
        this.scale = scale;
    }

    @Override
    public AudioClip getClip() {
        AudioClip original = input.getClip();
        AudioClip result = new AudioClip();

        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int sample = original.getSample(i);
            result.setSample(i, sample);

            if (i + delay < AudioClip.TOTAL_SAMPLES) {
                int delayedSample = (int) (sample * scale);
                result.setSample(i + delay, result.getSample(i + delay) + delayedSample);
            }
        }

        return result;
    }

    @Override
    public boolean hasInput() {
        return true; // Has input
    }

    @Override
    public void connectInput(AudioComponent input) {
        // Already assigned in the constructor
    }
}
