package sh.fyz.clutch.api.entity;


import jakarta.persistence.*;
import sh.fyz.architect.entities.IdentifiableEntity;
import sh.fyz.fiber.annotations.auth.IdentifierField;
import sh.fyz.fiber.annotations.auth.PasswordField;
import sh.fyz.fiber.annotations.dto.IgnoreDTO;
import sh.fyz.fiber.core.authentication.entities.UserAuth;
import sh.fyz.fiber.core.dto.DTOConvertible;
import sh.fyz.fiber.validation.Email;
import sh.fyz.fiber.validation.NotBlank;
import sh.fyz.fiber.validation.NotNull;

import java.util.Map;

@Entity
@Table(name = "users")
public class    User extends DTOConvertible implements IdentifiableEntity, UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String discordId;

    @IdentifierField
    @Email
    @NotBlank
    @NotNull
    private String email;

    @NotBlank
    @NotNull
    private String firstName;

    @NotBlank
    @NotNull
    private String lastName;

    private boolean isCompany;
    private String companyName;
    private String vatNumber;

    private String phone;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    @PasswordField
    @NotBlank
    @NotNull
    @IgnoreDTO
    private String password;

    private String role;
    private long createdAt;
    private long lastLogin;

    public User() {}

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public String getRole() {
        return role;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isCompany() {
        return isCompany;
    }

    public void setCompany(boolean isCompany) {
        this.isCompany = isCompany;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPassword() {
        return password;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }
}
