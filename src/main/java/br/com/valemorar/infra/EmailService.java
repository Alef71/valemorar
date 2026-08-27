package br.com.valemorar.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacaoSenha(String emailDestino, String token) {
        if (mailSender == null) {
            System.out.println("[DEV] Serviço de e-mail não configurado. Token para " + emailDestino + ": " + token);
            return;
        }

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(emailDestino);
            mensagem.setSubject("ValeMorar - Recuperação de Senha");
            mensagem.setText("Seu token de recuperação de senha é: " + token + "\n\nEste token é válido por 1 hora.");

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de recuperação: " + e.getMessage());
            System.out.println("[DEV FALLBACK] Token para " + emailDestino + ": " + token);
        }
    }
}