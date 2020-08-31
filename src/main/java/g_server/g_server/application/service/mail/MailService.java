package g_server.g_server.application.service.mail;

import g_server.g_server.application.entity.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.ZonedDateTime;

@Service
// TODO Глобально подумать о создании генерируемых на основе токенов ссылок подтверждения каких либо действий
// TODO это позволит сильно увеличить удобство пользования сайтом, например прямо из письма подтвердить заявку
// TODO студента, просто перейдя по ссылке
public class MailService {
    @Autowired
    public JavaMailSender mailSender;

    // Отправить по почте код подтверждения для студента
    public String sendStudentEmail(String recipient, String registrationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Код регистрации для сайта выпускников кафедры МОСИТ");
        message.setText("Здравствуйте, для завершения регистрации вам необходимо указать код подтверждения. " +
                "\nКод подтверждения регистрации: " + registrationCode + "\n");
        message.setText("\n\n\n Это письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.");
        this.mailSender.send(message);
        return "Email sent!";
    }

    // Отравить по почте уведолмение о регистрации
    public String sendLoginEmailAndPassword(String recipient, String password, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Учетная запись сайта выпускников кафедры МОСИТ");
        message.setText("Здравствуйте, вы были зарегистрированы в статусе " + status + " на сайте выпускников кафдеры МОСИТ." +
                "\nЛогин учетной записи: " + recipient + "\nПароль учётной записи: " + password + "\n");
        message.setText("\n\n\n Это письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.");
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
    public void sendRequestForScientificAdvisorMail(Users student, Users scientificAdvisor) {
        String studentFIO = student.getSurname() + ' ' + student.getName() + ' ' + student.getSecond_name();
        String studentGroup = student.getStudentData().getStudentGroup().getStudentGroup();
        String studentPhone = student.getPhone();
        String studentEmail = student.getEmail();
        String studentType = student.getStudentData().getStudentType().getStudentType().toLowerCase();
        // TODO Если сделаем темы, сделать здесь также уведомление о выбранной студентом теме
        String theme = "мы придумаем их позже";
        String scientificAdvisorName = scientificAdvisor.getName() + ' ' + scientificAdvisor.getSurname();
        // Получим время суток
        String time = mailTimeDetector();
        // Получив необходимые данные, отправим само письмо
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(scientificAdvisor.getEmail());
        message.setSubject("Заявка студента на ваше научное руководство");
        message.setText(time + ", " + scientificAdvisorName + ".\n");
        message.setText("Студент(ка)-" + studentType + ' ' + studentFIO + " из группы " + studentGroup + " подал(а) заявку на то," +
                " чтобы вы стали его(её) научным руководителем\n");
        message.setText("Его(её) интересует тема ВКР " + theme + ".");
        message.setText("Вы можете как <u>принять</u>, так и <u>отклонить</u> данную заявку, перейдя по этим ссылкам, " +
                "работу которых мы конечно же организуем позже\n");
        message.setText("\n Для связи с данным студентом(кой) вы можете использовать:\n");
        message.setText("email-адрес " + studentEmail + "\n");
        // TODO Возможно сделать автоматический перевод мобильного телефона в адекватный вид
        message.setText("мобильный телефон " + studentPhone + "\n");
        message.setText("\n\n\n Это письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.");
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