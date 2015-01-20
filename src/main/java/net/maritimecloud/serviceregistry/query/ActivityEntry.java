/* Copyright 2015 Danish Maritime Authority.
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
package net.maritimecloud.serviceregistry.query;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author Christoffer Børrild
 */
@Entity
//@Table(uniqueConstraints = {
//    @UniqueConstraint(columnNames = {"organizationId", "userName"})})
public class ActivityEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long activityId;
    private String username;
    private String organizationId;
    private boolean isPublic;
    //@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateTime;
    private String eventType;
    private String eventSimpleType;
    private String title;
    private String summary;
    private String targetType;
    private String targetId;

    public ActivityEntry() {
    }

    public ActivityEntry(String username, String organizationId, boolean isPublic, Date dateTime, String eventType, String eventSimpleType, String title, String summary, String targetType, String targetId) {
        this.username = username;
        this.organizationId = organizationId;
        this.isPublic = isPublic;
        this.dateTime = dateTime;
        this.eventType = eventType;
        this.eventSimpleType = eventSimpleType;
        this.title = title;
        this.summary = summary;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public boolean isIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getEventSimpleType() {
        return eventSimpleType;
    }

    public void setEventSimpleType(String eventSimpleType) {
        this.eventSimpleType = eventSimpleType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public ActivityEntry(String organizationId, String username, String eventType, String title, String summary) {
        this.organizationId = organizationId;
        this.username = username;
        this.eventType = eventType;
        this.dateTime = new Date();
        this.title = title;
        this.summary = summary;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "ActivityEntry{" + "activityId=" + activityId + ", username=" + username + ", organizationId=" + organizationId + ", isPublic=" + isPublic + ", dateTime=" + dateTime + ", eventType=" + eventType + ", eventSimpleType=" + eventSimpleType + ", title=" + title + ", summary=" + summary + ", targetType=" + targetType + ", targetId=" + targetId + '}';
    }
    
}
