package dev.sumuks.simplefileconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TextServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TextServiceApplication.class, args);
	}

}
