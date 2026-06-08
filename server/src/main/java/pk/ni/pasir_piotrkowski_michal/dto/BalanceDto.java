package pk.ni.pasir_piotrkowski_michal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceDto {
    @NotNull
    private double totalIncome;

    @NotNull
    private double totalExpense;

    @NotNull
    private double balance;
}

