package pk.ni.pasir_piotrkowski_michal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDto {
    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name must be at most 100 characters long")
    private String name;
}
