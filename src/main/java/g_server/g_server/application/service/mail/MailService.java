package g_server.g_server.application.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    public JavaMailSender mailSender;

    public String sendStudentEmail(String recipient, String registrationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Код регистрации для graduates_server");
        message.setText("Код подтверждения регистрации:\n" + registrationCode);
        this.mailSender.send(message);
        return "Email sent!";
    }

    public String sendLoginEmail(String recipient, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Учетная запись graduates_server");
        message.setText("Логин учетной записи: " + recipient + "\nПароль учётной записи: " + password);
        this.mailSender.send(message);
        return "Email sent!";
    }

}
