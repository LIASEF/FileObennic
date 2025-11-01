package com.example.demo;// Пространство имен
// поменял использую Jetty билиотеки на ней можно делать http сервер
import org.eclipse.jetty.server.Server;// Сам http сервер
import org.eclipse.jetty.servlet.ServletContextHandler;// отвечает за разграничение путей localhost/upload1 , localhost upload 1 и так далее
import org.eclipse.jetty.servlet.ServletHolder;// Чттобы описать контреное поведение при запросе клиента
import jakarta.servlet.MultipartConfigElement;// Чтобы сервлет мог быть вызван через форму

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8000);//Создаем объект для запуска сервера

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);// Создаем контекст который будет обрабатывать сервлеты
        context.setContextPath("/");// Работает на корневом пути сервера
        server.setHandler(context);// Устанавиливаем контекст чтобы работал

        ServletHolder uploadHolder = new ServletHolder(new FileUploadServlet());// Создается сервлет для загрузки файлов объект сервлета скорее
        uploadHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("", 104857600L, 115343360L, 0));// Задаем паратры загрузки до 100 mb я задал ибо некоторые файлы не загружались
        context.addServlet(uploadHolder, "/upload");// Добавляем его к пути /upload то есть он будет работать при выполнении этого пути
        context.addServlet(new ServletHolder(new FileDownloadServlet()), "/download/*");
        context.addServlet(new ServletHolder(new FileListServlet()), "/files");
        context.addServlet(new ServletHolder(new StaticFileServlet()), "/*");// Обрабтывает запросы которые не обозначенные ранее чтобы не было ошибок

        // Добавляем сервлеты для скачивания для получения списка в файлов 

        FileCleanupService cleanupService = new FileCleanupService();
        cleanupService.start();// Это сервис для очистки файлов он сам по себе работает и удаляет (30 дней)

        server.start();// Запускаем сервер
        server.join();//  Останавилваем его в случае отказа работы

        cleanupService.stop();// останавливаем сервис при остановке сервера
    }
}
