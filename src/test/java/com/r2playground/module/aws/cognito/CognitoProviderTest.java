package com.r2playground.module.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.r2playground.module.aws.cognito.domain.CognitoConfig;
import com.r2playground.module.aws.cognito.model.AWSCognitoResponse;
import com.r2playground.module.aws.cognito.model.AppUser;
import com.r2playground.module.aws.cognito.model.ResponseType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CognitoProviderConfig.class)
@EnableConfigurationProperties
@TestPropertySource(locations = "classpath:application.properties")
public class CognitoProviderTest {

    @Autowired
    private AWSCognitoIdentityProvider cognitoIdentityProvider;
    @Autowired
    private CognitoConfig cognitoConfig;

    private CognitoProvider provider;

    @Before
    public void init(){
            this.provider = new CognitoProvider(cognitoIdentityProvider, cognitoConfig);
    }


    @Test
    public void cognitoAvailableTest(){
        Assert.assertNotNull(cognitoIdentityProvider);
        Assert.assertNotNull(cognitoConfig);
        Assert.assertNotNull(cognitoConfig.getAWSSecretKey());
        Assert.assertNotNull(cognitoConfig.getAWSAccessKeyId());
        Assert.assertNotNull(cognitoConfig.getClientId());
        Assert.assertNotNull(cognitoConfig.getPoolId());
        Assert.assertNotNull(cognitoConfig.getRegion());
        Assert.assertNotNull(this.provider);
    }


    @Test
    public void createUserTest(){

        //Create/Migrate a user to cognito
        final AppUser user = new AppUser();
        user.setEmail("rr.rivs@hotmail.com");
        user.setFirstName("Ryan");
        user.setLastName("Rivera");

        final boolean response = this.provider.createUser(user);
        Assert.assertTrue(response);

        if(response){

            //Login to Cognito with the password used during createUser
            final AWSCognitoResponse awsCognitoResponse = this.provider.loginUser(user.getEmail(), "CFLProvidedPassw0rd!");
            Assert.assertNotNull(awsCognitoResponse);
            Assert.assertNotNull(awsCognitoResponse.getChallengeSessionId());
            Assert.assertTrue(ResponseType.NEW_PASSWORD_REQUIRED.equals(awsCognitoResponse.getResponseType()));
            Assert.assertNull(awsCognitoResponse.getAccessToken());
            Assert.assertNull(awsCognitoResponse.getRefreshToken());

            //Respond to the auth challenge
            final AWSCognitoResponse awsAuthChallengeResponse = this.provider.respondToAuthChallenge(user.getEmail(), "UserPr0videdPassword!",
                    awsCognitoResponse.getChallengeSessionId());
            Assert.assertNotNull(awsAuthChallengeResponse);
            Assert.assertNotNull(awsCognitoResponse.getChallengeSessionId());
            Assert.assertTrue(ResponseType.DEFAULT.equals(awsCognitoResponse.getResponseType()));
            Assert.assertNotNull(awsCognitoResponse.getAccessToken());
            Assert.assertNotNull(awsCognitoResponse.getRefreshToken());
        }

    }

    /**
     *
     */
    @Test
    public void authenticateUserTest(){

        final AppUser user = new AppUser();
        user.setEmail("rr.rivs@hotmail.com");

        final AWSCognitoResponse awsCognitoResponse = this.provider.loginUser(user.getEmail(), "UserPr0videdPassword!");
        Assert.assertNotNull(awsCognitoResponse);
        Assert.assertTrue(ResponseType.DEFAULT.equals(awsCognitoResponse.getResponseType()));
        Assert.assertNotNull(awsCognitoResponse.getAccessToken());
        Assert.assertNotNull(awsCognitoResponse.getRefreshToken());
    }


}
