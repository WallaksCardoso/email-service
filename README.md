# email-service

[![CI](https://github.com/WallaksCardoso/email-service/actions/workflows/ci.yml/badge.svg)](https://github.com/WallaksCardoso/email-service/actions/workflows/ci.yml)

Consumidor Kafka que avisa por e-mail quando alguém entra na lista da pelada. O
[pelada-gestao](https://github.com/WallaksCardoso/pelada-gestao) publica um evento no tópico
`jogador-evento` a cada jogador cadastrado, e este serviço monta e envia a mensagem para o e-mail
de notificação da lista.

A ideia era tirar o envio de e-mail do caminho da requisição: o cadastro responde na hora, e se o
SMTP estiver fora do ar o jogador entra na lista do mesmo jeito.

## Falhas

- Erro no envio: nova tentativa 3 vezes, com 5 segundos entre elas (`EMAIL_RETENTATIVAS` e
  `EMAIL_INTERVALORETENTATIVAMS`).
- Se continuar falhando, o evento vai para `jogador-evento.DLT` em vez de se perder.
- JSON inválido não adianta tentar de novo: vai para o log e é descartado.

Evento de exemplo:

```json
{
  "nomeJogador": "Carlos",
  "goleiro": true,
  "dataCadastro": "2026-05-10",
  "nomeSorteio": "Pelada de quinta",
  "emailNotificacao": "notificacoes@exemplo.com"
}
```

## Rodando

Pelo Docker Compose do [pelada-gestao](https://github.com/WallaksCardoso/pelada-gestao#subindo-tudo),
os e-mails caem no Mailpit em <http://localhost:8025>, sem precisar de conta de e-mail.

Sozinho, com Java 21, um Kafka e um SMTP (o [Mailtrap](https://mailtrap.io) tem sandbox grátis):

```bash
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export MAIL_HOST=sandbox.smtp.mailtrap.io MAIL_PORT=2525
export MAIL_USERNAME=... MAIL_PASSWORD=...
./mvnw spring-boot:run
```

## Testes

```bash
./mvnw verify
```

Rodam sem Kafka nem SMTP: conferem o e-mail montado a partir do evento, o descarte do JSON inválido e
que a falha no SMTP volta para o Kafka tentar de novo.

## Licença

MIT
