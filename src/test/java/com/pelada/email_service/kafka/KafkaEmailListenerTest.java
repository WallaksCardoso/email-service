package com.pelada.email_service.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pelada.email_service.service.EmailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;

class KafkaEmailListenerTest {

    private final EmailService emailService = mock(EmailService.class);
    private final KafkaEmailListener listener =
            new KafkaEmailListener(new ObjectMapper().registerModule(new JavaTimeModule()), emailService);

    private ConsumerRecord<String, String> evento(String json) {
        return new ConsumerRecord<>("jogador-evento", 0, 0L, null, json);
    }

    @Test
    void enviaEmailComOsDadosDoJogador() {
        listener.consumirEvento(evento("""
                {"nomeJogador": "Carlos", "goleiro": true, "dataCadastro": "2026-05-10",
                 "nomeSorteio": "Pelada de quinta", "emailNotificacao": "notificacoes@exemplo.com"}
                """));

        ArgumentCaptor<String> corpo = ArgumentCaptor.forClass(String.class);
        verify(emailService).enviar(
                eq("notificacoes@exemplo.com"),
                eq("Novo Jogador Cadastrado: Carlos"),
                corpo.capture());
        assertThat(corpo.getValue())
                .contains("Nome: Carlos")
                .contains("Goleiro: Sim")
                .contains("Data de Cadastro: 2026-05-10")
                .contains("Sorteio: Pelada de quinta");
    }

    @Test
    void falhaNoEnvioSobeParaOKafkaTentarDeNovo() {
        doThrow(new MailSendException("SMTP fora do ar")).when(emailService).enviar(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> listener.consumirEvento(evento("""
                {"nomeJogador": "Ana", "goleiro": false, "dataCadastro": "2026-05-10",
                 "nomeSorteio": "Pelada", "emailNotificacao": "notificacoes@exemplo.com"}
                """)))
                .isInstanceOf(MailSendException.class);
    }

    @Test
    void eventoInvalidoNaoEnviaEmail() {
        listener.consumirEvento(evento("isto não é json"));

        verify(emailService, never()).enviar(anyString(), anyString(), anyString());
    }
}
