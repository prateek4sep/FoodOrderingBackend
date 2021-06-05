package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "customer")
@NamedQueries({
        @NamedQuery(
                name = "customerByContactNumber",
                query = "select u from CustomerEntity u where u.contactNumber=:contactNumber"),
        @NamedQuery(name = "customerByEmail", query = "select u from CustomerEntity u where u.email=:email"),
        @NamedQuery(name = "getCustomerByUUID", query = "select u from CustomerEntity u where u.uuid =:uuid")
})
public class CustomerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "FIRSTNAME")
    @Size(max = 30)
    @NotNull
    private String firstName;

    @Column(name = "LASTNAME")
    @Size(max = 30)
    private String lastName;

    @Column(name = "EMAIL")
    @Size(max = 50)
    private String email;

    @Column(name = "CONTACT_NUMBER")
    @Size(max = 30)
    @NotNull
    private String contactNumber;

    @Column(name = "PASSWORD")
    @Size(max = 255)
    @NotNull
    @ToStringExclude
    private String password;

    @Column(name = "SALT")
    @Size(max = 255)
    @NotNull
    @ToStringExclude
    private String salt;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "customers")
    private List<AddressEntity> address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<AddressEntity> getAddress() {
        return address;
    }

    public void setAddress(List<AddressEntity> address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}