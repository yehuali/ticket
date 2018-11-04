package com.ticket;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class TicketGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketGatewayApplication.class, args);
    }

}
