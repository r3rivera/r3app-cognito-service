package com.r2playground.module.aws.cognito.domain;

import com.amazonaws.auth.AWSCredentials;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@Data
@ToString
public class CognitoConfig implements AWSCredentials {

    @Value("${r3app.aws.region}")
    private String region;

    @Value("${r3app.aws.cognito.access.key.id}")
    private String awsAccessKey;

    @Value("${r3app.aws.cognito.secret.key}")
    private String awsSecretKey;

    @Value("${r3app.aws.cognito.clientid}")
    private String clientId;

    @Value("${r3app.aws.cognito.poolid}")
    private String poolId;

    @Override
    public String getAWSAccessKeyId() {
        return this.awsAccessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return this.awsSecretKey;
    }
}
