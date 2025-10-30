package com.example.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

@Service// Чтобы в любым местах проекта вызывать класс как сервисный
public class FileCleanupService {

    private static final Logger logger = Logger.getLogger(FileCleanupService.class.getName());// Вывод сообщений какие фмлы удалены были ошибки
    private static final String UPLOAD_DIR = "uploads";// Путь к папке
    private static final long DAYS_TO_KEEP = 30; // 30 дней

    @Scheduled(fixedRate = 86400000) // Метод будет выполнять каждый 24 часа
    public void cleanupOldFiles() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                return;
            }// Если папки нет ничего не делаем 

            Files.list(uploadPath)// Получаем все папк
                .filter(Files::isRegularFile)// Работаем с файлами
                .forEach(filePath -> {// Для каждого файла
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);// Получаем базовые атрибуты время создание доступ пследний доступ
                        Instant lastAccessTime = attrs.lastAccessTime().toInstant();// Берем последнее изменение и преобразуем его в Instant для Java удобный для времени
                        Instant cutoff = Instant.now().minus(DAYS_TO_KEEP, ChronoUnit.DAYS);// Получаем время 30 дней назад 

                        if (lastAccessTime.isBefore(cutoff)) {//если последний доступ был раньше чем 30 дней назад тоесть больше 30 дней то удаляем 
                            Files.delete(filePath);
                            logger.info("Удален устаревший файл: " + filePath.getFileName());
                        }
                    } catch (IOException e) {
                        logger.warning("Ошибка при обработке файла " + filePath.getFileName() + ": " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            logger.severe("Ошибка при очистке файлов: " + e.getMessage());
        }
    }
}
