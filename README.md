Code example of using **RabbitMQ** - messaging broker and transactions 


Starting rabbitmq server
```bash
docker run -p 5672:5672 -p 15672:15672 -d --hostname my-rabbit --name some-rabbit rabbitmq:3-management
```