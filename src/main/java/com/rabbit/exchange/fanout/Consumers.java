package com.rabbit.exchange.fanout;

import com.rabbit.exchange.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.rabbit.exchange.fanout.FanoutMessagingApplication.*;

@Component
public class Consumers {

    private static final Logger LOGGER  = LoggerFactory.getLogger(Consumers.class);

    /**
     * two consumers for same queue, given messages will be delivered to one of them
     */
    @Component
    static class Queue1Consumers {

        @RabbitListener(queues = QUEUE_NAME1)
        public void processMessageFromFirstQueue(MyMessage message) {
            LOGGER.info("[{}] consumer 1, {}", QUEUE_NAME1, message);
        }

        @RabbitListener(queues = QUEUE_NAME1)
        public void processMessageFromFirstQueue2(MyMessage message) {
            LOGGER.info("[{}] consumer 2, {}", QUEUE_NAME1, message);
        }

    }

    @Component
    static class Queue2Consumer {

        @RabbitListener(queues = QUEUE_NAME2)
        public void processMessageFromSecondQueue(MyMessage message) {
            LOGGER.info("[{}], {}", QUEUE_NAME2, message);
        }

    }


    /**
     * for those consumers, individual queues will be created and bind with exchange
     */
    @Component
    static class FanoutMessagingConsumers {


        @RabbitListener(bindings = @QueueBinding(value = @Queue,
                exchange = @Exchange(name = FANOUT_EXCHANGE, type = ExchangeTypes.FANOUT)))
        public void processMessageFromExchange(MyMessage message) {
            LOGGER.info("[fanout] {}", message);
        }

        @RabbitListener(bindings = @QueueBinding(value = @Queue,
                exchange = @Exchange(name = FANOUT_EXCHANGE, type = ExchangeTypes.FANOUT)))
        public void processMessageFromExchange(Message message) {
            LOGGER.info("[fanout-raw] {}", new String(message.getBody()));
        }
    }

}
