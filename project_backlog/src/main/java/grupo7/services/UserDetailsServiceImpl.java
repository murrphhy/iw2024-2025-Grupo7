package grupo7.services;

import grupo7.models.*;
import grupo7.repositories.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        if (user instanceof Technician) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TECHNICIAN"));
        } else if (user instanceof Promoter) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PROMOTER"));
        } else if (user instanceof Cio) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CIO"));
        } else if (user instanceof Applicant) {
            authorities.add(new SimpleGrantedAuthority("ROLE_APPLICANT"));
        } else if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
