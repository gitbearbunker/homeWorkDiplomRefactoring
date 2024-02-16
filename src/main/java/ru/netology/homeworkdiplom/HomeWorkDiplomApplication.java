package ru.netology.homeworkdiplom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Slf4j
@SpringBootApplication
public class HomeWorkDiplomApplication {
    static Calendar calendar = new GregorianCalendar();

    public static void main(String[] args) {
        log.info("Сервер запущен: {}", calendar.getTime());

        SpringApplication.run(HomeWorkDiplomApplication.class, args);
    }
}