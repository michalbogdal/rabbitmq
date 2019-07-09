package com.rabbit.transactions;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitMessagingApplication {


    public static final String FANOUT_EXCHANGE = "ex.fanout-events";

    public static void main(String[] args) {
        SpringApplication.run(RabbitMessagingApplication.class, args);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //important to execute in transaction
        rabbitTemplate.setChannelTransacted(true);
        return rabbitTemplate;
    }

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

}
