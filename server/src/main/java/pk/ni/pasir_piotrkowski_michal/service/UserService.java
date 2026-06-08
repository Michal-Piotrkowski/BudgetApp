package pk.ni.pasir_piotrkowski_michal.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pk.ni.pasir_piotrkowski_michal.dto.LoginDto;
import pk.ni.pasir_piotrkowski_michal.dto.UserDto;
import pk.ni.pasir_piotrkowski_michal.exception.UserAlreadyExistsException;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.repository.UserRepository;
import pk.ni.pasir_piotrkowski_michal.security.JwtUtil;

import java.util.ArrayList;
import java.util.Locale;

@RequiredArgsConstructor
@Service
@NullMarked
public class UserService implements UserDetailsService {
    private  final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public User register(UserDto dto){
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("Użytkownik z podanym adresem e-mail jest już w bazie!");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public String login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika!"));
        if(!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Nieprawidłowe dane logowania!");
        }
        return jwtUtil.generateToken(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
