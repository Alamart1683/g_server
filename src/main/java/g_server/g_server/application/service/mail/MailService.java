package g_server.g_server.application.service.mail;

import g_server.g_server.application.entity.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.ZonedDateTime;

@Service
// TODO Глобально подумать о создании генерируемых на основе токенов ссылок подтверждения каких либо действий
// TODO это позволит сильно увеличить удобство пользования сайтом, например прямо из письма подтвердить заявку
// TODO студента, просто перейдя по ссылке
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    // Отправить по почте код подтверждения и ссылки завершения регистрации для студента
    public String sendStudentEmail(String recipient, String registrationCode, String registrationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            message.setTo(recipient);
            message.setSubject("Код регистрации для сайта выпускников кафедры МОСИТ");
            String htmlMessage = "Здравствуйте, для завершения регистрации вам необходимо указать код подтверждения."
                    + "<br>Код подтверждения регистрации:<b> " + registrationCode + "</b><p>" +
                    "<br>Так же вы можете завершить регистрацию, перейдя по <b><a href=\"" +
                    registrationLink + "\" target=\"_blank \"> ссылке </a></b>"
                    + "<br>Ссылка действительна 24 часа с момента регистрации.<p>" +
                    "<br><br><br>Это письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.";
            message.setText(htmlMessage, true);
            this.mailSender.send(mimeMessage);
        }
        catch (MessagingException messagingException) {
            return "MessagingException";
        }
        return "Email sent!";
    }

    // Послать уведомление об окончании регистрации студенту
    public String sendSuccessRegistrationMailForStudent(Users student) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Завершение регистрации");
        String time = mailTimeDetector();
        String studentName = student.getName() + ' ' + student.getSecond_name();
        message.setText(time + ", " + studentName + ".\n");
        message.setText(message.getText() + "Ваш аккаунт был успешно активирован. С этого момента вы можете " +
                "пользоваться всеми сервисами сайта.\n");
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.");
        this.mailSender.send(message);
        return "Email sent!";
    }

    // Отравить по почте уведолмение о регистрации
    public String sendLoginEmailAndPassword(String recipient, String password, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Учетная запись сайта выпускников кафедры МОСИТ");
        message.setText("Здравствуйте, вы были зарегистрированы в статусе " + status + " на сайте выпускников кафедры МОСИТ." +
                "\nЛогин учетной записи: " + recipient + "\nПароль учётной записи: " + password + "\n");
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.");
        this.mailSender.send(message);
        return "Email sent!";
    }

    // Валидировать email
    public boolean checkMail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    // Послать научному руководителю письмо о том, что студент хочет получить его научное руководство
    public void sendRequestForScientificAdvisorMail(Users student, Users scientificAdvisor, String theme) {
        String studentFIO = student.getSurname() + ' ' + student.getName() + ' ' + student.getSecond_name();
        String studentGroup = student.getStudentData().getStudentGroup().getStudentGroup();
        String studentPhone = student.getPhone();
        String studentEmail = student.getEmail();
        String studentType = student.getStudentData().getStudentType().getStudentType().toLowerCase();
        String studentCathedra = student.getStudentData().getCathedras().getCathedraName();
        String scientificAdvisorName = scientificAdvisor.getName() + ' ' + scientificAdvisor.getSecond_name();
        // Получим время суток
        String time = mailTimeDetector();
        // Получив необходимые данные, отправим само письмо
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(scientificAdvisor.getEmail());
        message.setSubject("Заявка студента на ваше научное руководство");
        message.setText(time + ", " + scientificAdvisorName + ".\n");
        message.setText(message.getText() + "Студент(ка)-" + studentType + ' ' + studentFIO + " из группы " + studentGroup +
                " с кафедры " + studentCathedra + " подал(а) заявку на то," +
                " чтобы вы стали его(её) научным руководителем.\n");
        message.setText(message.getText() + "Студент(ка) заинтересован(а) в том, чтобы взять следующую тему ВКР: " + theme + ".\n");
        message.setText(message.getText() + "Вы можете как принять, так и отклонить данную заявку, перейдя по этим ссылкам, " +
                "либо же совершить аналогичные действия в соответствующем разделе нашего сайта.\n");
        message.setText(message.getText() + "\nДля связи с данным студентом(кой) вы можете использовать\n");
        message.setText(message.getText() + "email-адрес: " + studentEmail + "\n");
        // TODO Возможно сделать автоматический перевод мобильного телефона в адекватный вид
        message.setText(message.getText() + "мобильный телефон: " + studentPhone + "\n");
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.");
        this.mailSender.send(message);
    }

    // Послать студенту письмо о том, что его заявка доставлена преподавателю
    public void sendMailStudentAboutHisRequestSending(Users student) {
        String time = mailTimeDetector();
        String studentName = student.getName() + ' ' + student.getSecond_name();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Ваша заявка была отправлена научному руководителю");
        message.setText(time + ", " + studentName + ".\n");
        message.setText(message.getText() + "Ваша заявка на научное руководство была успешно отправлена" +
                " на рассмотрение научному руководителю. \n");
        message.setText(message.getText() + "После рассмотрения заявки вы получите соответствующее уведомление. \n");
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.");
        this.mailSender.send(message);
    }

    // Определить время письма
    String mailTimeDetector() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        Integer currentHour = dateTime.getHour();
        if (currentHour >= 0 && currentHour <= 6) {
            return "Доброй ночи";
        }
        else if (currentHour >= 7 && currentHour <= 11) {
            return "Доброе утро";
        }
        else if (currentHour >= 12 && currentHour <= 17) {
            return "Добрый день";
        }
        else if (currentHour >= 18 && currentHour <= 23) {
            return "Добрый вечер";
        }
        else {
            return "Доброго времени суток";
        }
    }
}