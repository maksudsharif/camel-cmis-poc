package com.maksudsharif.camelpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class CamelPocApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CamelPocApplication.class, args);
    }
}
