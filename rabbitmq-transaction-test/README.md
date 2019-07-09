Example of transaction in **RabbitMQ** - messaging broker

More details:
https://www.rabbitmq.com/semantics.html

RabbitMq **doesn't support 2PC**, which means it cannot participate in **XA** transactions.
What it does it synchronizes their own transaction (send, ack) with current transactions managed by e.g. JpaTransactionManager.

This looks like _"Best effort 1PC"_ which is kind of trade off, not having 2pc but still being able to deliver messages in most cases - but it is not fail proof, which I will show here.
All potential **business failures** are in **first transactions** (database constraints etc) this is because there is big chance it might fail,
in the **last transaction** we have **sending messages to rabbitmq** - this is because it might fail only in problems with integration (connections, network etc), which seems to be quire rare case comparing to business cases.

When database transaction is committed successfully then message broker transaction is committed as well.
It means in same rare case we might have **"at most once"** message delivery semantic
Small test case show this scenario.

Steps:
1) execute logic which persist event to database and send message to broker
2) transaction is created (and managed by JpaTransactionManager)
3) event object is saved
4) messages is send to broker
5) message broker register transaction synchronization with current transaction
6) database transaction **is committed**
7) transaction manager tries to commit "other" transactions (to send ACK to rabbitMQ)
8) RabbitMQ fails in delivering ack message to broker - exception is thrown
9) database transaction is still already committed with all events 


Starting rabbit mq server
```bash
docker run -p 5672:5672 -p 15672:15672 -d --hostname my-rabbit --name some-rabbit rabbitmq:3-management
```


