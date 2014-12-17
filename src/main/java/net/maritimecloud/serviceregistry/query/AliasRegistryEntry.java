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
package net.maritimecloud.serviceregistry.query;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Christoffer Børrild
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"groupId" , "typeName", "alias"})})
public class AliasRegistryEntry {
    
    @Id
    @GeneratedValue
    private long id;

    private String groupId;
    private String typeName;
    private String alias;
    private String targetId;

    public AliasRegistryEntry() {
    }

    public AliasRegistryEntry(String groupId, String typeName, String alias, String targetId) {
        this.groupId = groupId;
        this.typeName = typeName;
        this.alias = alias;
        this.targetId = targetId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "AliasRegistryEntry{" + "groupId=" + groupId + ", typeName=" + typeName + ", alias=" + alias + ", targetId=" + targetId + '}';
    }

}
