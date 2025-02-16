package org.example;


import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;

public class SpeechToTextVoskTimeout {
    public static void main(String[] args) {
        try {

            // Загружаем модель
            Model model = new Model("C:\\Users\\Andrs\\IdeaProjects\\untitled\\src\\main\\java\\org\\example\\vosk-model-small-ru-0.22");

            // Настройка формата аудио и микрофона
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Микрофон не поддерживается.");
                return;
            }

            // Открытие микрофона
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            // Создаем Recognizer для распознавания речи
            Recognizer recognizer = new Recognizer(model, 16000);

            // Начало прослушивания
            System.out.println("Запись... Говорите что-нибудь!");

            // Буфер для хранения данных
            byte[] buffer = new byte[4096];

            long startTime = System.currentTimeMillis();
            long timeout = 5000; // Время записи в миллисекундах (5 секунд)

            // Запуск распознавания речи
            while (System.currentTimeMillis() - startTime < timeout) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);

                if (bytesRead > 0) {
                    recognizer.acceptWaveForm(buffer, bytesRead);  // Отправляем данные на распознавание
                }
            }

            // Получение финального распознанного текста
            String result = recognizer.getResult();

            if (result.isEmpty()) {
                System.out.println("Не удалось распознать речь.");
            } else {
                System.out.println("Распознанный текст: " + result);
            }

            // Закрытие микрофона
            microphone.stop();
            microphone.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}