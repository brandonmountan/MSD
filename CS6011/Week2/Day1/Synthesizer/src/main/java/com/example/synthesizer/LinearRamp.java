package com.example.synthesizer;

public class LinearRamp implements AudioComponent {
    public int start;
    public int stop;

    public LinearRamp(int start, int stop){
        this.start = start;
        this.stop = stop;
    }

    @Override
    public AudioClip getClip() {
        AudioClip clip = new AudioClip();
        for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++){
            float value = ( start * (AudioClip.TOTAL_SAMPLES - i ) + stop * i );
            clip.setSample(i, Math.round(value));
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
    }
}
