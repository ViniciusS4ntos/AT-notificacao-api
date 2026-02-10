package com.vinicius.notificacao_api.business;

import com.vinicius.notificacao_api.business.dto.TarefasDTO;
import com.vinicius.notificacao_api.infrastructure.excecptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${envio.email.remetente}")
    public String remetente; // observacao

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void enviarEmail(TarefasDTO dto) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            // From -> To
            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));
            mimeMessageHelper.setTo(InternetAddress.parse(dto.getEmailUsuario()));

            // formata data
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

            String dataFormatada = dto.getDataEvento().format(formatter);



            mimeMessageHelper.setSubject("Notificação de Tarefa");

            Context context = new Context();
            context.setVariable("nomeTarefa", dto.getNomeTarefa());
            context.setVariable("dataEvento", dataFormatada);
            context.setVariable("descricao", dto.getDescricao());

            String template = templateEngine.process("notificacao", context);

            mimeMessageHelper.setText(template, true);

            javaMailSender.send(message);

        } catch (EmailException | MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar o email : ", e.getCause());
        }
    }
}
