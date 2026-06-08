package pk.ni.pasir_piotrkowski_michal.mappers;

import org.springframework.stereotype.Component;
import pk.ni.pasir_piotrkowski_michal.dto.DebtDto;
import pk.ni.pasir_piotrkowski_michal.model.Debt;

@Component
public class DebtToDebtDtoMapper {
    public static DebtDto map(Debt debt){
        DebtDto debtDto = new DebtDto();
        debtDto.setAmount(debt.getAmount());
        debtDto.setDebtorId(debt.getDebtor().getId());
        debtDto.setCreditorId(debt.getCreditor().getId());
        debtDto.setGroupId(debt.getGroup().getId());
        debtDto.setTitle(debt.getTitle());
        return debtDto;
    }
}
