package g_server.g_server.application.service.messager;

import g_server.g_server.application.entity.messanger.Messages;
import g_server.g_server.application.entity.messanger.components.Message;
import g_server.g_server.application.entity.messanger.components.MessageSendForm;
import g_server.g_server.application.entity.messanger.components.Receiver;
import g_server.g_server.application.entity.messanger.components.Sender;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.messanger.MessagesRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessagesService {
    private MessagesRepository messagesRepository;
    private UsersRepository usersRepository;
    private MailService mailService;

    @Autowired
    public void setMessagesRepository(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @Autowired
    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    // Вспомогательный метод трансформации даты
    private String getRussianDateTime(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String russianDate = day + "." + month + "." + year;
        return russianDate + date.substring(10);
    }

    // Вспомогательный метод получения информации о получателе
    private Receiver getMessageReceiver(Integer receiverId) {
        if (usersRepository.findById(receiverId).isPresent()) {
            Users receiverUser = usersRepository.findById(receiverId).get();
            return new Receiver(
                    receiverUser.getId(),
                    receiverUser.getSurname() + " " + receiverUser.getName() + " " + receiverUser.getSecond_name(),
                    receiverUser.getEmail()
            );
        }
        return null;
    }

    // Вспомогательный метод получения информации об отправителе
    private Sender getMessageSender(Integer senderId) {
        if (usersRepository.findById(senderId).isPresent()) {
            Users senderUser = usersRepository.findById(senderId).get();
            return new Sender(
                    senderUser.getId(),
                    senderUser.getSurname() + " " + senderUser.getName() + " " + senderUser.getSecond_name(),
                    senderUser.getEmail()
            );
        }
        return null;
    }

    // Метод получения необходимой информации о сообщении из базы данных
    public Message getMessage(Messages dbMessage) {
        Sender sender = getMessageSender(Integer.parseInt(dbMessage.getSender()));
        if (sender != null) {
            List<Receiver> receiverList = new ArrayList<>();
            String[] receivers = dbMessage.getReceivers().split(",");
            for (String receiver : receivers) {
                receiverList.add(getMessageReceiver(Integer.parseInt(receiver)));
            }
            return new Message(
                    dbMessage.getId(),
                    sender,
                    getRussianDateTime(dbMessage.getSendDate()),
                    dbMessage.getMessageTheme(),
                    dbMessage.getMessageText(),
                    receiverList
            );
        }
        return null;
    }

    // Метод получения всех отправленных пользователем сообщений
    public List<Message> getUsersSentMessages(Integer userId) {
        List<Message> messages = new ArrayList<>();
        if (usersRepository.findById(userId).isPresent()) {
            List<Messages> dbMessages = messagesRepository.findBySender(userId.toString());
            for (Messages dbMessage: dbMessages) {
                messages.add(getMessage(dbMessage));
            }
            return messages;
        }
        return null;
    }

    // Вспомогательный метод определения получателя в списке получателей
    private boolean isReceiver(Integer userId, String[] receivers) {
        for (String receiver: receivers) {
            if (receiver.equals(userId.toString())) {
                return true;
            }
        }
        return false;
    }

    // Метод получения всех отправленных пользователю сообщений
    public List<Message> getUsersReceivedMessages(Integer userId) {
        List<Messages> dbMessagesList = messagesRepository.findAll();
        List<Message> messagesList = new ArrayList<>();
        for (Messages dbMessage: dbMessagesList) {
            if (dbMessage.getReceivers().equals(userId.toString())) {
                messagesList.add(getMessage(dbMessage));
            } else if (isReceiver(userId, dbMessage.getReceivers().split(","))) {
                messagesList.add(getMessage(dbMessage));
            }
        }
        return messagesList;
    }

    // Метод отправки сообщения
    public String sendMessage(Integer senderId, MessageSendForm messageSendForm) {
        try {
            Sender sender = getMessageSender(senderId);
            List<Receiver> receiverList = new ArrayList<>();
            for (String receiver: messageSendForm.getReceivers().split(",")) {
                receiverList.add(getMessageReceiver(Integer.parseInt(receiver)));
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Messages message = new Messages();
            message.setSender(senderId.toString());
            message.setSendDate(timestamp.toString());
            message.setMessageTheme(messageSendForm.getMessageTheme());
            message.setMessageText(messageSendForm.getMessageText());
            message.setReceivers(messageSendForm.getReceivers());
            messagesRepository.save(message);
            mailService.sendMailByMessage(
                    sender,
                    messageSendForm.getMessageTheme(),
                    messageSendForm.getMessageText(),
                    receiverList
            );
            String mailResult = mailService.sendMailByMessage(sender, messageSendForm.getMessageTheme(),
                    messageSendForm.getMessageText(), receiverList);
            return "Сообщение успешно отправлено! \n" + mailResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка отправки сообщения!";
        }
    }
}