package com.example.synthesizer;

import java.lang.Math;

public class SineWave implements AudioComponent {
    public double frequency;
    private AudioComponent input;

    SineWave(double frequency) {
        this.frequency = frequency;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        for ( int i = 0; i < AudioClip.TOTAL_SAMPLES; i++ ) {
            double sampleValue = Short.MAX_VALUE * Math.sin( 2 * Math.PI * frequency * i / AudioClip.SAMPLE_RATE );
            clip.setSample(i, (int) sampleValue);
        }
        return clip;
    }

    @Override
    public boolean hasInput() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent ac) {
        assert false;
        System.out.println("no connectInput for SineWave");
    }
}
