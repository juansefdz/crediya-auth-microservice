package co.com.pragma.api.config;

import co.com.pragma.api.Handler;
import co.com.pragma.api.dto.UserRequestDTO;
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
    @RouterOperations(@RouterOperation(
            path = "/api/v1/usuarios/{roleId}",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "registrarUsuario",
            operation = @Operation(
                    operationId = "registrarUsuario",
                    summary = "Registrar un nuevo usuario",
                    description = "Crea un nuevo usuario en el sistema y retorna sus datos.",
                    tags = { "Gestión de Usuarios" }
                    ,
                    requestBody = @RequestBody(
                            required = true,
                            description = "Datos del usuario para el registro",
                            content = @Content(schema = @Schema(implementation = UserRequestDTO.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(schema = @Schema(implementation = User.class))),
                            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (Bad Request)"),
                            @ApiResponse(responseCode = "409", description = "El dato ingresado ya existe"),
                            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                    }
            )
    ))
    public RouterFunction<ServerResponse> registrarUsuarioRoute(Handler handler) {
        return route(POST("/api/v1/usuarios/{roleId}"), handler::registrarUsuario);
    }
}