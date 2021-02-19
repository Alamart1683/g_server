package g_server.g_server.application.service.messager;

import g_server.g_server.application.entity.messanger.Messages;
import g_server.g_server.application.entity.messanger.components.Message;
import g_server.g_server.application.entity.messanger.components.MessageSendForm;
import g_server.g_server.application.entity.messanger.components.Receiver;
import g_server.g_server.application.entity.messanger.components.Sender;
import g_server.g_server.application.entity.users.AssociatedStudents;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.messanger.MessagesRepository;
import g_server.g_server.application.repository.users.AssociatedStudentsRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.mail.MailService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class MessagesService {
    private MessagesRepository messagesRepository;
    private UsersRepository usersRepository;
    private MailService mailService;
    private UsersService usersService;
    private AssociatedStudentsRepository associatedStudentsRepository;

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

    @Autowired
    public void setUsersService(UsersService usersService) { this.usersService = usersService; }

    @Autowired
    public void setAssociatedStudentsRepository(AssociatedStudentsRepository associatedStudentsRepository) {
        this.associatedStudentsRepository = associatedStudentsRepository;
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
            List<Integer> isRedList = new ArrayList<>();
            String[] receivers = dbMessage.getReceivers().split(",");
            String[] isRedArray = dbMessage.getIsRedString().split(",");
            for (String receiver : receivers) {
                receiverList.add(getMessageReceiver(Integer.parseInt(receiver)));
            }
            for (String isRed: isRedArray) {
                isRedList.add(Integer.parseInt(isRed));
            }
            return new Message(
                    dbMessage.getId(),
                    sender,
                    getRussianDateTime(dbMessage.getSendDate()),
                    dbMessage.getMessageTheme(),
                    dbMessage.getMessageText(),
                    receiverList,
                    isRedList
            );
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

    // Метод получения "переписки" (Всех отправленных пользователем другому пользователю сообщений и наоборот
    public List<Message> getUserToUserMessages(Integer firstUserID, Integer secondUserID) {
        List<Messages> dbMessagesList = messagesRepository.findAll();
        List<Message> userToUserMessageList = new ArrayList<>();
        for (Messages dbMessage: dbMessagesList) {
            if ((dbMessage.getSender().equals(firstUserID.toString()) &&
                    isReceiver(secondUserID, dbMessage.getReceivers().split(","))) ||
                    (dbMessage.getSender().equals(secondUserID.toString()) &&
                            isReceiver(firstUserID, dbMessage.getReceivers().split(",")))) {
                userToUserMessageList.add(getMessage(dbMessage));
            }
        }
        return userToUserMessageList;
    }

    // Метод получения всех отправленных сообщений одним пользователем другому
    public List<Message> getUserToUserSentMessages(Integer senderUserID, Integer receiverUserID) {
        List<Messages> dbMessagesList = messagesRepository.findAll();
        List<Message> userToUserSendMessageList = new ArrayList<>();
        for (Messages dbMessage: dbMessagesList) {
            if (dbMessage.getSender().equals(senderUserID.toString()) &&
                    isReceiver(receiverUserID, dbMessage.getReceivers().split(","))) {
                userToUserSendMessageList.add(getMessage(dbMessage));
            }
        }
        return userToUserSendMessageList;
    }

    // Метод получения всех полученных сообщений одним пользователем от другого
    public List<Message> getUserToUserReceivedMessages(Integer receiverUserID, Integer senderUserID) {
        List<Messages> dbMessagesList = messagesRepository.findAll();
        List<Message> userToUserSendMessageList = new ArrayList<>();
        for (Messages dbMessage: dbMessagesList) {
            if (dbMessage.getSender().equals(senderUserID.toString()) &&
                    isReceiver(receiverUserID, dbMessage.getReceivers().split(","))) {
                userToUserSendMessageList.add(getMessage(dbMessage));
            }
        }
        return userToUserSendMessageList;
    }

    // Метод отправки сообщения
    public String sendMessage(Integer senderId, MessageSendForm messageSendForm) {
        try {
            Sender sender = getMessageSender(senderId);
            List<Receiver> receiverList = new ArrayList<>();
            String isRedString = "";
            for (String receiver: messageSendForm.getReceivers().split(",")) {
                receiverList.add(getMessageReceiver(Integer.parseInt(receiver)));
            }
            for (int i = 0; i < receiverList.size(); i++) {
                if (i < receiverList.size() - 1) {
                    isRedString += "0,";
                } else {
                    isRedString += "0";
                }
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Messages message = new Messages();
            message.setSender(senderId.toString());
            message.setSendDate(timestamp.toString());
            message.setMessageTheme(messageSendForm.getMessageTheme());
            message.setMessageText(messageSendForm.getMessageText());
            message.setReceivers(messageSendForm.getReceivers());
            System.out.println(isRedString);
            message.setIsRedString(isRedString);
            messagesRepository.save(message);
            String mailResult = mailService.sendMailByMessage(sender, messageSendForm.getMessageTheme(),
                    messageSendForm.getMessageText(), receiverList);
            return "Сообщение успешно отправлено! \n" + mailResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка отправки сообщения!";
        }
    }

    // Получить всех возможных получателей сообщений для поиска
    private List<Receiver> getAllExistReceivers() {
        List<Receiver> receiverList = new ArrayList<>();
        List<Users> usersList = usersRepository.findAll();
        for (Users user: usersList) {
            receiverList.add(new Receiver(
                    user.getId(),
                    user.getSurname() + " " + user.getName() + " " + user.getSecond_name(),
                    user.getEmail()
                )
            );
        }
        return receiverList;
    }

    // Метод поиска получателей
    public List<Receiver> findReceiver(String userInput) {
        List<Receiver> allReceiversList = getAllExistReceivers();
        List<Receiver> foundedReceivers = new ArrayList<>();
        for (Receiver receiver: allReceiversList) {
            String receiverString = receiver.getFio() + " " + receiver.getEmail();
            if ((receiverString).contains(userInput)) {
                foundedReceivers.add(receiver);
            }
        }
        return foundedReceivers;
    }

    /*
    Метод поиска получателей для научного руководителя или студента:
    Студент получает данные своего научного руководителя и его студентов
    Научный руководитель получает данные о всех его студентах
    */
    public List<Receiver> getAssociatedReceivers(Integer userID) {
        List<Receiver> myReceiverList = new ArrayList<>();
        String userRole = usersService.getUserRoleByRoleID(userID).substring(5).toLowerCase();
        AssociatedStudents associatedStudent;
        List<AssociatedStudents> associatedStudents;
        switch (userRole) {
            case "student":
                associatedStudent = associatedStudentsRepository.findByStudent(userID);
                int advisorID = associatedStudent.getScientificAdvisor();
                associatedStudents = associatedStudentsRepository.findByScientificAdvisor(advisorID);
                myReceiverList.add(getMessageReceiver(advisorID));
                for (AssociatedStudents student: associatedStudents) {
                    if (student.getStudent() != userID) {
                        myReceiverList.add(getMessageReceiver(student.getStudent()));
                    }
                }
                break;
            case "scientific_advisor":
                associatedStudents = associatedStudentsRepository.findByScientificAdvisor(userID);
                for (AssociatedStudents student: associatedStudents) {
                    myReceiverList.add(getMessageReceiver(student.getStudent()));
                }
                break;
            default:
                break;
        }
        return myReceiverList;
    }

    // Получить список недавних контактов
    public List<Object> getMyRecentContact(Integer userID, Integer limit) {
        List<Messages> messagesList = messagesRepository.findAll();
        List<Object> myRecentContacts = new ArrayList<>();
        List<Integer> contactsID = new ArrayList<>();
        for(int i = messagesList.size() - 1; i >= 0; i--) {
            if (messagesList.get(i).getSender().equals(userID.toString())) {
                String[] receivers = messagesList.get(i).getReceivers().split(",");
                for (String receiver: receivers) {
                    if (myRecentContacts.size() < limit && !contactsID.contains(Integer.parseInt(receiver))
                    ) {
                        myRecentContacts.add(getMessageReceiver(Integer.parseInt(receiver)));
                        contactsID.add(Integer.parseInt(receiver));
                    } else {
                        return myRecentContacts;
                    }
                }
            } else if (isReceiver(userID, messagesList.get(i).getReceivers().split(","))) {
                if (myRecentContacts.size() < limit &&
                        !contactsID.contains(Integer.parseInt(messagesList.get(i).getSender()))
                ) {
                    myRecentContacts.add(getMessageSender(Integer.parseInt(messagesList.get(i).getSender())));
                    contactsID.add(Integer.parseInt(messagesList.get(i).getSender()));
                } else {
                    return myRecentContacts;
                }
            }
        }
        return myRecentContacts;
    }

    // Прочитать сообщение от имени получателя
    public String readMessage(Integer messageID, Integer receiverID) {
        Messages message;
        if (messagesRepository.findById(messageID).isPresent()) {
            message = messagesRepository.findById(messageID).get();
            String[] receivers = message.getReceivers().split(",");
            int receiverIndex = -1;
            for (int i = 0; i < receivers.length; i++) {
                if (receivers[i].equals(receiverID.toString())) {
                    receiverIndex = i;
                    break;
                }
            }
            String[] isReadStringArray = message.getIsRedString().split(",");
            if (receiverID > -1) {
                String editedRedString = "";
                isReadStringArray[receiverIndex] = "1";
                for (int i = 0; i < isReadStringArray.length; i++) {
                    if (i < isReadStringArray.length - 1) {
                        editedRedString = editedRedString + isReadStringArray[i] + ",";
                    } else {
                        editedRedString = editedRedString + isReadStringArray[i];
                    }
                }
                message.setIsRedString(editedRedString);
                messagesRepository.save(message);
                return "Сообщение прочитано";
            } else {
                return "Получатель не найден";
            }
        }
        return "Сообщение не найдено";
    }
}