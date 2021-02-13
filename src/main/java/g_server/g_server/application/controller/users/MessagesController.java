package g_server.g_server.application.controller.users;

import g_server.g_server.application.entity.messanger.components.Message;
import g_server.g_server.application.entity.messanger.components.MessageSendForm;
import g_server.g_server.application.service.messager.MessagesService;
import g_server.g_server.application.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
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
        return messagesService.getUsersReceivedMessages(Integer.parseInt(getTokenFromRequest(httpServletRequest)));
    }

    @GetMapping("/messages/get/sent")
    public List<Message> getSentMessages(HttpServletRequest httpServletRequest) {
        return messagesService.getUsersSentMessages(Integer.parseInt(getTokenFromRequest(httpServletRequest)));
    }

    @PostMapping("/messages/send")
    public String sendMessage(
            @ModelAttribute("messageSendForm") @Validated MessageSendForm messageSendForm,
            HttpServletRequest httpServletRequest) {
        return messagesService.sendMessage(
                usersService.getUserId(getTokenFromRequest(httpServletRequest)), messageSendForm);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}