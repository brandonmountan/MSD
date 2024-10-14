package com.example.synthesizer;

public interface AudioComponent {
    //    AudioClip getClip() - return the current sound produced by this component
    AudioClip getClip();
// can you connect something to this as an input?
    default boolean hasInputs() { return false;}

    void connectInput(AudioComponent input, int index);

    default int availableInputPorts() { return 0; }

    default void removeInput(AudioComponent input) { assert(false);}
}

//    AudioComponent note = new SineWave( 440 );
//    AudioComponent vol = new VolumeAdjuster();
//    vol.connectInput( note );



