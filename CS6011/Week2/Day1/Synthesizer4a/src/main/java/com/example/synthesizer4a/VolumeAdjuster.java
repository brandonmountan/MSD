package com.example.synthesizer4a;

public class VolumeAdjuster implements AudioComponent {
    private final double scale;
    private AudioComponent input;

    public VolumeAdjuster(double scale) {
        this.scale = scale;
    }

    @Override
    public AudioClip getClip() {
        if (input == null) {
            throw new IllegalStateException("No input connected");
        }

        AudioClip original = input.getClip();
        AudioClip result = new AudioClip();

        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int adjustedSample = (int) (original.getSample(i) * scale);
            result.setSample(i, Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, adjustedSample))); // Clamping
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
