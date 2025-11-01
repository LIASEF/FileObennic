package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;// преобразование из json в java объекты
// Библиотеки для работы с сервлетами 
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
//Для работы с файлами потоками чтобы записывать в переменными во время работы
import java.io.*;
// Для генерации путей к файлам на сервере 

import java.nio.file.*;
// Для отправки на клиент ответов о состоянии
import java.util.HashMap;
import java.util.Map;
// Генерация уникальных индефикаторов для уникальных ссылок на скачивание
import java.util.UUID;

@MultipartConfig// Анотация  чтобы multipart form data запросов чтобы к форма html могла работать с файлами
public class FileUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";
    private final ObjectMapper objectMapper = new ObjectMapper();// Создаем переменную хранищает методы для преобразований в json ответ и наоброт
    // Создаем класс в котором будет содержаться методы загрузки файла на сервер

    @Override // Переопределяем метод инициализации класса надо чтобы при каждйо инициализации проверялсы папка сервера если ее нету то создаем
    public void init() throws ServletException {
        File dir = new File(UPLOAD_DIR);// Создаем программно директорию uploads
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Проверка что файла не сеществект
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Файл не выбран");
                objectMapper.writeValue(response.getWriter(), error);// Записываем ошибку в ответ на запрос и передает ошибку
                return;
            }

            String submittedFileName = filePart.getSubmittedFileName();
            String originalFileName = (submittedFileName != null) ? Paths.get(submittedFileName).getFileName().toString() : "unknown";
            String fileName = UUID.randomUUID().toString() + "-" + originalFileName;
            Path filePath = Paths.get(UPLOAD_DIR, fileName); // Создаем пути к файлу на серверу

            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);// Читаем сам файл сохраняем его по пути папки uploads
            }

            String downloadUrl = "http://localhost:8000/download/" + fileName;
            Map<String, String> result = new HashMap<>();
            result.put("downloadUrl", downloadUrl);
            objectMapper.writeValue(response.getWriter(), result);// Формируем ссылку и в качестве ответа отпраыляем ее в json формате
        } catch (Exception e) {
            System.err.println("Error uploading file: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ошибка при загрузке файла");
            objectMapper.writeValue(response.getWriter(), error);
        }
    }
}
