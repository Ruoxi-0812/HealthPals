package cn.kmbeast.pojo.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLoginDTO {
    /**
     * Google ID token from Google Identity Services
     */
    private String idToken;
}
