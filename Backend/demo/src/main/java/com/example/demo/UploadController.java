package com.example.demo; // Указывает на пакет, в котором находится данный класс
// Импорт классов из библиотеки Spring
import org.springframework.http.HttpStatus; 
// HttpStatus — это перечисление, которое содержит стандартные коды состояния HTTP (например, 200 OK, 404 Not Found, 500 Internal Server Error).
import org.springframework.http.ResponseEntity; 
// ResponseEntity — класс, который представляет собой HTTP-ответ. Он позволяет задать тело ответа (например, строку, объект или другие данные),
import org.springframework.web.bind.annotation.*; 
// Аннотации для работы с HTTP-запросами в Spring: ну то есть если запрос по этой ветке то идет отправление
import org.springframework.web.multipart.MultipartFile; 
// MultipartFile — это интерфейс, представляющий файл, загруженный через HTTP-запрос. Для работы с файлом от клиента
import java.io.File; 
// File — класс из стандартной библиотеки Java, представляющий файл или директорию в файловой системе.

import java.io.IOException; 
// IOException — это исключение, которое может возникать при выполнении операций ввода/вывода// Для граммонтых исключений

import java.nio.file.Files; 
// Files — утилитный класс, содержащий статические методы для работы с файлами и папками.
import java.nio.file.Path; 
//это хранит путь к файлу

import java.nio.file.Paths; 
// Прероразование строковвого пути в путь объекта Path

import java.util.UUID; 
//Генерация уникальных индедификатор
// Объявдяем контроллер для обратоки запросов от клиента
@RestController
// Энд поинт для загрузки файла
@RequestMapping("/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads"; // Папка где будут файлы

    static {
        //Создаем папку если не существует
        
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    //Этот поинт заработает когда upload будет вызван

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {// Получаем от клиента файл
        if (file.isEmpty()) {
            return new ResponseEntity<>("Файл не выбран", HttpStatus.BAD_REQUEST);
        }

        try {
            // Генерация уникального имени для файла
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR, fileName);

            // Сохранение файла на сервере
            Files.copy(file.getInputStream(), path);

            // Генерация ссылки на скачивание
            String downloadUrl = "http://localhost:8080/download/" + fileName;

            // Возвращаем успешный ответ с URL для скачивания
            return ResponseEntity.ok(new UploadResponse(downloadUrl));

        } catch (IOException e) {
            return new ResponseEntity<>("Ошибка при сохранении файла", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint для скачивания файла
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        Path path = Paths.get(UPLOAD_DIR, fileName);// Создаем путь файла на серверу

        if (Files.exists(path)) {
            try {
                byte[] fileContent = Files.readAllBytes(path);
                return ResponseEntity.ok(fileContent);//Считываем весь файл в массив байтов
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//Вернуть ошибку сервера при ошибке загрузки файла
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);// Если файла не найден
        }
    }

    // Класс для хранения URL для скачивания
    static class UploadResponse {
        private String downloadUrl;// ссылка переменная

        public UploadResponse(String downloadUrl) {
            this.downloadUrl = downloadUrl;//получаем ссылку
        }

        public String getDownloadUrl() {
            return downloadUrl;//Возращаем при надобности
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;// Если URL может меняться
        }
    }
}
