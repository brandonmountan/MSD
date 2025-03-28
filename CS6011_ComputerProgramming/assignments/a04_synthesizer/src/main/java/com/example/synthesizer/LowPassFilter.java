package com.example.synthesizer;

public class LowPassFilter implements AudioComponent {
    private AudioComponent input;
    private double alpha;

    public LowPassFilter(double alpha) {
        this.alpha = alpha; // 0 < alpha < 1
    }

    @Override
    public AudioClip getClip() {
        AudioClip original = input.getClip();
        AudioClip result = new AudioClip();
        int length = AudioClip.TOTAL_SAMPLES;

        int previousSample = 0;
        for (int i = 0; i < length; i++) {
            int currentSample = original.getSample(i);
            int filteredSample = (int)(alpha * currentSample + (1 - alpha) * previousSample);
            result.setSample(i, filteredSample);
            previousSample = filteredSample;
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent input) {
        this.input = input;
    }
}
