# CrediYa – Plataforma de Solicitudes de Préstamos

CrediYa es una plataforma que busca digitalizar y optimizar la gestión de solicitudes de préstamos personales, eliminando procesos manuales y presenciales.
El sistema permite que los solicitantes ingresen sus datos y la información del préstamo que desean, los evalúa automáticamente y notifica a los administradores para la aprobación final.

## Funcionalidades principales

- Gestión de tipos de préstamos: Crear, editar y eliminar productos de crédito.

- Proceso de solicitud: Envío de datos de préstamo por solicitantes.

- Capacidad de endeudamiento: Evaluación automática mediante reglas de negocio.

- Gestión de usuarios y roles: Solicitantes y administradores con permisos diferenciados.

- Notificaciones automáticas: Estado del crédito vía correo/SMS.

- Reportes de rendimiento: Métricas del negocio (préstamos aprobados, rechazados, montos).

## Arquitectura

La solución está construida bajo un modelo de microservicios reactivos con Spring WebFlux y arquitectura hexagonal.

### Microservicios principales:

- auth-service → autenticación y gestión de usuarios.

- loan-service → gestión de solicitudes y productos de crédito.

- scoring-service → evaluación de capacidad de endeudamiento.

- notification-service → envío de notificaciones.

- reporting-service → reportes de rendimiento.

## Tecnologías:

- Backend: Java 17, Spring Boot, Spring WebFlux.

- DB Relacional (RDS - PostgreSQL): Solicitudes, usuarios y préstamos.

- DB No Relacional (DynamoDB): Reportes y métricas.

- Mensajería (SQS): Comunicación asíncrona entre servicios.

- Infraestructura (AWS): ECS Fargate, API Gateway, Lambda, CloudWatch, Secret Manager.

## Estructura del proyecto

Cada microservicio se encuentra en un repositorio independiente siguiendo el scaffold de Clean Architecture Bancolombia.

/crediya
   ├── auth-service
   ├── loan-service
   ├── scoring-service
   ├── notification-service
   ├── reporting-service
   └── docs


Dentro de cada microservicio:

/src
   ├── main/java/com/crediya
   │     ├── application   -> Casos de uso
   │     ├── domain        -> Entidades del dominio
   │     ├── infrastructure-> Adapters (DB, SQS, REST)
   │     └── entrypoints   -> Controladores WebFlux
   └── test/java/com/crediya 

## Instalación y ejecución local

- Clonar el repositorio correspondiente:

        git clone https://github.com/crediya/loan-service.git
        cd loan-service


- Compilar y correr con Gradle:

        ./gradlew bootRun


La API estará disponible en:

    http://localhost:8080


Documentación de endpoints:

`http://localhost:8080/swagger-ui.html`

## Testing

Ejecutar pruebas unitarias:

    ./gradlew test
