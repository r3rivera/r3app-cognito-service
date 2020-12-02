package com.r2playground.module.aws.cognito;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.r2playground.module.aws.cognito.domain.CognitoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoProviderConfig {

    @Bean
    public CognitoConfig createCognitoConfig(){
        return new CognitoConfig();
    }

    @Bean
    public AWSCognitoIdentityProvider awsCognitoIdentityProvider(){

        final CognitoConfig credential = createCognitoConfig();
        final AWSCredentialsProvider credentials = buildCredential(credential.getAWSAccessKeyId(),
                credential.getAWSSecretKey());
        return AWSCognitoIdentityProviderClientBuilder.standard().withCredentials(credentials)
                .withRegion(credential.getRegion()).build();
    }

    private AWSCredentialsProvider buildCredential(String accessKey, String secretKey){
        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
        return provider;
    }
}
