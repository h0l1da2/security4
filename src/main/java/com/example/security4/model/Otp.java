package com.example.security4.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Otp {

    @Id
    private String username;
    private String code;
}
