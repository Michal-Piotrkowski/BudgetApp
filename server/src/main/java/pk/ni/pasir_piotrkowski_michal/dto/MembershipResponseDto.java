package pk.ni.pasir_piotrkowski_michal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MembershipResponseDto {
    private Long id;
    private Long userId;
    private Long groupId;
    private String userEmail;
}
