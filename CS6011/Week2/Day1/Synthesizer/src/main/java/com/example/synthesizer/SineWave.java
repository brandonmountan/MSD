package com.example.synthesizer;

import java.lang.Math;

public class SineWave implements AudioComponent {
    int frequency_;
    SineWave(int frequency) {
        this.frequency_ = frequency;
    }

    public AudioClip getClip() {
        AudioClip audioclip = new AudioClip();
        // produce sin wave
        // sample[ i ] = maxValue * sine( 2*pi*frequency * i / sampleRate );
        for (int i = 0; i < 88200; i++) {
            int maxValue = 15000;
            audioclip.setSample(i, (int)(maxValue * Math.sin( 2 * Math.PI * frequency_ * i / 44100 )));
        }
        return audioclip;
    }

    @Override
    public Boolean hasInputs() {
        return null;
    }

    @Override
    public void connectInput(AudioComponent ac, int index) {
        System.out.println("no connectInput for SineWave");
    }

    // connectInput() -> throw error
    // AudioComponent ac = null
    // set freq()...
}
