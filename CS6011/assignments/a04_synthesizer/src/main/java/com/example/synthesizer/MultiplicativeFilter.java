package com.example.synthesizer;

public class MultiplicativeFilter implements AudioComponent {
    private AudioComponent input1;
    private AudioComponent input2;

    @Override
    public AudioClip getClip() {
        AudioClip clip1 = input1.getClip();
        AudioClip clip2 = input2.getClip();
        AudioClip result = new AudioClip();
        int length = AudioClip.TOTAL_SAMPLES;

        for (int i = 0; i < length; i++) {
            int sample1 = clip1.getSample(i);
            int sample2 = clip2.getSample(i);
            int multipliedSample = (sample1 * sample2) / Short.MAX_VALUE; // Normalize
            result.setSample(i, multipliedSample);
        }
        return result;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent input) {
        if (input1 == null) {
            input1 = input;
        } else if (input2 == null) {
            input2 = input;
        } else {
            throw new UnsupportedOperationException("MultiplicativeFilter can only take two inputs.");
        }
    }
}
