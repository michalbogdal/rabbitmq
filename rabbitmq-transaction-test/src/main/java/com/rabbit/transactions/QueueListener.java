package com.rabbit.transactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import static com.rabbit.transactions.RabbitMessagingApplication.FANOUT_EXCHANGE;

@Component
public class QueueListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    protected PlatformTransactionManager transactionManager;

    @RabbitListener(bindings = @QueueBinding(value = @Queue,
            exchange = @Exchange(name = FANOUT_EXCHANGE, type = ExchangeTypes.FANOUT)))
    public void processMessageFromExchange(Message message) {
        LOGGER.info("[events][{}] {}", transactionManager.getClass().getSimpleName(), new String(message.getBody()));
    }
}
