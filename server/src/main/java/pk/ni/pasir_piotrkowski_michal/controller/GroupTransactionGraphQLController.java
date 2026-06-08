package pk.ni.pasir_piotrkowski_michal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_piotrkowski_michal.dto.GroupTransactionDto;
import pk.ni.pasir_piotrkowski_michal.service.GroupTransactionService;

@RequiredArgsConstructor
@Controller
public class GroupTransactionGraphQLController {
    private final GroupTransactionService groupTransactionService;

    @MutationMapping
    public Boolean addGroupTransaction(@Argument GroupTransactionDto groupTransactionDTO) {
        groupTransactionService.addGroupTransaction(groupTransactionDTO);
        return true;
    }
}
