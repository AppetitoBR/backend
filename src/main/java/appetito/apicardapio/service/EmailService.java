package appetito.apicardapio.service;

import appetito.apicardapio.config.EmailProviderConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.Properties;
import java.util.Random;
/*
Irei apagar depois
 */
@Service
public class EmailService {
    /*


    @Value("${api.mail.username}")
    private String remetente;

    @Value("${api.mail.password}")
    private String senha;

    public String enviarCodigoVerificacao(String destinatario) {
        try {
            String codigo = gerarCodigoVerificacao();

            String host = EmailProviderConfig.getSmtpHost(remetente);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remetente, senha);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remetente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Seu código de verificação");

            String html = gerarCorpoHtml(codigo);
            message.setContent(html, "text/html; charset=utf-8");

            Transport.send(message);
            return "Código de verificação enviado com sucesso para " + destinatario + "!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao enviar código de verificação: " + e.getMessage();
        }
    }

    private String gerarCodigoVerificacao() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    private String gerarCorpoHtml(String codigo) {
        return """
                <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                    <h2 style="color: #4CAF50;">Verificação de E-mail</h2>
                    <p>Olá! Aqui está o seu código de verificação:</p>
                    <h1 style="color: #4CAF50; font-size: 48px;">%s</h1>
                    <p>Insira esse código no aplicativo para confirmar seu e-mail.</p>
                    <br>
                    <p style="font-size: 12px; color: #777;">Se você não solicitou este código, ignore este e-mail.</p>
                </div>
                """.formatted(codigo);
    }

     */
}
