package com.example.synthesizer;

public class VFSineWave implements AudioComponent {
    @Override
    public AudioClip getClip() {
        AudioClip vfSineWave = input.getClip();
        double phase = 0;
        for (int i = 0; i < 88200; i++) {
            phase += 2 * Math.PI * vfSineWave.getSample(i) / 88200;
            vfSineWave.setSample(i, (int)(15000 * Math.sin(phase)));
        }
        return vfSineWave;
    }

    @Override
    public Boolean hasInputs() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent input, int index) {
        this.input = input;
    }

    private AudioComponent input;
}
