package com.lama.otp;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.common.ClientConnection;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

public class OTPAuthenticationFactory implements AuthenticatorFactory {

    private static AuthenticatorConfigModel configModel = null;
    private static ClientModel clientModel = null;
    private static KeycloakSession keycloakSession = null;
    private static ClientConnection clientConnection = null;
    private static MultivaluedMap<String, String> formData = null;
    private static RealmModel realmModel = null;
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED};

    @Override
    public String getDisplayType() {
        return Constants.CONF_PRP_LABEL;
    }

    @Override
    public String getReferenceCategory() {
        return Constants.CONF_PRP_PROVIDER_ID;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }
    
    @Override
    public String getHelpText() {
        return Constants.CONF_PRP_HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return setProviderConfigProperties();
    }

    private static List<ProviderConfigProperty> setProviderConfigProperties() {
        List<ProviderConfigProperty> configProperties = new ArrayList<>();
        ProviderConfigProperty property;

        property = new ProviderConfigProperty();
        property.setName(Constants.CONF_PRP_OTP_VERIFICATION_ENDPOINT_URL);
        property.setLabel("OTP Endpoint Verification URL");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("https://webhook.site/#!/77742bbf-0460-4b91-84f8-a2a6abccaf2c/4461838f-6d45-4954-81e2-d4a716f836ef/1");
        configProperties.add(property);
        return configProperties;
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new OTPProvider();
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return Constants.CONF_PRP_PROVIDER_ID;
    }


}
