package com.rabbit.exchange.direct;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class DirectMessagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DirectMessagingApplication.class, args);
    }


    @Bean
    public ApplicationRunner runner(AmqpTemplate template) {
        return args -> template.convertAndSend("myqueue", "foo");
    }

    @Bean
    public Queue myQueue() {
        return new Queue("myqueue");
    }


    @Component
    static class DirectMessagingConsumer {

        @RabbitListener(queues = "myqueue")
        public void listen(String in) {
            System.out.println(in);
        }

    }

}
