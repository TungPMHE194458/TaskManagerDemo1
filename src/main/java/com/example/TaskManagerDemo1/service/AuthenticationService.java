package com.example.TaskManagerDemo1.service;

import com.example.TaskManagerDemo1.dto.request.UserLoginRequest;
import com.example.TaskManagerDemo1.dto.response.ApiResponse;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.exception.AppException;
import com.example.TaskManagerDemo1.exception.ErrorCode;
import com.example.TaskManagerDemo1.repository.AuthenticationRepository;
import com.example.TaskManagerDemo1.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
public class AuthenticationService {
    @Autowired
    AuthenticationRepository authenticationRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    protected static final String SIGNER_KEY =
            "afba56f4df0a373b5b53e3ee4719ce7601436eadd7e593cf606eafc86c335e83";

    public ApiResponse<String> authenticate(UserLoginRequest request) {

        Users user = authenticationRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }

        String token = generateToken(user);
        return ApiResponse.success(token);
    }

    private String generateToken(Users user) {

        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("TungPMHE194458")
                    .issueTime(new Date())
                    .expirationTime(
                            new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli())
                    )
                    .claim("user_id", user.getID())
                    .claim("scope", buildScope(user))
                    .build();

            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);

            jwsObject.sign(new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();

        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    private String buildScope(Users user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(stringJoiner::add);
        }
        return stringJoiner.toString();
    }
}
