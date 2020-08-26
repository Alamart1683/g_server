package g_server.g_server.application.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MailService {
    @Autowired
    public JavaMailSender mailSender;

    public String sendStudentEmail(String recipient, String registrationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Код регистрации для сайта выпускников кафедры МОСИТ");
        message.setText("Здравствуйте, для завершения регистрации вам необходимо указать код подтверждения. \nКод подтверждения регистрации: " + registrationCode);
        this.mailSender.send(message);
        return "Email sent!";
    }

    public String sendLoginEmail(String recipient, String password, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Учетная запись сайта выпускников кафедры МОСИТ");
        message.setText("Здравствуйте, вы были зарегистрированы в статусе " + status + " на сайте выпускников кафдеры МОСИТ." +
                "\nЛогин учетной записи: " + recipient + "\nПароль учётной записи: " + password);
        this.mailSender.send(message);
        return "Email sent!";
    }

    public boolean checkMail(String email) {
        Pattern pattern = Pattern.compile("^([A-Za-z0-9_-]+\\.)*[a-zA-Z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches())
            return false;
        else
            return true;
    }
}