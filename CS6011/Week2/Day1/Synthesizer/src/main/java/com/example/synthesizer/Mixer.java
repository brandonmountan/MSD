package com.example.synthesizer;

import java.util.ArrayList;

public class Mixer implements AudioComponent {

    @Override
    public AudioClip getClip() {
        AudioClip mixedClip = new AudioClip();
        ArrayList<AudioClip> clips = new ArrayList<>();
        for (AudioComponent ac : inputs) {
            clips.add(ac.getClip());
        }
        int sample = 0;
        for (int i = 0; i < 88200; i++) {
            for (AudioClip clip : clips) {
                sample += clip.getSample(i);
                if (sample > 32000) {
                    sample = 32000;
                } else if (sample < -32000) {
                    sample = -32000;
                }
                mixedClip.setSample(i, sample);
            }
        }
        return mixedClip;
    }

    @Override
    public boolean hasInputs() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent input) {
        inputs.add(input);
    }

    ArrayList<AudioComponent> inputs = new ArrayList<>();
}
