package pk.ni.pasir_piotrkowski_michal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupResponseDto {
    private Long id;
    private String name;
    private Long ownerId;
}
