package com.lama.otp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.ClientConnection;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

public class OTPProvider implements Authenticator {

    private static final Logger logger = Logger.getLogger(OTPProvider.class);
    private static AuthenticatorConfigModel configModel = null;
    private static EventBuilder event;
    private static ClientModel clientModel = null;
    private static KeycloakSession keycloakSession = null;
    private static ClientConnection clientConnection = null;
    private static MultivaluedMap<String, String> formData = null;
    private static RealmModel realmModel = null;
    boolean flag = false;


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (!flag) {
            setUp(context);

            /**
             * LoginFormsProvider.createForm() method loads a Freemarker template file from your login theme.
             * Additionally you can call the LoginFormsProvider.setAttribute() method if you want to pass additional information to the Freemarker template
             */
            Response challenge = context.form().createForm(Constants.FTL);
            context.challenge(challenge);
            context.failure(null);
            flag = true;
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        setUp(context);
        String otp = formData.getFirst(Constants.FTL_PRP_TEXT);
        if ( otp != null) {
            boolean result = validateOTP(otp);
            context.success();
            if (result) {
                context.success();
            } else {
                context.getAuthenticationSession().setAuthNote("otpError", "true");
                context.failure(AuthenticationFlowError.INTERNAL_ERROR);
            }
//              need to clear otp-value so when we have more than one otp in one flow it should call otp api again
            formData.remove(Constants.FTL_PRP_TEXT);
        }
    }


    private boolean validateOTP(String otp) {
        String OTP_VERIFICATION_ENDPOINT_URL = configModel.getConfig().get(Constants.CONF_PRP_OTP_VERIFICATION_ENDPOINT_URL);
        String valid_otp = "1111";
        if (otp != null) {
            HttpClient httpClient = keycloakSession.getProvider(HttpClientProvider.class).getHttpClient();
            HttpPost post = new HttpPost(OTP_VERIFICATION_ENDPOINT_URL);
            List<NameValuePair> formParams = new LinkedList<>();
            formParams.add(new BasicNameValuePair("otp", otp));
            formParams.add(new BasicNameValuePair("ip-address", clientConnection.getRemoteAddr()));
            try {
                UrlEncodedFormEntity form = new UrlEncodedFormEntity(formParams, "UTF-8");
                post.setEntity(form);
                HttpResponse response = httpClient.execute(post);
//                TODO: Njoud will figure this out
//                try (InputStream content = response.getEntity().getContent()) {
//                    return (boolean) JsonSerialization.readValue(content, Map.class).get("isValidOtp"); // a field called isValidOtp returned from the otp-verification-endpoint that tells us if the otp is valid or not.
//                }
                return otp.equals(valid_otp);
            } catch (Exception e) {
                logException("OTP_Error", clientModel, realmModel, keycloakSession, clientConnection, e.getMessage(), Constants.ErrorCode_ERROR);
                return false;
            }
        }
        return false;
    }

    
    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public void close() {

    }

    private void setUp(AuthenticationFlowContext context) {
        clientModel = context.getAuthenticationSession().getClient();
        keycloakSession = context.getSession();
        clientConnection = context.getConnection();
        configModel = context.getAuthenticatorConfig();
        realmModel = context.getRealm();
        formData = context.getHttpRequest().getDecodedFormParameters();
    }


    private void logException(String eventType, ClientModel clientModel, RealmModel realmModel, KeycloakSession
            keycloakSession, ClientConnection clientConnection, String exception, String errorCode) {
        event = new EventBuilder(realmModel, keycloakSession, clientConnection);
        event.event(EventType.valueOf(eventType)).client(clientModel)
                .detail("exception", exception)
                .error(errorCode);
    }
}
