package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}
}

//добавить зависимости
// 1 JdbsTemplate без версии
// MVN repository найти для БД H2
//            <scope>test</scope> - убрать
//application.properties - create

//DataBaseCustomerStorage @Primary @Repository @RequiredArgsConstructor