package com.rabbit.transactions;

import com.rabbit.transactions.repo.Event;
import com.rabbit.transactions.repo.EventRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

import static com.rabbit.transactions.RabbitMessagingApplication.FANOUT_EXCHANGE;

@RestController
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AmqpTemplate template;


    @PostMapping("/event/{description}")
    @Transactional
    public void saveAndSend(@PathVariable String description) {

        Event event = new Event();
        event.setDescription(description);
        eventRepository.save(event);

        String message = String.format("saved event with desc: %s", description);
        template.convertAndSend(FANOUT_EXCHANGE, "", message);
    }

    @GetMapping("/event")
    public List<Event> getEvents(){
        return eventRepository.findAll();
    }
}
