package appetito.apicardapio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String enviarEmailTexto(String destinatario, String assunto, String mensagem) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(destinatario);
            message.setSubject(assunto);
            message.setText(mensagem);
            javaMailSender.send(message);
            return "Email enviado com sucesso!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ocorreu um erro ao enviar email!";
        }
    }
}
