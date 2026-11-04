package com.sixlv6.backend.user;

import com.sixlv6.backend.common.InstantTextConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name="users")
public class User{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="username",nullable=false,length=32)
    private String username;

    @Column(name="email",nullable=false,length=128)
    private String email;

    @Column(name="password_hash",nullable=false,length=100)
    private String passwordHash;

    @Column(name="role",nullable=false,length=16)
    private String role="USER";

    @Column(name="status",nullable=false)
    private int status=1;

    @Convert(converter=InstantTextConverter.class)
    @Column(name="created_at",nullable=false,updatable=false)
    private Instant createdAt;

    @Convert(converter=InstantTextConverter.class)
    @Column(name="updated_at",nullable=false)
    private Instant updatedAt;

    /**第一次插入时补 createdAt，同时刷新 updatedAt */
    @PrePersist
    void onCreate(){
        Instant now=Instant.now();
        if(createdAt==null){
            createdAt=now;
        }
        updatedAt=now;
    }
 // ---------- getters / setters ----------
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }
    
    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash=passwordHash;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role=role;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status=status;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt){
        this.createdAt=createdAt;
    }

    public Instant getUpdatedAt(){
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt){
        this.updatedAt=updatedAt;
    }


}