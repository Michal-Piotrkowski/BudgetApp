package pk.ni.pasir_piotrkowski_michal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtDto {
    @NotNull(message = "Debtor id is required")
    private Long debtorId;

    @NotNull(message = "Creditor id is required")
    private Long creditorId;

    @NotNull(message = "Group id is required")
    private Long groupId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters long")
    private String title;
}
