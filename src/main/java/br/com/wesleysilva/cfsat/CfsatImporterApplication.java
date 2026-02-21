package br.com.wesleysilva.cfsat;

import br.com.wesleysilva.cfsat.presentation.console.ConsoleApp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CfsatImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CfsatImporterApplication.class, args);
    }

    @Bean
    CommandLineRunner run(ConsoleApp app) {
        return args -> app.start();
    }
}