package pk.ni.pasir_piotrkowski_michal.mappers;

import org.springframework.stereotype.Component;
import pk.ni.pasir_piotrkowski_michal.dto.DebtDto;
import pk.ni.pasir_piotrkowski_michal.dto.TransactionDTO;
import pk.ni.pasir_piotrkowski_michal.model.TransactionType;

import java.time.LocalDateTime;

@Component
public class DebtDtoToTransactionDtoMapper {
    public static TransactionDTO map(DebtDto debtDto, TransactionType transactionType) {
        if(debtDto == null) {
            return null;
        }

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(debtDto.getAmount());
        transactionDTO.setNotes(debtDto.getTitle());
        transactionDTO.setTimestamp(LocalDateTime.now());

        transactionDTO.setType(transactionType);

        return transactionDTO;
    }
}
