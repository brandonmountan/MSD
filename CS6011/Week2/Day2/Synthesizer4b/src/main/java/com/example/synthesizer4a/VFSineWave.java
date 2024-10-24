package com.example.synthesizer4a;

public class VFSineWave implements AudioComponent {
    private AudioComponent input;
    private double phase;

    @Override
    public AudioClip getClip() {
        if (input == null) throw new IllegalStateException("Input not connected");
        AudioClip clip = new AudioClip();
        AudioClip frequencyClip = input.getClip();
        phase = 0;

        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
            double frequency = frequencyClip.getSample(i);
            phase += 2 * Math.PI * frequency / AudioClip.SAMPLE_RATE;
            clip.setSample(i, (int) (Short.MAX_VALUE * Math.sin(phase)));
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
