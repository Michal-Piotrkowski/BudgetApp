package pk.ni.pasir_piotrkowski_michal.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import pk.ni.pasir_piotrkowski_michal.handlers.NotificationHandler;
import pk.ni.pasir_piotrkowski_michal.model.Debt;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationHandler notificationHandler;

    public void sendNotification(Debt debt) throws IOException {
        Long groupId = debt.getGroup().getId();
        String message = debt.getCreditor().getEmail() +
                " added a new debt: " +
                String.format("\"%s\"", debt.getTitle()) +
                " in group " +
                String.format("%s.", debt.getGroup().getName()) +
                " Your debt amount is: " +
                String.format("%.2f", debt.getAmount()) +
                " zł. Please pay it ASAP.";

        String jsonMessage = getString(debt, message, groupId);
        notificationHandler.sendNotificationToUser(jsonMessage, debt.getDebtor().getId());
    }

    private static @NonNull String getString(Debt debt, String message, Long groupId) {
        String messageStr = message.replace("\"", "\\\"");
        return String.format(java.util.Locale.US, """
                {
                  "type": "GROUP_EXPENSE_ADDED",
                  "groupId": %d,
                  "groupName": "%s",
                  "title": "%s",
                  "amount": %.2f,
                  "userShare": %.2f,
                  "createdByEmail": "%s",
                  "message": "%s"
                }""",
                groupId,
                debt.getGroup().getName(),
                debt.getTitle(),
                debt.getAmount(),
                debt.getAmount(),
                debt.getCreditor().getEmail(),
                messageStr
        );
    }
}
