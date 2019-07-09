package com.rabbit.exchange.topic;

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
public class TopicMessagingApplication {

    public static final String TOPIC_EXCHANGE = "ex.topic";
    public static final String QUEUE_NAME1 = "t-queue1";
    public static final String QUEUE_NAME2 = "t-queue2";
    public static final String QUEUE_NAME3 = "t-queue3";
    public static final String QUEUE1_ROUTING_KEY = "*.topic.queue1";
    public static final String QUEUE2_ROUTING_KEY = "*.topic.queue2";
    public static final String QUEUE3_ROUTING_KEY = "*.topic.*";


    public static void main(String[] args) {
        SpringApplication.run(TopicMessagingApplication.class, args);
    }


    @Bean
    public ApplicationRunner runner(AmqpTemplate template, TopicExchange topic) {
        return args -> {
            template.convertAndSend(topic.getName(), "routing.topic.queue1",
                    new MyMessage(LocalDateTime.now(), "hello rabbit in queue1"));

            template.convertAndSend(topic.getName(), "routing.topic.queue2",
                    new MyMessage(LocalDateTime.now(), "hello rabbit in queue2"));
        };
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
    public TopicExchange topic() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Queue myQueue1() {
        return new Queue(QUEUE_NAME1);
    }

    @Bean
    public Queue myQueue2() {
        return new Queue(QUEUE_NAME2);
    }

    @Bean
    public Queue myQueue3() {
        return new Queue(QUEUE_NAME3);
    }

    @Bean
    public Binding binding1(TopicExchange topic,
                            Queue myQueue1) {
        return BindingBuilder.bind(myQueue1).to(topic).with(QUEUE1_ROUTING_KEY);
    }

    @Bean
    public Binding binding2(TopicExchange topic,
                            Queue myQueue2) {
        return BindingBuilder.bind(myQueue2).to(topic).with(QUEUE2_ROUTING_KEY);
    }

    /**
     * it will start receiving all messages (which were targeted to either queue1 or queue2)
     *
     * @param topic
     * @param myQueue3
     * @return
     */
    @Bean
    public Binding binding3(TopicExchange topic,
                            Queue myQueue3) {
        return BindingBuilder.bind(myQueue3).to(topic).with(QUEUE3_ROUTING_KEY);
    }
}
