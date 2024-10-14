package com.example.synthesizer;

public class LinearRamp implements AudioComponent {
    @Override
    public AudioClip getClip() {
        AudioClip linearRamp = new AudioClip();
        for (int i = 0; i < 88200; i++){
            linearRamp.setSample(i, (int)(( start * ((float)(linearRamp.TOTAL_SAMPLES) - i) + stop * i ) / (float)(linearRamp.TOTAL_SAMPLES)));
        }
//        sample[i] = ( start * ( numSamples - i ) + stop * i ) / numSamples
        return linearRamp;
    }

    @Override
    public boolean hasInputs() {
        return false;
    }

    @Override
    public void connectInput(AudioComponent ac, int index) {
        assert false;
    }

    LinearRamp(float start, float stop){
        this.start = start;
        this.stop = stop;
    }

    private float start;
    private float stop;
}
