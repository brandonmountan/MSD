package com.example.synthesizer;

public interface AudioComponent {
    //    AudioClip getClip() - return the current sound produced by this component
    AudioClip getClip();
// can you connect something to this as an input?
    default boolean hasInputs() { return false; }

    void connectInput(AudioComponent input);

    default int availableInputPorts() { return 0; }

    default void removeInput(AudioComponent input) { assert(false); }
}



