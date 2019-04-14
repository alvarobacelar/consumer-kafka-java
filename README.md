consumer-kafka-java
===========

[![Build Status](https://travis-ci.org/alvarobacelar/consumer-kafka-java.svg?branch=master)](https://travis-ci.org/alvarobacelar/consumer-kafka-java)

Aplicação java consumidor de mensagem para broker kafka. 

Para utilizar essa aplicação é preciso baixar o arquivo consumer.yml e alterar as env de acordo com o seu ambiente. 
Para executar a aplicação basta rodar o seguinte comando: 
```
$ docker-compose -f consumer.yml up
```

Ao executar a saída de log desse container irá apresentar as mensagens que a aplicação consumiu do broker kafka. 

Informações do Autor
-------
Álvaro Bacelar - Especialista em TI
