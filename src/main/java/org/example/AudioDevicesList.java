package org.example;

import javax.sound.sampled.*;

public class AudioDevicesList {
    public static void main(String[] args) {
        System.out.println("Доступные аудиоустройства:");

        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixers.length; i++) {
            System.out.println(i + ": " + mixers[i].getName() + " - " + mixers[i].getDescription());
        }
    }
}
