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
package net.maritimecloud.serviceregistry.command.servicespecification;

/**
 * The ServiceType indicates something about the communication interface and its protocol
 * <p>
 * @author Christoffer Børrild
 */
public enum ServiceType {

    MMS(Category.M2M, "mms", "An M2M service provided over Maritime Messaging Service"),
    REST(Category.M2M, "http/https scheme", "An M2M service provided by a REST web service"),
    SOAP(Category.M2M, "http/https scheme", "An M2M service provided by a SOAP web service"),
    HTTP(Category.M2M, "http/https scheme", "An M2M service provided by a web service not characterized as REST or SOAP"),
    FTP(Category.M2M, "ftp", "An service provided by access to an FTP server"),
    TCP(Category.M2M, "tcp", "An M2M service provided over a TCP connection"),
    UDP(Category.M2M, "udp", "An M2M service provided using connectionless UDP messages"),
    AISASM(Category.M2M, "ais", "An M2M service provided using AIS Application Specific Messages"),
    WWW(Category.OTHER, "http/https scheme", "A service provided by a human readable web site"),
    EMAIL(Category.OTHER, "mailto", "A service provided over email"),
    TEL(Category.OTHER, "tel", "A service provided using telephone calls, either voice or fax"),
    VHF(Category.OTHER, "vhf", "A service provided using VHF voice communication"),
    NAVTEX(Category.OTHER, "navtex", "A service provided using NAVTEX"),
    DGNSS(Category.OTHER, "dgnss", "A service provided by a number of DGNSS stations");

    /**
     * (Technical) Services can be divided into a number of service types. These can be grouped into machine-to-machine services and
     * services involving human interaction with a machine or with another human. These groups are modeled by the Category ENUM, and can
     * have the value of either M2M for Machine-To-Machine or OTHER for anything else.
     */
    public static enum Category {

        M2M, OTHER
    };

    private final Category category;
    private final String description;
    private final String endpointScheme;

    private ServiceType(final Category category, final String endpointScheme, final String description) {
        this.category = category;
        this.endpointScheme = endpointScheme;
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getEndpointScheme() {
        return endpointScheme;
    }

}
