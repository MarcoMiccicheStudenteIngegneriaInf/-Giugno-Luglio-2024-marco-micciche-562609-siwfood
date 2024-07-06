package it.uniroma3.siw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * La classe principale per l'applicazione SIW Food.
 * Questo è il punto di ingresso dell'applicazione Spring Boot.
 */
@SpringBootApplication
public class SiwFoodApplication {

    /**
     * Il metodo principale per eseguire l'applicazione Spring Boot.
     * 
     * @param args argomenti della riga di comando (non usati)
     */
    public static void main(String[] args) {
        SpringApplication.run(SiwFoodApplication.class, args);
    }
}
