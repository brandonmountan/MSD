package com.example.synthesizer4a;

public class LowPassFilter implements AudioComponent {
    private final AudioComponent input;
    private final double alpha; // Smoothing factor

    public LowPassFilter(AudioComponent input, double alpha) {
        this.input = input;
        this.alpha = alpha;
    }

    @Override
    public AudioClip getClip() {
        AudioClip original = input.getClip();
        AudioClip result = new AudioClip();
        int lastSample = 0;

        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int sample = original.getSample(i);
            lastSample = (int) (alpha * sample + (1 - alpha) * lastSample);
            result.setSample(i, lastSample);
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
