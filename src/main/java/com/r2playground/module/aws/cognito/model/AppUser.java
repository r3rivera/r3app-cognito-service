package com.r2playground.module.aws.cognito.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@ToString
public class AppUser {
    private String firstName;
    private String lastName;
    private String email;
}
