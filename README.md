# Email Service

Microsserviço que consome eventos do Kafka e envia e-mails de notificação quando um jogador é
cadastrado no sistema da pelada.

[![CI](https://github.com/WallaksCardoso/email-service/actions/workflows/ci.yml/badge.svg)](https://github.com/WallaksCardoso/email-service/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-informational)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-informational)
[![Licença](https://img.shields.io/badge/licen%C3%A7a-MIT-informational)](LICENSE)

## Sobre

Quando um jogador entra em um sorteio no [pelada-gestao](https://github.com/WallaksCardoso/pelada-gestao),
a API publica um evento no tópico `jogador-evento` (se `pelada.kafka.enabled=true`). Este serviço
escuta o tópico, monta a mensagem e envia o e-mail para o endereço de notificação configurado no
sorteio. A API não espera o envio: a notificação é assíncrona e uma falha no SMTP não afeta o cadastro.

## Funcionalidades

- Consumo do tópico `jogador-evento` (grupo `email-service`)
- Montagem do e-mail com nome do jogador, se é goleiro, data de cadastro e sorteio
- Envio por SMTP (testado com o sandbox do Mailtrap)
- Falha no envio é tentada de novo (3 vezes, com 5 segundos entre elas); se continuar falhando, o
  evento vai para o tópico `jogador-evento.DLT` em vez de se perder
- Eventos com JSON inválido são registrados no log e descartados, sem novas tentativas

Exemplo de evento:

```json
{
  "nomeJogador": "Carlos",
  "goleiro": true,
  "dataCadastro": "2026-05-10",
  "nomeSorteio": "Pelada de quinta",
  "emailNotificacao": "notificacoes@exemplo.com"
}
```

## Stack

- Java 21 e Spring Boot 3.5
- Spring for Apache Kafka
- Spring Boot Starter Mail
- Maven, Docker e GitHub Actions

## Como rodar

Pré-requisitos: Java 21, um Kafka acessível e uma conta SMTP (o
[Mailtrap](https://mailtrap.io) tem um sandbox gratuito).

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Endereço do Kafka |
| `MAIL_HOST` | `sandbox.smtp.mailtrap.io` | Servidor SMTP |
| `MAIL_PORT` | `2525` | Porta SMTP |
| `MAIL_USERNAME` | | Usuário SMTP |
| `MAIL_PASSWORD` | | Senha SMTP |
| `EMAIL_RETENTATIVAS` | `3` | Novas tentativas antes de mandar para o tópico de erro |
| `EMAIL_INTERVALORETENTATIVAMS` | `5000` | Intervalo entre as tentativas, em milissegundos |

```bash
./mvnw spring-boot:run
```

Com Docker:

```bash
docker build -t email-service .
docker run -p 8081:8081 \
  -e KAFKA_BOOTSTRAP_SERVERS=... -e MAIL_USERNAME=... -e MAIL_PASSWORD=... \
  email-service
```

O serviço sobe na porta 8081.

## Testes

```bash
./mvnw verify
```

Os testes do consumidor rodam sem Kafka nem SMTP: conferem o e-mail montado a partir do evento, que um
evento inválido não dispara envio e que a falha no SMTP é devolvida ao Kafka para nova tentativa. A CI roda os testes e monta a imagem Docker a cada push.

## Estrutura

```
src/main/java/com/pelada/email_service/
  config/    novas tentativas e tópico de erro
  event/     formato do evento recebido
  kafka/     consumidor do tópico
  service/   envio do e-mail
```

## Projetos relacionados

- [pelada-gestao](https://github.com/WallaksCardoso/pelada-gestao): API que publica os eventos

## Licença

[MIT](LICENSE)
