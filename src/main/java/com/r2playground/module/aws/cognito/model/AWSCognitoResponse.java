package com.r2playground.module.aws.cognito.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@ToString
public class AWSCognitoResponse {
    private ResponseType responseType;
    private String challengeSessionId;
    private String accessToken;
    private String refreshToken;
    private AppUser userDetails;

}
