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
 *
 * @author Christoffer Børrild
 */
public class Mail {

    private final String recipients;
    private final String subject;
    private final String message;

    public Mail(String recipients, String subject, String message) {
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return "Mail{" + "\nrecipients=" + recipients + ", \nsubject=" + subject + ", \nmessage=" + message + '}';
    }
    
    
}
