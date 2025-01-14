package com.example.synthesizer;

public class Reverb implements AudioComponent {
    private AudioComponent input;
    private int delay; // Delay in samples
    private double scale; // Scale for the reverb effect

    public Reverb(int delay, double scale) {
        this.delay = delay;
        this.scale = scale;
    }

    @Override
    public AudioClip getClip() {
        AudioClip original = input.getClip();
        AudioClip result = new AudioClip();
        int length = AudioClip.TOTAL_SAMPLES;

        for (int i = 0; i < length; i++) {
            int currentSample = original.getSample(i);
            int delayedSample = (i >= delay) ? (int)(scale * original.getSample(i - delay)) : 0;
            result.setSample(i, currentSample + delayedSample);
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
