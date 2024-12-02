# mouts-order

### Descrição

Projeto realizado para participação no processo seletivo da empresa Mouts.

### Proposta

Desenvolver um serviço com Springboot para receber pedidos de um serviço externo A, realizar o cálculo do valor do pedido e disponibilizar o pedido para uma serviço externo B.

O fluxo proposto foi desenvolvido da seguinte forma.

1. Serviços externos disponibilizam os pedidos em um tópico KAFKA pending_orders - para pedidos pendentes.
2. O serviço de processamento de pedidos, consome as mensagens desta fila.
3. O serviço realiza o cálculo total do pedido e verifica possíveis pedidos duplicados.
4. O serviço salva o pedido cálculo em uma base de dados POSTRGRE
5. O serviço possui um OrderStateMachine que intercepta a validação do pedido e altera seu status para VALIDATED.
6. Um listener do OrderStateMachine reconhece o evento de validação e envia o pedido com status VALIDATED para um tópico KAFKA processed_orders - para pedidos processados.
7. Serviços externos podem consumir pedidos já processados nesta fila.
8. Serviços externos podem consultar pedidos já processados através do endpoint api/v1/orders/all

### Modelo conceitual

![alt text](https://github.com/dvalvarenga/mouts-order/blob/main/docs/model.png?raw=true)

### Instalação

1. Faça o checkout do projeto
2. Utilize o maven e jdk 21 para realizar o build do projeto.
3. Utilize o arquivo docker-compose disponível em mouts-order/docker para configurar Kafka no Docker
4. Crie os tópicos kafka com os comandos
   docker exec -it kafka kafka-topics.sh --create --topic pending-orders --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
   docker exec -it kafka kafka-topics.sh --create --topic processed-orders --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
   docker exec -it kafka kafka-topics.sh --create --topic retry-orders --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
6. Execute o script de criação do banco de dados disponivel em mouts-order/database
7. Rode o projeto
8. Poste na topic pending-orders utilizando o comando abaixo
   docker exec -it kafka kafka-console-producer.sh --broker-list localhost:9092 --topic pending-orders
   { "status": "PENDING", "products": [ { "product": { "id": 11, "price":150.00}, "unitPrice": 130.00, "quantity": 1 }, { "product": { "id": 4, "price": 190.00 },"unitPrice": 170.00, "quantity": 11 } ] }
   
   
