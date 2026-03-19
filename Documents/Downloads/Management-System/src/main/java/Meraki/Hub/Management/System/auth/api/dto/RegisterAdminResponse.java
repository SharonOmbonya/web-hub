package Meraki.Hub.Management.System.auth.api.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private boolean approved;
}
