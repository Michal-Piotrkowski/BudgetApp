package pk.ni.pasir_piotrkowski_michal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pk.ni.pasir_piotrkowski_michal.mappers.DebtDtoToTransactionDtoMapper;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_piotrkowski_michal.dto.DebtDto;
import pk.ni.pasir_piotrkowski_michal.dto.TransactionDTO;
import pk.ni.pasir_piotrkowski_michal.model.Debt;
import pk.ni.pasir_piotrkowski_michal.model.TransactionType;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.service.DebtService;
import pk.ni.pasir_piotrkowski_michal.service.NotificationService;
import pk.ni.pasir_piotrkowski_michal.service.TransactionService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class DebtGraphQLController {
    private final DebtService debtService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;

    @QueryMapping
    public List<Debt> groupDebts(@Argument Long groupId) {
        return debtService.getGroupDebts(groupId);
    }

    @MutationMapping
    public Debt createDebt(@Valid @Argument DebtDto debtDTO) throws IOException {
        Debt savedDebt = debtService.createDebt(debtDTO);

        TransactionType transactionType = TransactionType.EXPENSE;
        TransactionDTO transactionDTO = DebtDtoToTransactionDtoMapper.map(debtDTO, transactionType);

        User creditor = savedDebt.getCreditor();
        transactionService.createTransactionForUser(transactionDTO, creditor);

        notificationService.sendNotification(savedDebt);

        return savedDebt;
    }

    @MutationMapping
    public Boolean deleteDebt(@Argument Long debtId) {
        debtService.deleteDebt(debtId);
        return true;
    }

    @MutationMapping
    public Boolean markDebtAsPaid(@Argument Long debtId) {
        return debtService.markDebtAsPaid(debtId);
    }

    @MutationMapping
    public Boolean confirmDebtPayment(@Argument Long debtId) {
        return debtService.confirmDebtPayment(debtId);
    }
}