package com.example.demo;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.*;
import java.time.temporal.ChronoUnit;// Нужен считать разницу 
import java.util.concurrent.*;// Что сделать задачу раз в 30 днкй -> планировщик

public class FileCleanupService {// Класс содержит методы для удаления
    private static final String UPLOAD_DIR = "uploads";// Имя папки сервера
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);// Инициализруем планировщик

    public void start() {
        scheduler.scheduleAtFixedRate(this::cleanupOldFiles, 0, 1, TimeUnit.DAYS);// Метод запуска планировщика
    }

    public void stop() {
        scheduler.shutdown();// метод остановки планировщика
    }

    private void cleanupOldFiles() {
        try {
            Files.list(Paths.get(UPLOAD_DIR))// Берем файлы директории
                .filter(Files::isRegularFile) // Фильтр
                .forEach(path -> {// Для каждого файла
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);// Получаем все данные
                        Instant lastAccess = attrs.lastModifiedTime().toInstant();// Время последнего доступа
                        if (lastAccess.isBefore(Instant.now().minus(30, ChronoUnit.DAYS))) {// Если больше 30 дней назад файл не открывался
                            Files.delete(path); // Удаляем
                            System.out.println("Deleted old file: " + path.getFileName());
                        }
                    } catch (IOException e) {
                        System.err.println("Error processing file: " + path.getFileName());
                    }
                });
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}
