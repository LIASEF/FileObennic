package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
// Для работы со временем
import java.time.*;
import java.time.format.DateTimeFormatter;
// Для обработки потоеов и ввода вывода и преобразований в list
import java.util.*;
import java.util.stream.Collectors;

public class FileListServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");// Отправляем запрос клиенту в формате json

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");// Создаем форматировщик который будет преобразовыввать формат по этому  формату

        List<Map<String, String>> files = Files.list(Paths.get(UPLOAD_DIR))// Получаем список файлов в директории
            .filter(Files::isRegularFile)// Фильтр чтобы брать только файлы не директории
            .map(path -> {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);// Чтение базовых атрибутов файла
                    String fileName = path.getFileName().toString();// Берем имя файла
                    String originalName = fileName.substring(fileName.indexOf('-') + 1);// Берем его без uid
                    String downloadUrl = "http://localhost:8000/download/" + fileName; // Cоздаем url для скачивания
                    String creationDate = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault()).format(formatter);// Получаем время создания файла
                    String uploadDate = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault()).format(formatter);// Дата загрузки
                    String lastModifiedDate = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault()).format(formatter);// Дата посленего изменения

                    Map<String, String> fileInfo = new HashMap<>();// Создаем map и заполняем его 
                    fileInfo.put("name", originalName);
                    fileInfo.put("downloadUrl", downloadUrl);
                    fileInfo.put("creationDate", creationDate);
                    fileInfo.put("uploadDate", uploadDate);
                    fileInfo.put("lastModifiedDate", lastModifiedDate);
                    return fileInfo;
                } catch (IOException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)// Убираем null значтения
            .collect(Collectors.toList());// Преобразуем данные в виде list списка

        objectMapper.writeValue(response.getWriter(), files);// Отправляем после работы ответ клиенту
    }
}
