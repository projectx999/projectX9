package com.hydra.divideup.model;

import javax.validation.constraints.NotNull;

public record UserDTO(
    @NotNull(message = "name must not be null") String name,
    @NotNull(message = "phone number must not be null") String phone,
    @NotNull(message = "email must not be null") String email,
    @NotNull(message = "password must not be null") String password) {}
