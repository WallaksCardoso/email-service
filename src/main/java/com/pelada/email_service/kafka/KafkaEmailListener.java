package com.pelada.email_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pelada.email_service.event.JogadorCadastradoEvent;
import com.pelada.email_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEmailListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(topics = "jogador-evento", groupId = "email-service")
    public void consumirEvento(ConsumerRecord<String, String> record) {
        try {
            JogadorCadastradoEvent event = objectMapper.readValue(record.value(), JogadorCadastradoEvent.class);

            String assunto = "Novo Jogador Cadastrado: " + event.getNomeJogador();
            String corpo = String.format("""
                            Olá,
                            
                            Um novo jogador foi cadastrado:
                            
                            Nome: %s
                            Goleiro: %s
                            Data de Cadastro: %s
                            Sorteio: %s
                            
                            Att,
                            Sistema Pelada
                            """,
                    event.getNomeJogador(),
                    event.isGoleiro() ? "Sim" : "Não",
                    event.getDataCadastro(),
                    event.getNomeSorteio()
            );

            emailService.enviar(event.getEmailNotificacao(), assunto, corpo);
            log.info("Email enviado com sucesso para jogador {}", event.getNomeJogador());

        } catch (Exception e) {
            log.error("Erro ao processar evento: {}", e.getMessage(), e);
        }
    }
}
