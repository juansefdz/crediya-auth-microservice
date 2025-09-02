    package co.com.pragma.usecase.user;

    import co.com.pragma.model.customExceptions.*;
    import co.com.pragma.model.role.gateways.RoleRepository;
    import co.com.pragma.model.user.User;
    import co.com.pragma.model.user.gateways.UserRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import reactor.core.publisher.Mono;

    import java.math.BigDecimal;

    @Slf4j
    @RequiredArgsConstructor
    public class UserUseCase {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;


        private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

        public Mono<User> execute(User userDraft, String roleId) {
            log.info("Iniciando CU crear usuario, email hash={}", userDraft.getEmail().hashCode());

            return roleRepository.findById(roleId)
                    .switchIfEmpty(Mono.error(new RoleNotFoundException(roleId)))
                    .map(userDraft::withRol)
                    .flatMap(this::validateAllRules)
                    .flatMap(userRepository::save)
                    .doOnSuccess(u -> log.info("Usuario creado ok, id={}, rol={}", u.getId(), u.getRol().getNombre()))
                    .doOnError(e -> log.error("Error creando usuario: {}", e.getMessage()));
        }

        private Mono<User> validateAllRules(User user) {
            if (user.getSalarioBase().compareTo(BigDecimal.ZERO) < 0 || user.getSalarioBase().compareTo(MAX_SALARY) > 0) {
                return Mono.error(new InvalidSalaryException(user.getSalarioBase().toPlainString()));
            }
            Mono<Boolean> emailExists = userRepository.existsByEmail(user.getEmail());
            Mono<Boolean> documentExists = userRepository.existsByDocumentoIdentidad(user.getDocumentoIdentidad());

            return Mono.zip(emailExists, documentExists)
                    .flatMap(tuple -> {
                        boolean emailTaken = tuple.getT1();
                        boolean documentTaken = tuple.getT2();

                        if (emailTaken) {
                            return Mono.error(new EmailAlreadyExistsException());
                        }
                        if (documentTaken) {
                            return Mono.error(new DocumentAlreadyExistsException(user.getDocumentoIdentidad()));
                        }
                        return Mono.just(user);
                    });
        }
    }