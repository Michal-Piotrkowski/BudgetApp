package pk.ni.pasir_piotrkowski_michal.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.ni.pasir_piotrkowski_michal.dto.GroupTransactionDto;
import pk.ni.pasir_piotrkowski_michal.model.Debt;
import pk.ni.pasir_piotrkowski_michal.model.Group;
import pk.ni.pasir_piotrkowski_michal.model.Membership;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.repository.DebtRepository;
import pk.ni.pasir_piotrkowski_michal.repository.GroupRepository;
import pk.ni.pasir_piotrkowski_michal.repository.MembershipRepository;
import pk.ni.pasir_piotrkowski_michal.service.NotificationService;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupTransactionService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final MembershipService membershipService;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public void addGroupTransaction(GroupTransactionDto transactionDTO) {
        Group group = groupRepository.findById(transactionDTO.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        membershipService.assertCurrentUserIsGroupMember(group.getId());

        List<Membership> members = membershipRepository.findByGroupId(group.getId());
        User currentUser = currentUserService.getCurrentUser();
        List<Membership> selectedMembers = selectParticipants(transactionDTO, members, currentUser);

        if (selectedMembers.isEmpty()) {
            throw new IllegalStateException("Group has no members, cannot add transaction.");
        }

        double amountPerUser = transactionDTO.getAmount() / selectedMembers.size();

        boolean expense = "EXPENSE".equals(transactionDTO.getType());

        for (Membership member : selectedMembers) {
            User otherUser = member.getUser();
            if (!otherUser.getId().equals(currentUser.getId())) {
                Debt debt = new Debt();
                debt.setDebtor(expense ? otherUser : currentUser);
                debt.setCreditor(expense ? currentUser : otherUser);
                debt.setGroup(group);
                debt.setAmount(amountPerUser);
                debt.setTitle(transactionDTO.getTitle());
                Debt savedDebt = debtRepository.save(debt);

                try {
                    notificationService.sendNotification(savedDebt);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send notification", e);
                }
            }
        }
    }

    private List<Membership> selectParticipants(
            GroupTransactionDto transactionDTO,
            List<Membership> members,
            User currentUser
    ) {
        List<Long> selectedUserIds = transactionDTO.getSelectedUserIds();

        if (selectedUserIds == null || selectedUserIds.isEmpty()) {
            return members;
        }

        Set<Long> uniqueSelectedUserIds = new HashSet<>(selectedUserIds);
        List<Membership> selectedMembers = members.stream()
                .filter(membership -> uniqueSelectedUserIds.contains(membership.getUser().getId()))
                .toList();

        if (selectedMembers.size() != uniqueSelectedUserIds.size()) {
            throw new IllegalStateException("All selected members must be part of the group");
        }

        boolean currentUserSelected = selectedMembers.stream()
                .anyMatch(membership -> membership.getUser().getId().equals(currentUser.getId()));

        if (!currentUserSelected) {
            throw new IllegalStateException("Current user must be included in the transaction participants");
        }

        if (selectedMembers.size() < 2){
            throw new IllegalStateException("At least two participants must be selected for the group transaction");
        }

        return selectedMembers;
    }
}
