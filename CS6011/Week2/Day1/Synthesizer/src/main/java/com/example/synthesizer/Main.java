package com.example.synthesizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.*;

public class Main {

    public static void main(String[] args) throws LineUnavailableException {

        // sinewave sw = new sinewave()
        // volumeadjuster va = new volumeadjuster()
        // va.setvol(1.0)
        // va.connectinput(sw)
        // audioclip ac = va.getclip()

        // mixer will have AudioComponent[] connections. takes in volume component and vol

// Get properties from the system about samples rates, etc.
// AudioSystem is a class from the Java standard library.
        Clip c = AudioSystem.getClip(); // Note, this is different from our AudioClip class.

// This is the format that we're following, 44.1 KHz mono audio, 16 bits per sample.
        AudioFormat format16 = new AudioFormat( 44100, 16, 1, true, false );

        AudioComponent audioComponent1 = new SineWave(440);

        // TEST sinewave
//        SineWave sineWave = new SineWave(440);
//        AudioClip audioClip = sineWave.getClip();

        // TEST volume adjuster
//        VolumeAdjuster volumeAdjuster = new VolumeAdjuster(2);
//        volumeAdjuster.connectInput(audioComponent1, 0);
//        AudioClip audioClip = volumeAdjuster.getClip();

        // TEST mixer
        // try turning down volume before playing so using volume adjuster
//        AudioComponent audioComponent2 = new SineWave(220);
//        Mixer mixer = new Mixer();
//        mixer.connectInput(audioComponent1, 0);
//        mixer.connectInput(audioComponent2, 0);
//        AudioClip audioClip = mixer.getClip();

        // TEST linear ramp vfsine wave
        LinearRamp linearRamp = new LinearRamp(50, 2000);
        VFSineWave vfSineWave = new VFSineWave();
        vfSineWave.connectInput(linearRamp, 0);
        AudioClip audioClip = vfSineWave.getClip();

        c.open( format16, audioClip.getData(), 0, audioClip.getData().length ); // Reads data from our byte array to play it.

        System.out.println( "About to play..." );
        c.start(); // Plays it.
        c.loop( 2 ); // Plays it 2 more times if desired, so 6 seconds total

// Makes sure the program doesn't quit before the sound plays.
        while( c.getFramePosition() < AudioClip.TOTAL_SAMPLES || c.isActive() || c.isRunning() ){
            // Do nothing while we wait for the note to play.
        }

        System.out.println( "Done." );
        c.close();
    }
}
