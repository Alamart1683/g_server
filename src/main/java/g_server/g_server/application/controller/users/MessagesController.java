package g_server.g_server.application.controller.users;

import g_server.g_server.application.entity.messanger.components.Message;
import g_server.g_server.application.entity.messanger.components.MessageSendForm;
import g_server.g_server.application.entity.messanger.components.Receiver;
import g_server.g_server.application.service.messager.MessagesService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;

@RestController
public class MessagesController {
    private MessagesService messagesService;
    private UsersService usersService;
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    public void setMessagesService(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/messages/get/received")
    public List<Message> getReceivedMessages(HttpServletRequest httpServletRequest) {
        return messagesService.getUsersReceivedMessages(
                usersService.getUserId(getTokenFromRequest(httpServletRequest))
        );
    }

    @GetMapping("/messages/get/sent")
    public List<Message> getSentMessages(HttpServletRequest httpServletRequest) {
        return messagesService.getUsersSentMessages(
                usersService.getUserId(getTokenFromRequest(httpServletRequest))
        );
    }

    @GetMapping("/messages/get/correspondence/")
    public List<Message> getCorrespondenceMessages(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer secondUserID
    ) {
        return messagesService.getUserToUserMessages(
                usersService.getUserId(getTokenFromRequest(httpServletRequest)), secondUserID
        );
    }

    @GetMapping("/messages/get/sent/to/")
    public List<Message> getSentMessagesTo( // Получаем сообщения которые отправил владелец токена указанному пользователю
            HttpServletRequest httpServletRequest,
            @RequestParam Integer receiverUserID
    ) {
        return messagesService.getUserToUserSentMessages(
                usersService.getUserId(getTokenFromRequest(httpServletRequest)),
                receiverUserID
        );
    }

    @GetMapping("/messages/get/received/from/")
    public List<Message> getReceivedMessagesFrom( // Получаем сообщения которые получил владелец токена от указанного пользователя
               HttpServletRequest httpServletRequest,
               @RequestParam Integer senderUserID
    ) {
        return messagesService.getUserToUserReceivedMessages(
                usersService.getUserId(getTokenFromRequest(httpServletRequest)),
                senderUserID
        );
    }

    @PostMapping("/messages/send")
    public String sendMessage(
            @ModelAttribute("messageSendForm") @Validated MessageSendForm messageSendForm,
            HttpServletRequest httpServletRequest) {
        return messagesService.sendMessage(
                usersService.getUserId(getTokenFromRequest(httpServletRequest)), messageSendForm);
    }

    @GetMapping("/messages/find/receivers/")
    public List<Receiver> findReceivers(@RequestParam String inputString) {
        return messagesService.findReceiver(inputString);
    }

    @GetMapping("/messages/find/receivers/associated")
    public List<Receiver> findAssociatedReceivers(HttpServletRequest httpServletRequest) {
        return messagesService.getAssociatedReceivers(usersService.getUserId(getTokenFromRequest(httpServletRequest)));
    }

    @GetMapping("/messages/find/recent/contacts/")
    public List<Object> findRecentContacts(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer limit // Количество недавних контактов
    ) {
        return messagesService.getMyRecentContact(
                usersService.getUserId(getTokenFromRequest(httpServletRequest)),
                limit
        );
    }

    @PostMapping("/messages/read/")
    public String readMessage(
            HttpServletRequest httpServletRequest,
            @RequestParam Integer messageID,
            @RequestParam Integer receiverID
    ) {
        Integer userID = usersService.getUserId(getTokenFromRequest(httpServletRequest));
        if (userID.equals(receiverID)) {
            return messagesService.readMessage(messageID, receiverID);
        } else {
            return "Попытка прочитать чужое сообщение";
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}