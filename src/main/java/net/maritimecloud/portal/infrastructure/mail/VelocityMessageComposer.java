/* Copyright 2014 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.maritimecloud.portal.infrastructure.mail;

import java.util.HashMap;
import java.util.Map;
import net.maritimecloud.identityregistry.command.api.ResetPasswordKeyGenerated;
import net.maritimecloud.identityregistry.command.api.UnconfirmedUserEmailAddressSupplied;
import net.maritimecloud.identityregistry.command.api.UserRegistered;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 *
 * @author Christoffer Børrild
 */
public class VelocityMessageComposer implements MessageComposer {

    private static final String BASE_URL = "http://localhost:8080/app/index.html";
    private static final String TEMPLATE_SIGN_UP_ACTIVATION_MESSAGE = "templates/signUpActivationMessage.vm.html";
    private static final String TEMPLATE_RESET_PASSWORD_MESSAGE = "templates/resetPasswordMessage.vm.html";
    private static final String TEMPLATE_CONFIRM_CHANGED_EMAIL_ADDRESS_MESSAGE = "templates/confirmChangedEmailAddressMessage.vm.html";
    private final VelocityEngine velocityEngine;

    public VelocityMessageComposer(VelocityEngine velocityEngine) {
        assert (velocityEngine != null);
        this.velocityEngine = velocityEngine;
    }

    @Override
    public String composeSignUpActivationMessage(UserRegistered event) {
        assertNotNull(event.getEmailVerificationCode());
        return compose(createModel(event.getPrefferedUsername(), event.getEmailVerificationCode()), TEMPLATE_SIGN_UP_ACTIVATION_MESSAGE);
    }

    private void assertNotNull(Object value) throws IllegalStateException {
        if(value == null)
            throw new IllegalStateException("User object is missing activationid or reset key!");
    }

    private String compose(Map model, String templateFilename) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateFilename, "UTF-8", model);
    }

    @Override
    public String composeResetPasswordMessage(ResetPasswordKeyGenerated event) {
        // TODO: consider to rename activationId to confirmationId or verificationId
        assertNotNull(event.getResetPasswordKey());
        return compose(createModel(event.getUsername(), event.getResetPasswordKey()), TEMPLATE_RESET_PASSWORD_MESSAGE);
    }

    private Map createModel(String username, String key) {
        Map model = new HashMap();
        model.put("username", username);
        model.put("baseUrl", BASE_URL);
        model.put("activationid", key);
        return model;
    }

    @Override
    public String composeConfirmChangedEmailAddressMessage(UnconfirmedUserEmailAddressSupplied event) {
        assertNotNull(event.getEmailVerificationCode());
        return compose(createModel(event.getUsername(), event.getEmailVerificationCode()), TEMPLATE_CONFIRM_CHANGED_EMAIL_ADDRESS_MESSAGE);
    }

}
