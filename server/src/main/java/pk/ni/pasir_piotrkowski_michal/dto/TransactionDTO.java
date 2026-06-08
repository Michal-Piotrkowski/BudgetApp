package pk.ni.pasir_piotrkowski_michal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pk.ni.pasir_piotrkowski_michal.model.TransactionType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @Size(max = 50, message = "Tags must be at most 50 characters long")

    @Pattern(regexp = "^[\\p{L}\\p{N},\\s-]*$", message = "Tags may contain letters, digits, spaces, commas and hyphens only")
    private String tags;

    @Size(max = 255, message = "Notes must be at most 255 characters long")
    private String notes;

    private LocalDateTime timestamp;
}
