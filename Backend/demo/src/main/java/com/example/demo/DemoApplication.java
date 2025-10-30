package com.example.demo;

import org.springframework.boot.SpringApplication;// Запуск приложения 
import org.springframework.boot.autoconfigure.SpringBootApplication;// Для сборки всех зависисомтей проекта чтобы планировщик работал
import org.springframework.scheduling.annotation.EnableScheduling;// Cам планировщик

@SpringBootApplication// Запуск чтобы нажел все зависисомти проекта
@EnableScheduling// Внедряем планировщик задач
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
