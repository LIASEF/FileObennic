package com.example.demo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;

public class StaticFileServlet extends HttpServlet {
    private static final String STATIC_DIR = "src/main/resources/static";// Папка где index html сама логика клиента

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();// Получаем путь до файла
        if (path == null || path.equals("/")) {
            path = "/index.html";// Если пустой то путь index html
        }

        Path filePath = Paths.get(STATIC_DIR, path);// Созданаем полный путь до файла
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {// проверка если файла нет или это папка ошибка
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getContentType(path);// Сохраняем тип файла для брауеза
        response.setContentType(contentType);// Сохраняем это ответа клиенту

        try (InputStream input = Files.newInputStream(filePath);
             OutputStream output = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead); // отправлям файл клиенту
            
            }
        }
    }
    // Определяем какой MIME типо возвращать браузеру чтобы он знал как работать с файлом

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        return "application/octet-stream";
    }
}
