package g_server.g_server.application.service.mail;

import g_server.g_server.application.entity.messanger.components.Receiver;
import g_server.g_server.application.entity.messanger.components.Sender;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.service.documents.DocumentProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.util.List;

@Service
// TODO Глобально подумать о создании генерируемых на основе токенов ссылок подтверждения каких либо действий
// TODO это позволит сильно увеличить удобство пользования сайтом, например прямо из письма подтвердить заявку
// TODO студента, просто перейдя по ссылке
public class MailService {
    private JavaMailSender mailSender;
    private DocumentProcessorService documentProcessorService;

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void setDocumentProcessorService(DocumentProcessorService documentProcessorService) {
        this.documentProcessorService = documentProcessorService;
    }

    @Value("${client.url}")
    private String clientUrl;

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
                    "<br><br><br>Это письмо было сгенерировано автоматически, пожалуйста, " +
                    "не отвечайте на него.";
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
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, " +
                "не отвечайте на него.");
        this.mailSender.send(message);
        return "Email sent!";
    }

    // Отравить по почте уведолмение о регистрации
    public String sendLoginEmailAndPassword(String recipient, String password, String status) {
        String time = mailTimeDetector();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject("Учетная запись сайта выпускников кафедры МОСИТ");
        message.setText(time + ", вы были зарегистрированы в статусе " + status + " на сайте выпускников кафедры МОСИТ." +
                "\nЛогин учетной записи: " + recipient + "\nПароль учётной записи: " + password + "\n");
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, " +
                "не отвечайте на него.");
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

    @Deprecated
    // Послать научному руководителю письмо о том, что студент хочет получить его научное руководство
    public String sendRequestForScientificAdvisorMail(Users student, Users scientificAdvisor,
                                                    String theme, String acceptURL, String declineURL) {
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
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            message.setTo(scientificAdvisor.getEmail());
            message.setSubject("Заявка студента на ваше научное руководство");
            String htmlMessage = time + ", " + scientificAdvisorName + ".<br>" +
                "Студент(ка)-" + studentType + ' ' + studentFIO + " из группы " +
                 studentGroup + " с кафедры " + studentCathedra + " подал(а) " +
                "заявку на то," + " чтобы вы стали его(её) научным руководителем.<br>" +
                "Студент(ка) заинтересован(а) в том, чтобы взять следующую тему ВКР: " + theme + ".<br>" +
                "Вы можете как <a href=\"" + acceptURL + "\" target=\"_blank \">принять</a>, " +
                "так и <a href=\"" + declineURL + "\" target=\"_blank \">отклонить</a>" + " данную заявку, перейдя по этим ссылкам, " +
                "либо же совершить аналогичные действия в соответствующем разделе сайта.<br>" +
                "Ссылки действительны 7 дней с момента отправления письма.<br>" +
                "<br>Для связи с данным студентом(кой) вы можете использовать<br>" +
                "email-адрес: " + studentEmail + "<br>" +
                // TODO Возможно сделать автоматический перевод мобильного телефона в адекватный вид
                "мобильный телефон: " + studentPhone + "<br>" +
                "<br><br><br>Это письмо было сгенерировано автоматически, пожалуйста, не отвечайте на него.";
            message.setText(htmlMessage, true);
            this.mailSender.send(mimeMessage);
            return "Email sent!";
        }
        catch (MessagingException messagingException) {
            return "MessagingException";
        }
    }

    // Послать студенту письмо о том, что его заявка доставлена преподавателю
    public String sendMailStudentAboutHisRequestSending(Users student) {
        String time = mailTimeDetector();
        String studentName = student.getName() + ' ' + student.getSecond_name();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Ваша заявка была отправлена научному руководителю");
        message.setText(time + ", " + studentName + ".\n");
        message.setText(message.getText() + "Ваша заявка на научное руководство была успешно отправлена" +
                " на рассмотрение научному руководителю. \n");
        message.setText(message.getText() + "После рассмотрения заявки вы получите соответствующее уведомление. \n");
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, " +
                "не отвечайте на него.");
        this.mailSender.send(message);
        return "Email sent!";
    }

    // Послать студенту письмо о том, что его заявка обработана преподавателем
    public String sendMailStudentAboutHandledRequest(Users student, Users scientificAdvisor, String handleResult) {
        String time = mailTimeDetector();
        String studentName = student.getName() + ' ' + student.getSecond_name();
        String advisorName = scientificAdvisor.getSurname() + ' ' +
                scientificAdvisor.getName() + ' ' + scientificAdvisor.getSecond_name();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Заявка " + handleResult);
        message.setText(time + ", " + studentName + ".\n");
        if (handleResult.equals("принята")) {
            message.setText(message.getText() + advisorName + " принял(а) вашу заявку на научное руководство.");
        }
        else if (handleResult.equals("отклонена")) {
            message.setText(message.getText() + advisorName + " отклонил(а) вашу заявку на научное руководство.");
        }
        message.setText(message.getText() + "\n\n\nЭто письмо было сгенерировано автоматически, пожалуйста, " +
                "не отвечайте на него.");
        this.mailSender.send(message);
        return "Email sent!";
    }

    // Послать код для смены пароля
    public String sendMailWithPasswordChangeCode(Users user, Integer code) {
        String time = mailTimeDetector();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            message.setTo(user.getEmail());
            message.setSubject("Код смены пароля учетной записи на сайте выпускников кафедры МОСИТ");
            String htmlMessage = time + ", " + user.getName() + " " + user.getSecond_name() +
                    ". Для смены пароля на сайте вам необходимо указать код подтвержения."
                    + "<br>Код подтверждения смены пароля:<p style=\"font-size: 34px\";><b> " + code + "</b></p>" +
                    "<br><br><br>Это письмо было сгенерировано автоматически, пожалуйста, " +
                    "не отвечайте на него.";
            message.setText(htmlMessage, true);
            this.mailSender.send(mimeMessage);
        }
        catch (MessagingException messagingException) {
            return "MessagingException";
        }
        return "Email sent!";
    }

    // Уведомить о смене пароля
    public String sendMailAboutPasswordChanging(Users user) {
        String time = mailTimeDetector();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            message.setTo(user.getEmail());
            message.setSubject("Смена пароля учетной записи на сайте выпускников кафедры МОСИТ");
            String htmlMessage = time + ", " + user.getName() + " " + user.getSecond_name() +
                    ". Пароль вашей учетной записи был успешно изменен." +
                    "<br><br><br>Это письмо было сгенерировано автоматически, пожалуйста, " +
                    "не отвечайте на него.";
            message.setText(htmlMessage, true);
            this.mailSender.send(mimeMessage);
        }
        catch (MessagingException messagingException) {
            return "MessagingException";
        }
        return "Email sent!";
    }

    // Дублировать отправку сообщения на почту
    public String sendMailByMessage(Sender sender, String messageTheme, String messageText, List<Receiver> receiverList) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            for (Receiver receiver: receiverList) {
                messageHelper.setTo(receiver.getEmail());
                messageHelper.setSubject(messageTheme);
                String[] splitFio = sender.getFio().split(" ");
                String htmlMessage = "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"utf-8\">\n" +
                        "</head>\n" +
                        "<body style=\"background-color:white; font-family:'PT Sans', sans-serif; font-size:20pt; color:#3A5985; -moz-hyphens: auto; -webkit-hyphens: auto; -ms-hyphens: auto\">\n" +
                        "<div style=\"background-color:#F1F4FB; width:50%; min-width:400px; height:100%; border:solid 15px #B2CAEE; position:absolute; border-radius: 7px; left:25%; margin:0; overflow-y:auto\">\n" +
                        "<p style=\"padding:0px 25px; font-size:24pt\">Вам пришло сообщение от " + documentProcessorService.getRpFio(splitFio[0], splitFio[1], splitFio[2])+ " (" + sender.getEmail() + "):</p>\n" +
                        "<form style=\"text-align:justify; -moz-text-align-last: justify; text-align-last: justify\">\n" +
                        "<p style=\"padding:0px 25px\"; text-align:justify; -moz-text-align-last: justify; text-align-last: justify>" + messageText + "</p>\n" +
                        "</form>\n" +
                        "<form action=\"action\">\n" +
                        "<center><a href=" + clientUrl + "messages" + " style=\"background-color:#B2CAEE; text-decoration:none; border:solid 0px #B2CAEE; font-family:'PT Sans', sans-serif; font-size:24pt; color:#3A5985; padding:2% 5%; position:relative; left:50%; transform:translate(-50%, 0); border-radius:7px; top:10px\"> Перейти к сообщению </a></center>\n" +
                        "</form>\n" +
                        "<p style=\"text-align:center; bottom:0px; position:relative; bottom:-20px; left: 50%; transform: translate(-50%, 0); font-size:18pt\">© Кафедра МОСИТ - выпускникам, 2020-2021</p>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";
                String gmailHtmlMessage = "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"utf-8\">\n" +
                        "</head>\n" +
                        "<body style=\"background-color:white; font-family:'PT Sans', sans-serif; font-size:18pt; color:#3A5985; -moz-hyphens: auto; -webkit-hyphens: auto; -ms-hyphens: auto\">\n" +
                        "<div style=\"background-color:#F1F4FB; width:50%; min-width:400px; box-sizing:border-box; height:100%; border:solid 15px #B2CAEE; position:absolute; border-radius: 7px; left:25%; margin:0; overflow-y:auto\">\n" +
                        "<p style=\"padding:0px 25px; font-size:22pt\">Вам пришло сообщение от " + documentProcessorService.getRpFio(splitFio[0], splitFio[1], splitFio[2])+ " ( " + sender.getEmail() + "):</p>\n" +
                        "<form style=\"text-align:justify; -moz-text-align-last: justify; text-align-last: justify; bottom:-25px\">\n" +
                        "<p style=\"padding:0px 25px\">" + messageText + "</p>\n" +
                        "</form>\n" +
                        "<form action=\"" + clientUrl + "messages\" style=\"min-width:400px;\">\n" +
                        "<center><button style=\"background-color:#B2CAEE; border:solid 0px #B2CAEE; width:280px; word-break: break-all; font-family:'PT Sans', sans-serif; font-size:22pt; color:#3A5985; padding:2% 5%; position:relative; left:50%; transform:translate(-50%, 0); border-radius:7px; top:25px\"> Перейти к сообщению </button></center>" +
                        "</form>\n" +
                        "<p style=\"text-align:center; bottom:0px; position:relative; bottom:-50px; left: 50%; transform: translate(-50%, 0); font-size:18pt\">© Кафедра МОСИТ - выпускникам, 2020-2021</p>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";
                if (receiver.getEmail().contains("@gmail.com")) {
                    messageHelper.setText(gmailHtmlMessage, true);
                } else {
                    messageHelper.setText(htmlMessage, true);
                }
                this.mailSender.send(mimeMessage);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "MessagingException";
        }
        return "All emails successfully sent!";
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