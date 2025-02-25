package com.itsupportbackend.entity;

public enum Role {
    EMPLOYEE, IT_SUPPORT;

    public String getRoleName() {
        return this.name();
    }
}
