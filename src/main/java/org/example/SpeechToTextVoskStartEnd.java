package org.example;

import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.util.Scanner;

public class SpeechToTextVoskStartEnd {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Загружаем модель
            Model model = new Model("C:\\Users\\Andrs\\IdeaProjects\\Vosk\\src\\main\\resources\\vosk-model-small-ru-0.22");

            // Настройка формата аудио и микрофона
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Микрофон не поддерживается.");
                return;
            }

            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            Recognizer recognizer = new Recognizer(model, 16000);

            byte[] buffer = new byte[4096];

            while (true) {
                System.out.println("Введите '1', чтобы начать запись, или '0', чтобы выйти:");
                String input = scanner.nextLine();

                if ("1".equals(input)) {
                    System.out.println("Запись...Говорите!");

                    while (true) {
                        if (System.in.available() > 0) {
                            String command = scanner.nextLine();
                            if ("0".equals(command)) {
                                System.out.println("Запись остановлена.");
                                break;
                            }
                        }

                        int bytesRead = microphone.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            recognizer.acceptWaveForm(buffer, bytesRead);
                        }
                    }

                    // Вывод финального результата
                    System.out.println("Распознанный текст: " + recognizer.getResult());

                } else if ("0".equals(input)) {
                    System.out.println("Выход.");
                    break;
                }
            }

            microphone.stop();
            microphone.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
