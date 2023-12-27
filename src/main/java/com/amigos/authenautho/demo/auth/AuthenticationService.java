package com.amigos.authenautho.demo.auth;

import com.amigos.authenautho.demo.config.JWTService;

import com.amigos.authenautho.demo.entities.Cart;
import com.amigos.authenautho.demo.entities.Customer;
import com.amigos.authenautho.demo.entities.token.Token;
import com.amigos.authenautho.demo.entities.token.TokenRepository;
import com.amigos.authenautho.demo.entities.token.TokenType;
import com.amigos.authenautho.demo.entities.user.User;

import com.amigos.authenautho.demo.entities.user.UserRepository;
import com.amigos.authenautho.demo.repositories.CartRepsitory;
import com.amigos.authenautho.demo.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final CartRepsitory cartRepsitory;

    public AuthenticationResponse register(RegisterRequest request) {

        Optional<User> userFind =userRepository.findByEmail(request.getEmail());
        if(userFind.isPresent()){
            return AuthenticationResponse.builder().success(0).failedType("Địa chỉ thư điện tử đã được sử dụng cho tài khoản khác. Vui lòng sử dụng email khác.").build();

        }
        Optional<Customer> findCus = customerRepository.findByPhoneNumber(request.getPhoneNumber());

        if(findCus.isPresent()){
            return AuthenticationResponse.builder().success(0).failedType("Số điện thoại đã được sử dụng cho tài khoản khác. Vui lòng sử dụng số điện thoại khác.").build();
        }

        Cart cart = Cart.builder().build();
        cartRepsitory.save(cart);
        var customer = Customer.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .gioHang(cart)
                .build();

        customerRepository.save(customer);


        Optional<Customer> cus = customerRepository.findByPhoneNumber(request.getPhoneNumber());
        int khId = cus.get().getId();
        var user = User.builder()
                .khId(khId)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole()).build();



        var savedUser  =  userRepository.save(user);


        var jwtToken = jwtService.generateToken(user);

        var refreshToken = jwtService.generateReFreshToken(user);

        saveUserToken(savedUser, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .success(1)
                    .refreshToken(refreshToken)
                    .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            var jwtToken = jwtService.generateToken(user);

            var refreshToken = jwtService.generateReFreshToken(user);

            revokeAllUserTokens(user);

            saveUserToken(user, jwtToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .success(1)
                    .refreshToken(refreshToken)
                    .build();
        }
        catch (ExpiredJwtException e) {
            // Handle token expiration exception
            System.out.println("Token Hết Hạn");
            return AuthenticationResponse.builder()
                    .failedType("Token has expired.")
                    .success(0)
                    .build();
        }
        catch (AuthenticationException ae){
            return AuthenticationResponse.builder().failedType("Thông tin đăng nhập không chính xác.").success(0).build();
        }
    }

    private void revokeAllUserTokens(User user){
        var validUserTokens  = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        //save token data to db
        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        //todo extract the userEmail from JWT token
        userEmail = jwtService.extractUsername(refreshToken);
        if(userEmail!=null){
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();

            System.out.println("TOKEN ? = "+jwtService.isTokenValid(refreshToken, user));

            if(jwtService.isTokenValid(refreshToken, user)){
               var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
               var authResponse = AuthenticationResponse
                       .builder()
                       .accessToken(accessToken)
                       .refreshToken(refreshToken)
                       .build();
               new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }else{
                System.out.println("Token het han o line 180/ auth service");
            }
        }
    }
}
