package com.example.demo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// библиотека для методов ввода вывода работы с файлами и получения разных данных файла
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

public class FileDownloadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";// Создаем константу для обращение к папке

    @Override
    // Создаем метод для получения файла 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getPathInfo().substring(1); // получениии имени файла
        Path filePath = Paths.get(UPLOAD_DIR, fileName); // Создание пути для скачивания файла с сервера

        if (!Files.exists(filePath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }// Если файл не существует выдать ошибку

        // Обновить время последнего доступа
        Files.setLastModifiedTime(filePath, FileTime.fromMillis(System.currentTimeMillis()));

        String originalName = fileName.substring(fileName.indexOf('-') + 1);// Сохраняем имя без UID
        response.setContentType("application/octet-stream");// Ставим типо запроса бинарные файлы что он мог потом скачаться
        response.setHeader("Content-Disposition", "attachment; filename=\"" + originalName + "\"");// Чтобы браузер выдал менюшку для скачивания файла

        try (InputStream input = Files.newInputStream(filePath);// Потомк для чтеная файла
             OutputStream output = response.getOutputStream()) {// Поток для отправки файла клиенту от сервера
            byte[] buffer = new byte[8192];// Буфер для перенеса файла с сервера 
            while ((bytesRead = input.read(buffer)) != -1) {// Пененосим в буфер 
                output.write(buffer, 0, bytesRead);// Отправляем файл с сервера
            }
        }
    }
}
