package pk.ni.pasir_piotrkowski_michal.handlers;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pk.ni.pasir_piotrkowski_michal.service.GroupService;
import pk.ni.pasir_piotrkowski_michal.service.MembershipService;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class NotificationHandler extends TextWebSocketHandler {
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final MembershipService membershipService;
    private final GroupService groupService;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void sendNotificationToUser(String jsonMessage, Long targetUserId) {
        sessions.forEach(session -> {
            Long userId = (Long) session.getAttributes().get("userId");

            if (userId != null && userId.equals(targetUserId)) {
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void sendNotificationToGroup(String jsonMessage, Long targetGroupId) throws IOException {
        sessions.forEach(session -> {
            Long userId = (Long) session.getAttributes().get("userId");

            if (userId != null) {
                boolean isMember = membershipService.isUserMemberOfGroup(targetGroupId, userId);
                if(isMember){
                    try {
                        session.sendMessage(new TextMessage(jsonMessage));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
