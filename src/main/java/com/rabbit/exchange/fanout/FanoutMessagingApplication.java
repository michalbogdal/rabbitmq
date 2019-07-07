package com.rabbit.exchange.fanout;

import com.rabbit.exchange.MyMessage;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class FanoutMessagingApplication {

    public static final String FANOUT_EXCHANGE = "ex.fanout";
    public static final String QUEUE_NAME1 = "fanoutQueue1";
    public static final String QUEUE_NAME2 = "fanoutQueue2";


    public static void main(String[] args) {
        SpringApplication.run(FanoutMessagingApplication.class, args);
    }


    @Bean
    public ApplicationRunner runner(AmqpTemplate template, FanoutExchange fanout) {
        return args ->
            template.convertAndSend(fanout.getName(), "",
                new MyMessage(LocalDateTime.now(), "hello rabbit"));
    }


    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }


    //-------------------------------------------------------------------
    //optionally wrapped in retryTemplate
/*  @Bean
    public AmqpTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(producerJackson2MessageConverter());

        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);
        return template;
    }*/
    //-------------------------------------------------------------------
    @Bean
    public Queue myQueue1() {
        return new Queue(QUEUE_NAME1);
    }

    @Bean
    public Queue myQueue2() {
        return new Queue(QUEUE_NAME2);
    }


    @Bean
    public Binding binding1(FanoutExchange fanout,
                            Queue myQueue1) {
        return BindingBuilder.bind(myQueue1).to(fanout);
    }

    @Bean
    public Binding binding2(FanoutExchange fanout,
                            Queue myQueue2) {
        return BindingBuilder.bind(myQueue2).to(fanout);
    }
    //-------------------------------------------------------------------

}
