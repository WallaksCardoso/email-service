# email-service

Serviço responsável por consumir eventos do Kafka e enviar e-mails de notificação.

## Tecnologias Utilizadas

- Java 21  
- Spring Boot 3.5.0  
- Spring for Apache Kafka  
- Spring Boot Starter Mail  
- Maven  

## Configuração do Ambiente

### Pré-requisitos

- Java 21 instalado  
- Maven instalado  
- Docker (opcional, para rodar Kafka e Mailtrap)  

### Configuração de Propriedades

No arquivo `src/main/resources/application.properties`, configure as propriedades do Kafka e do servidor SMTP:

```properties
# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=email-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer

# SMTP (exemplo com Mailtrap)
spring.mail.host=smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=SEU_USUARIO_MAILTRAP
spring.mail.password=SUA_SENHA_MAILTRAP
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> Substitua `SEU_USUARIO_MAILTRAP` e `SUA_SENHA_MAILTRAP` com suas credenciais reais do [Mailtrap.io](https://mailtrap.io).

---

## Funcionalidades

- Consome eventos de cadastro de jogadores via Kafka  
- Envia e-mails de notificação

## Observações

- Certifique-se de que o Kafka e o Mailtrap estejam em execução e configurados corretamente.  
- As configurações de destinatário e layout do e-mail podem ser ajustadas conforme a necessidade.

---

## Build e Execução

### Para compilar o projeto:

```bash
mvn clean install
```

### Para rodar a aplicação localmente:

```bash
mvn spring-boot:run
```

### Ou via Docker:

```bash
docker build -t email-service .
docker run -p 8081:8081 email-service
```
