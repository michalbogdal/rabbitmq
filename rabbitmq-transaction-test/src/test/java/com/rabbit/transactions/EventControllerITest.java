package com.rabbit.transactions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

/**
 * Test scenario shows the potential problem with rabbitmq transactions (which doesn't support 2PC)
 * see: https://www.rabbitmq.com/semantics.html
 * <p>
 * Test requires running RabbitMQ message broker
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EventControllerITest {


    @Autowired
    private EventController eventController;

    @Test
    @Rollback(false)
    @Transactional
    public void shouldEventBeSavedEvenIfMessageBrokerFails() {
        //trigger two operations, saving data to database, send message to broker
        eventController.saveAndSend("somethingToSave");

        injectProblematicResourceHolderToTransaction();
    }

    /**
     * Message broker (see ConnectionFactoryUtils) adds transaction synchronization to existing transaction.
     * Then transaction manager (e.g. JpaTransactionManager) after commit his own changes try to commit additional synchronizations
     * org.springframework.transaction.support.AbstractPlatformTransactionManager#invokeAfterCompletion(java.util.List, int)
     * in our case sending ACK messages to broker.
     * <p>
     * Those two transactions are not atomic, triggered in a sequence
     * which means if message broker fails, database transaction still will be committed.
     * <p>
     * in this configuration looks we have "at most once" delivery message semantic
     * <p>
     * see: Best effort 1PC
     * see: https://www.rabbitmq.com/semantics.html
     */
    private void injectProblematicResourceHolderToTransaction() {
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        TransactionSynchronization synch = synchronizations.iterator().next();

        RabbitResourceHolder rh = (RabbitResourceHolder) ReflectionTestUtils.getField(synch, "resourceHolder");
        RabbitResourceHolder spyRh = spy(rh);
        doThrow(new AmqpException("internal stuff, sorry")).when(spyRh).commitAll();
        ReflectionTestUtils.setField(synch, "resourceHolder", spyRh);
    }

    @AfterTransaction
    public void shouldReturnEventsIfDataIsCommitted() {
        assertThat(eventController.getEvents()).isNotEmpty();
    }
}