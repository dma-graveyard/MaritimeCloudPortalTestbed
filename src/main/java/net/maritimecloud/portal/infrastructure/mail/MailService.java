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

/**
 * MailService is responsible for composing and sending out domain 
 * specific mail messages.
 * 
 * @author Christoffer Børrild
 */
public class MailService {

    private final MessageComposer messageComposer;
    private final MailAdapter mailAdapter;

    public MailService(MessageComposer messageComposer, MailAdapter mailAdapter) {
        this.messageComposer = messageComposer;
        this.mailAdapter = mailAdapter;
    }

    public void sendSignUpActivationMessage(String emailAddress, String username, String verificationCode) {
        String message = messageComposer.composeSignUpActivationMessage(username, verificationCode);
        Mail mail = new Mail(emailAddress, "Account activation on Maritime Cloud Portal", message);
        mailAdapter.send(mail);
    }

    public void sendResetPasswordMessage(String emailAddress, String username, String resetPasswordCode) {
        String message = messageComposer.composeResetPasswordMessage(username, resetPasswordCode);
        Mail mail = new Mail(emailAddress, "Password reset for Account at Maritime Cloud Portal", message);
        mailAdapter.send(mail);
    }

    public void sendConfirmChangedEmailAddressMessage(String emailAddress, String username, String verificationCode) {
        String message = messageComposer.composeConfirmChangedEmailAddressMessage(username, verificationCode);
        Mail mail = new Mail(emailAddress, "Account activation on Maritime Cloud Portal", message);
        mailAdapter.send(mail);
    }

}
