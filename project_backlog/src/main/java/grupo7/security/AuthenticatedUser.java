package grupo7.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import grupo7.models.AppUser;
import grupo7.repositories.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<AppUser> get() {
        return authenticationContext.getAuthenticatedUser(AppUser.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()).get());
    }

    public void logout() {
        authenticationContext.logout();
    }

}