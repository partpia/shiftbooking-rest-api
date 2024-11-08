package hh.ont.shiftbooking.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import hh.ont.shiftbooking.dto.CredentialsDto;
import hh.ont.shiftbooking.exception.DatabaseException;
import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public String authenticate(CredentialsDto dto) throws Exception {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() 
            -> new DatabaseException("Virheelliset käyttäjätiedot."));
        String token = jwtService.createToken(user);

        return token;
    }
}
