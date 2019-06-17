package com.microsoft.demoai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class DemoaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoaiApplication.class, args);
	}

	@GetMapping("/")
    public void home()  {
       
    }

}
