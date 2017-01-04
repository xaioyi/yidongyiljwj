package com.sectong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class YidongyiljwjApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(YidongyiljwjApplication.class);
    }

    //@SpringBootApplication
//public class YidongyiljwjApplication {
    public static void main(String args[]) {
        SpringApplication.run(YidongyiljwjApplication.class, args);
    }

}
