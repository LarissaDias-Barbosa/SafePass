package com.SafePass.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String remetente;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmail(String to, String nomeUsuario, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);

            String mensagemCustomizada = "Olá " + nomeUsuario + ",\n\n"
                    + body + "\n\n"
                    + "Atenciosamente,\n"
                    + "Equipe SafePass";

            message.setText(mensagemCustomizada);
            message.setFrom(remetente);
            mailSender.send(message);

            System.out.println("Email enviado para: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar email: " + e.getMessage());
        }
    }

    public void enviarConfirmacao(String emailDestino, String linkConfirmacao) {
        String assunto = "Confirme seu e-mail - SafePass";
        String mensagem = """
                Olá!

                Obrigado por se cadastrar no SafePass.
                Clique no link abaixo para confirmar seu e-mail e ativar sua conta:

                """ + linkConfirmacao + """

                Se você não criou uma conta, ignore este e-mail.

                Atenciosamente,
                Equipe SafePass
                """;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(remetente);
        email.setTo(emailDestino);
        email.setSubject(assunto);
        email.setText(mensagem);

        mailSender.send(email);
    }

    public void enviarCodigoConfirmacao(String emailDestino, String nomeUsuario, String codigo) {
        String assunto = "Código de Confirmação - SafePass";
        String mensagem = """
            Olá %s,

            Seu código de confirmação é: %s

            Ele expira em 10 minutos.
            Se você não solicitou, ignore este e-mail.

            Atenciosamente,
            Equipe SafePass
            """.formatted(nomeUsuario != null ? nomeUsuario : "", codigo);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(remetente);
        email.setTo(emailDestino);
        email.setSubject(assunto);
        email.setText(mensagem);

        mailSender.send(email);
    }

    public String gerarCodigo() {
        int codigo = 100000 + (int) (Math.random() * 900000);
        return String.valueOf(codigo);
    }
}