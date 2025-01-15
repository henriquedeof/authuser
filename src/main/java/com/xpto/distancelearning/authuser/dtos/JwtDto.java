package com.xpto.distancelearning.authuser.dtos;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
// The @RequiredArgsConstructor Lombok annotation generates a constructor with the token parameter as a required parameter.
@RequiredArgsConstructor
public class JwtDto {

    // Lombok will generate a constructor with the token parameter as a required parameter.
    @NonNull
    private String token;

    // Because this is already defined, it is not necessary to generate a constructor with the type parameter as a required parameter.
    private String type = "Bearer";
}