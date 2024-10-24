package com.example.synthesizer4a;

import javax.sound.sampled.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Clip clip = AudioSystem.getClip();
        AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);

        AudioComponent sineWave = new SineWave(440); // A note
        AudioClip audioClip = sineWave.getClip();

        clip.open(format16, audioClip.getData(), 0, audioClip.getData().length);
        System.out.println("About to play...");
        clip.start();
        clip.loop(2); // Loop the sound

        while (clip.getFramePosition() < AudioClip.TOTAL_SAMPLES || clip.isActive() || clip.isRunning()) {
            // Wait for the note to finish
        }

        System.out.println("Done.");
        clip.close();
    }
}
