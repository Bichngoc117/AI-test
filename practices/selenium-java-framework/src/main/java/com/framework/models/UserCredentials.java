package com.framework.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UserCredentials — Model đọc test data từ users.json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCredentials {

    @JsonProperty("type")
    private String type;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("expectedRole")
    private String expectedRole;

    @JsonProperty("description")
    private String description;

    // ==================== Constructors ====================

    public UserCredentials() {}

    public UserCredentials(String type, String email, String password) {
        this.type = type;
        this.email = email;
        this.password = password;
    }

    // ==================== Getters / Setters ====================

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getExpectedRole() { return expectedRole; }
    public void setExpectedRole(String expectedRole) { this.expectedRole = expectedRole; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "UserCredentials{type='" + type + "', email='" + email + "', role='" + expectedRole + "'}";
    }
}
