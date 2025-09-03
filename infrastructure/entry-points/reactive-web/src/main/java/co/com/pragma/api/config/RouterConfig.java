package co.com.pragma.api.config;

import co.com.pragma.api.Handler;
import co.com.pragma.api.dto.LoginRequestDTO;
import co.com.pragma.api.dto.RegistrationRequestDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.model.auth.AuthToken;
import co.com.pragma.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/auth/login",
                    produces = { MediaType.APPLICATION_JSON_VALUE },
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "autenticarUsuario",
                    operation = @Operation(
                            operationId = "autenticarUsuario",
                            summary = "Autenticar un usuario",
                            description = "Verifica las credenciales de un usuario y devuelve un token JWT si son correctas.",
                            tags = { "Autenticación" },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Credenciales de acceso del usuario",
                                    content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa", content = @Content(schema = @Schema(implementation = AuthToken.class))),
                                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas (Unauthorized)")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = { MediaType.APPLICATION_JSON_VALUE },
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "registrarUsuario",
                    operation = @Operation(
                            operationId = "registrarUsuario",
                            summary = "Registrar un nuevo usuario",
                            description = "Crea un nuevo usuario",
                            tags = { "Gestión de Usuarios" },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del usuario para el registro",
                                    content = @Content(schema = @Schema(implementation = UserRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado", content = @Content(schema = @Schema(implementation = User.class))),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado (Forbidden)"),
                                    @ApiResponse(responseCode = "409", description = "El correo ya existe")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/auth/register",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "registroInicial",
                    operation = @Operation(
                            summary = "Registro inicial de un nuevo usuario",
                            tags = { "Autenticación" },
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = RegistrationRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
                                    @ApiResponse(responseCode = "409", description = "El correo electrónico ya existe")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> apiRoutes(Handler handler) {
        return route(POST("/api/v1/auth/login"), handler::autenticarUsuario)
                .and(route(POST("/api/v1/usuarios"), handler::registrarUsuario))
                .and(route(POST("/api/v1/auth/register"), handler::registroInicial));
    }
}