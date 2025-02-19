package org.example;

import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class VoskSpeechRecognizer {

    private static Model model;
    private static Recognizer recognizer;
    private static TargetDataLine microphone;
    private static final String RESULT_FILE_PATH = "C:\\Users\\Andrs\\IdeaProjects\\Vosk\\src\\main\\resources\\output.txt";
    private static final String MODEL_PATH = "C:\\Users\\Andrs\\IdeaProjects\\Vosk\\src\\main\\resources\\vosk-model-small-ru-0.22";

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Загружаем модель
            model = new Model(MODEL_PATH);
            recognizer = new Recognizer(model, 16000);

            while (true) {
                System.out.println("Введите '1', чтобы начать запись, или '0', чтобы выйти:");
                String input = scanner.nextLine();

                if ("1".equals(input)) {
                    startRecording();
                } else if ("0".equals(input)) {
                    System.out.println("Выход.");
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для начала записи
    private static void startRecording() throws LineUnavailableException, IOException {
        System.out.println("Запись... Говорите!");
        setupMicrophone();

        byte[] buffer = new byte[4096];
        while (true) {
            // Ожидаем команды для остановки записи
            if (System.in.available() > 0) {
                Scanner scanner = new Scanner(System.in);
                String command = scanner.nextLine();
                if ("0".equals(command)) {
                    stopRecording();
                    break;
                }
            }

            int bytesRead = microphone.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                recognizer.acceptWaveForm(buffer, bytesRead);
            }
        }

        // Записываем распознанный текст в файл и сразу выводим его в консоль
        saveRecognitionResultToFile();
        printRecognitionResultsFromFile();
    }

    // Метод для настройки и работы с микрофоном
    private static void setupMicrophone() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Микрофон не поддерживается.");
            return;
        }

        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();
    }

    // Метод для остановки записи
    private static void stopRecording() {
        System.out.println("Запись остановлена.");
        microphone.stop();
        microphone.close();
    }

    // Метод для сохранения распознанного текста в файл
    private static void saveRecognitionResultToFile() {
        String resultText = recognizer.getResult();
        try (FileWriter writer = new FileWriter(RESULT_FILE_PATH, true)) {
            writer.write(resultText + "\n");
            System.out.println("Распознанный текст сохранен в файл.");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении результата в файл: " + e.getMessage());
        }
    }

    // Метод для чтения и вывода текста из файла
    private static void printRecognitionResultsFromFile() {
        File file = new File(RESULT_FILE_PATH);
        if (!file.exists()) {
            System.out.println("Файл с результатами не найден.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            System.out.println("Распознанный текст:");
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
