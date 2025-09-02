package co.com.pragma.model.auth;


import lombok.Value;


public record Credentials(String email, String password) {
}