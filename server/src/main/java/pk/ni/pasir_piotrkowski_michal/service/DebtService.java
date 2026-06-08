package pk.ni.pasir_piotrkowski_michal.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pk.ni.pasir_piotrkowski_michal.mappers.DebtDtoToTransactionDtoMapper;
import pk.ni.pasir_piotrkowski_michal.mappers.DebtToDebtDtoMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pk.ni.pasir_piotrkowski_michal.dto.DebtDto;
import pk.ni.pasir_piotrkowski_michal.dto.TransactionDTO;
import pk.ni.pasir_piotrkowski_michal.model.Debt;
import pk.ni.pasir_piotrkowski_michal.model.Group;
import pk.ni.pasir_piotrkowski_michal.model.TransactionType;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.repository.DebtRepository;
import pk.ni.pasir_piotrkowski_michal.repository.GroupRepository;
import pk.ni.pasir_piotrkowski_michal.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtService {
    private final DebtRepository debtRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final CurrentUserService currentUserService;
    private final TransactionService transactionService;

    public List<Debt> getGroupDebts(Long groupId) {
        membershipService.assertCurrentUserIsGroupMember(groupId);
        return debtRepository.findByGroupId(groupId);
    }

    public Debt createDebt(DebtDto debtDto) {
        return createDebtForUser(debtDto, currentUserService.getCurrentUser());
    }

    public Debt createDebtForUser(DebtDto debtDto, User user){
        Group group = groupRepository.findById(debtDto.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot create debt. Group with ID " + debtDto.getGroupId() + " does not exist."));

        User debtor = userRepository.findById(debtDto.getDebtorId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot create debt. Debtor with ID " + debtDto.getDebtorId() + " does not exist."));

        User creditor = userRepository.findById(debtDto.getCreditorId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot create debt. Creditor with ID " + debtDto.getCreditorId() + " does not exist."));

        // Assert memberships
        membershipService.assertCurrentUserIsGroupMember(group.getId());
        membershipService.assertUserIsGroupMember(group.getId(), debtor.getId());
        membershipService.assertUserIsGroupMember(group.getId(), creditor.getId());

        if (debtor.getId().equals(creditor.getId())) {
            throw new IllegalStateException("Debtor and creditor must be different users.");
        }

        assertCurrentUserCanManageDebt(group, debtor, creditor, user);

        Debt debt = new Debt();
        debt.setGroup(group);
        debt.setDebtor(debtor);
        debt.setCreditor(creditor);
        debt.setAmount(debtDto.getAmount());
        debt.setTitle(debtDto.getTitle());

        return debtRepository.save(debt);
    }

    public void deleteDebt(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Debt with id " + debtId + " does not exist."));

        Long groupId = debt.getGroup().getId();
        membershipService.assertCurrentUserIsGroupMember(groupId);

        User currentUser = currentUserService.getCurrentUser();
        assertCurrentUserCanManageDebt(debt.getGroup(), debt.getDebtor(), debt.getCreditor(), currentUser);

        debtRepository.delete(debt);
    }

    private void assertCurrentUserCanManageDebt(Group group, User debtor, User creditor, User currentUser) {
        boolean isOwner = group.getOwner() != null && group.getOwner().getId().equals(currentUser.getId());
        boolean isInvolved = debtor.getId().equals(currentUser.getId()) || creditor.getId().equals(currentUser.getId());
        if (!(isOwner || isInvolved)) {
            throw new AccessDeniedException("You do not have permission to manage debts in this group.");
        }
    }

    public boolean markDebtAsPaid(Long debtId){
        Debt debt = getDebtForCurrentGroupMember(debtId);
        User currentUser = currentUserService.getCurrentUser();

        if(!debt.getDebtor().getId().equals(currentUser.getId())){
            throw new AccessDeniedException("Only the debtor can mark the debt as paid.");
        }

        DebtDto debtDto = DebtToDebtDtoMapper.map(debt);
        TransactionDTO transactionDTODebtor = DebtDtoToTransactionDtoMapper.map(debtDto, TransactionType.EXPENSE);
        transactionService.createTransaction(transactionDTODebtor);
        TransactionDTO transactionDTOCreditor = DebtDtoToTransactionDtoMapper.map(debtDto, TransactionType.INCOME);
        transactionService.createTransactionForUser(transactionDTOCreditor, debt.getCreditor());

        debt.setPaidByDebtor(true);
        debt.setConfirmedByCreditor(false); // Reset creditor confirmation if debtor changes payment status
        debtRepository.save(debt);
        return true;
    }

    public boolean confirmDebtPayment(Long debtId){
        Debt debt = getDebtForCurrentGroupMember(debtId);
        User currentUser = currentUserService.getCurrentUser();

        if(!debt.getCreditor().getId().equals(currentUser.getId())){
            throw new AccessDeniedException("Only the creditor can confirm the debt payment.");
        }

        if(!debt.isPaidByDebtor()){
            throw new IllegalStateException(
                    "Debtor must mark the debt as paid before creditor can confirm it.");
        }

        debt.setConfirmedByCreditor(true);
        debtRepository.save(debt);
        return true;
    }

    private Debt getDebtForCurrentGroupMember(Long debtId){
        Debt debt = debtRepository.findById(debtId).orElseThrow(
                () -> new EntityNotFoundException("Debt with id " + debtId + " does not exist.")
        );

        membershipService.assertCurrentUserIsGroupMember(debt.getGroup().getId());
        return debt;
    }
}