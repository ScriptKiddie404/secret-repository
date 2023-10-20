package com.fernando.puentes.app;

import com.fernando.puentes.app.service.AlldatumService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AlldatumTestApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AlldatumTestApplication.class, args);
        AlldatumService service = context.getBean(AlldatumService.class);
        service.obtenerYMostrarPersonas();
        context.close();
    }

}
