package com.rabbit.transactions;

import com.rabbit.transactions.repo.Event;
import com.rabbit.transactions.repo.EventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
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

import static com.rabbit.transactions.RabbitMessagingApplication.FANOUT_EXCHANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest {

    @Mock
    private AmqpTemplate template;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventController eventController;


    @Test
    public void shouldSaveEventAndThenSendMessage() {
        eventController.saveAndSend("extra stuff");

        verify(eventRepository).save(any());
        verify(template).convertAndSend(anyString(), anyString(), anyString());
        inOrder(eventRepository, template);
    }

    @Test
    public void shouldSaveEventWithDescription() {
        String description = "extra stuff";
        eventController.saveAndSend(description);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getDescription()).isEqualTo(description);
    }

    @Test
    public void shouldSendMessageWithDescription() {
        String description = "extra stuff";
        eventController.saveAndSend(description);

        String expectedMessage = String.format("saved event with desc: %s", description);
        verify(template).convertAndSend(FANOUT_EXCHANGE, "", expectedMessage);
    }

}