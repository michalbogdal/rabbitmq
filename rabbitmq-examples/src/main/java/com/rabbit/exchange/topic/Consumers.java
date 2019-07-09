package com.rabbit.exchange.topic;

import com.rabbit.exchange.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.rabbit.exchange.topic.TopicMessagingApplication.*;

@Component
public class Consumers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumers.class);

    @RabbitListener(queues = QUEUE_NAME1)
    public void processMessageFromFirstQueue(MyMessage message) {
        LOGGER.info("[{}], {}", QUEUE_NAME1, message);
    }

    @RabbitListener(queues = QUEUE_NAME2)
    public void processMessageFromSecondQueue(MyMessage message) {
        LOGGER.info("[{}], {}", QUEUE_NAME2, message);
    }

    @RabbitListener(queues = QUEUE_NAME3)
    public void processMessageFromThirdQueue(MyMessage message) {
        LOGGER.info("[{}], {}", QUEUE_NAME3, message);
    }

}
