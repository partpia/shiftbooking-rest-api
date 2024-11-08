package hh.ont.shiftbooking.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import hh.ont.shiftbooking.model.User;
import hh.ont.shiftbooking.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtService.getToken(request);

        if (token != null && !jwtService.isTokenExpired(token)) {

            String username = jwtService.validateToken(token);
            User user = userDetailsService.loadUserByUsername(username);

            if (user != null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }

    // Listalla oleviin päätepisteisiin tulleet pyynnöt eivät suodatu tämän suodattimen kautta
    private static final String[] excludedEndpoints = new String[] {"/accounts/register", "/accounts/authenticate"};

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(excludedEndpoints)
            .anyMatch(e -> new AntPathMatcher().match(e, request.getRequestURI()));
    }
}
