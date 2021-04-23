package com.dmv.footballheadz.manager.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import javax.validation.constraints.NotNull;
import java.util.*;

@DynamoDBTable(tableName = "Manager")
public class Manager {

    private String managerName;
    private Set<String> teams;
    private Boolean activeStatus;
    private Date activeDate;

    @DynamoDBHashKey(attributeName = "Manager")
    @NotNull(message = "Manager must not be empty")
    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String teamName) {
        this.managerName = teamName;
    }

    public Manager withManagerName(String teamName) {
        setManagerName(teamName);
        return this;
    }

    @DynamoDBAttribute(attributeName = "Teams")
    public Set<String> getTeams() {
        return teams;
    }

    public void setTeams(Set<String> teams) {
        this.teams = teams;
    }

    public Manager withTeams(List teams) {
        if (getTeams() == null) {
            setTeams(new HashSet<>());
        }
        getTeams().addAll(teams);
        return this;
    }

    @DynamoDBAttribute(attributeName = "ActiveStatus")
    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Manager withActiveStatus(Boolean activeStatus) {
        setActiveStatus(activeStatus);
        return this;
    }

    @DynamoDBAttribute(attributeName = "ActiveDate")
    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Manager withActiveDate(Date activeDate) {
        setActiveDate(activeDate);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Manager)) return false;
        Manager manager = (Manager) o;
        return managerName.equals(manager.managerName) && Objects.equals(teams, manager.teams) && Objects.equals(activeStatus, manager.activeStatus) && Objects.equals(activeDate, manager.activeDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerName, teams, activeStatus, activeDate);
    }
}
