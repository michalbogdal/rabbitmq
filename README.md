Code example of using **RabbitMQ** - messaging broker

Different exchanges (direct, fanout, topic) with one or more queue consumers

Starting rabbit mq server
```bash
docker run -p 5672:5672 -p 15672:15672 -d --hostname my-rabbit --name some-rabbit rabbitmq:3-management
```

ports:
- 5672 -  amqp
- 15672 - UI (http)

```bash
docker inspect some-rabbit
```

Rabbit UI management
```
http://localhost:15672/
```

# Http Api

fanout
```bash
curl -XPOST -H "content-type:application/json"  \
-d '{"properties":{"content_type": "application/json"},"routing_key":"","payload":"{\"localDateTime\":[2019,7,7,11,26,50,753000000],\"message\":\"hello rabbit\"}","payload_encoding":"string"}' \
-i -u guest:guest http://localhost:15672/api/exchanges/%2F/ex.fanout/publish
```


topic
```bash
curl -XPOST -H "content-type:application/json"  \
-d '{"properties":{"content_type": "application/json"},"routing_key":"routing.topic.queue1","payload":"{\"localDateTime\":[2019,7,7,11,26,50,753000000],\"message\":\"hello message to queue1\"}","payload_encoding":"string"}' \
-i -u guest:guest http://localhost:15672/api/exchanges/%2F/ex.topic/publish
```