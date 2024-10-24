package com.example.synthesizer4a;

public class LinearRamp implements AudioComponent {
    private final int start;
    private final int stop;
    private AudioComponent input;

    public LinearRamp(int start, int stop) {
        this.start = start;
        this.stop = stop;
    }

    public void connectInput(AudioComponent input) {
        this.input = input;
    }

    @Override
    public AudioClip getClip() {
        if (input == null) throw new IllegalStateException("Input not connected");
        AudioClip clip = new AudioClip();
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            float rampSample = (float) (start * (AudioClip.TOTAL_SAMPLES - i) + stop * i) / AudioClip.TOTAL_SAMPLES;
            clip.setSample(i, (int) rampSample);
        }
        return clip;
    }

    @Override
    public boolean hasInput() {
        return true;
    }
}
