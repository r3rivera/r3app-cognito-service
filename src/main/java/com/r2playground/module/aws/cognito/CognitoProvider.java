package com.r2playground.module.aws.cognito;


import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.r2playground.module.aws.cognito.domain.CognitoConfig;
import com.r2playground.module.aws.cognito.model.AWSCognitoResponse;
import com.r2playground.module.aws.cognito.model.AppUser;
import com.r2playground.module.aws.cognito.model.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class CognitoProvider {


    private AWSCognitoIdentityProvider cognitoIdentityProvider;
    private CognitoConfig cognitoConfig;


    public CognitoProvider(
            @Autowired AWSCognitoIdentityProvider cognitoIdentityProvider,
            @Autowired CognitoConfig cognitoConfig
    ){
        this.cognitoIdentityProvider = cognitoIdentityProvider;
        this.cognitoConfig = cognitoConfig;
    }


    /**
     * Creates fresh new user
     * @param user
     * @return
     */
    public boolean createUser(AppUser user){
        final AdminCreateUserRequest adminCreateUserRequest = new AdminCreateUserRequest()
                .withUserPoolId(cognitoConfig.getPoolId())
                .withUsername(user.getEmail())
                .withTemporaryPassword("CFLProvidedPassw0rd!")
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withMessageAction(MessageActionType.SUPPRESS)
                .withUserAttributes(Arrays.asList(
                        new AttributeType().withName("email").withValue(user.getEmail()),
                        new AttributeType().withName("given_name").withValue(user.getFirstName()),
                        new AttributeType().withName("family_name").withValue(user.getLastName()),
                        new AttributeType().withName("email_verified").withValue("true")
                ));

        AdminCreateUserResult createUserResult = null;
        try{
            createUserResult = cognitoIdentityProvider.adminCreateUser(adminCreateUserRequest);

        }catch(UsernameExistsException ex){
            ex.printStackTrace();
        }catch(InvalidParameterException ex){
            ex.printStackTrace();
        }catch(CodeDeliveryFailureException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        UserType userType = createUserResult.getUser();
        return userType.isEnabled();
    }



    /**
     * Authenticate the user
     *
     * @param username
     * @param password
     * @return
     */
    public AWSCognitoResponse loginUser(String username, String password){
        final Map<String, String> params = new HashMap<>();
        params.put("USERNAME",username);
        params.put("PASSWORD", password);

        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withAuthParameters(params)
                .withClientId(cognitoConfig.getClientId())
                .withUserPoolId(cognitoConfig.getPoolId());

        AdminInitiateAuthResult result = null;
        try{
            result = cognitoIdentityProvider.adminInitiateAuth(authRequest);
        }catch(NotAuthorizedException ex){
            ex.printStackTrace();
        }catch(PasswordResetRequiredException ex){
            ex.printStackTrace();
        }catch(UserNotFoundException ex){
            ex.printStackTrace();
        }catch(UserNotConfirmedException ex){
            ex.printStackTrace();
        }catch(ResourceNotFoundException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        final AWSCognitoResponse response = new AWSCognitoResponse();
        response.setChallengeSessionId(result.getSession());
        response.setResponseType(ResponseType.getType(result.getChallengeName()));

        if(result.getAuthenticationResult() != null){
            response.setAccessToken(result.getAuthenticationResult().getAccessToken());
            response.setRefreshToken(result.getAuthenticationResult().getRefreshToken());
        }
        return response;
    }


    /**
     * Respond to auth challenge
     * @param username
     * @param newPassword
     * @param challengeSessionId
     * @return
     */
    public AWSCognitoResponse respondToAuthChallenge(String username, String newPassword, String challengeSessionId){

        Map<String, String> challengeResponse = new HashMap<>();
        challengeResponse.put("USERNAME", username);
        challengeResponse.put("NEW_PASSWORD", newPassword);

        final RespondToAuthChallengeRequest challengeRequest = new RespondToAuthChallengeRequest()
                .withClientId(cognitoConfig.getClientId())
                .withSession(challengeSessionId)
                .withChallengeName(ResponseType.NEW_PASSWORD_REQUIRED.getValue())
                .withChallengeResponses(challengeResponse);

        RespondToAuthChallengeResult challengeResult = null;

        try{
            challengeResult = cognitoIdentityProvider.respondToAuthChallenge(challengeRequest);
        }catch(UserNotFoundException ex) {
            ex.printStackTrace();
        }catch(InvalidPasswordException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        final AuthenticationResultType resultType = challengeResult.getAuthenticationResult();
        final AWSCognitoResponse response = new AWSCognitoResponse();
        response.setChallengeSessionId(challengeResult.getSession());
        response.setResponseType(ResponseType.getType(challengeResult.getChallengeName()));

        if(challengeResult.getAuthenticationResult() != null){
            response.setAccessToken(resultType.getAccessToken());
            response.setRefreshToken(resultType.getRefreshToken());
        }
        return response;

    }






}
