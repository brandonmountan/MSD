package com.example.synthesizer;

public class VolumeAdjuster implements AudioComponent {
    private AudioComponent input; // setInput()
    public double scale;

    VolumeAdjuster(int scale) {
        this.scale = scale;
    }

    @Override
    public AudioClip getClip() {
        AudioClip original = input.getClip();
        AudioClip adjusted = new AudioClip();
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int sample = original.getSample(i);
            int adjustedSample = (int) (sample * scale);
            adjusted.setSample(i, adjustedSample);
        }
        return adjusted;
    };

    //    boolean hasInput() - can you connect something to this as an input?
    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput( AudioComponent input ) {
        this.input = input;
    };
}