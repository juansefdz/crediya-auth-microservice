package co.com.pragma.config;

import co.com.pragma.model.auth.gateways.AuthGateway;
import co.com.pragma.model.auth.gateways.CredentialGateway;
import co.com.pragma.model.auth.gateways.JwtProviderGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.auth.AuthenticateUserUseCase;
import co.com.pragma.usecase.auth.InitialRegistrationUseCase;
import co.com.pragma.usecase.user.UserUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

        @Bean
        public InitialRegistrationUseCase initialRegistrationUseCase(
                UserRepository userRepository,
                RoleRepository roleRepository,
                PasswordEncoderGateway passwordEncoder,
                CredentialGateway credentialGateway,
                @Value("${app.security.admin-role-id}") Long adminRoleId,
                @Value("${app.security.default-role-id}") Long defaultRoleId
        ) {
                return new InitialRegistrationUseCase(
                        userRepository,
                        roleRepository,
                        passwordEncoder,
                        credentialGateway,
                        adminRoleId,
                        defaultRoleId
                );
        }

        @Bean
        public AuthenticateUserUseCase authenticateUserUseCase(
                AuthGateway authGateway,
                PasswordEncoderGateway passwordEncoder,
                JwtProviderGateway jwtProvider
        ) {
                return new AuthenticateUserUseCase(authGateway, passwordEncoder, jwtProvider);
        }

        @Bean
        public UserUseCase userUseCase(
                UserRepository userRepository,
                RoleRepository roleRepository,
                PasswordEncoderGateway passwordEncoder,
                CredentialGateway credentialGateway
        ) {
                return new UserUseCase(userRepository, roleRepository, passwordEncoder, credentialGateway);
        }
}
