package com.example.synthesizer;

public class VFSineWave implements AudioComponent {
    private AudioComponent input;

    public int frequency;

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        double phase = 0;
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            int frequency = input.getClip().getSample(i);
            phase += 2 * Math.PI * frequency / AudioClip.SAMPLE_RATE;
            int sampleValue = (int) (Short.MAX_VALUE * Math.sin(phase));
            clip.setSample(i,sampleValue);
        }
        return clip;
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
