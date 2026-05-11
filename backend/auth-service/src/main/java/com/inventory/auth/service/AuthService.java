package com.inventory.auth.service;

import com.inventory.auth.dto.AuthResponse;
import com.inventory.auth.dto.LoginRequest;
import com.inventory.auth.dto.RegisterRequest;
import com.inventory.auth.entity.User;
import com.inventory.auth.repository.UserRepository;
import com.inventory.common.exception.DuplicateResourceException;
import com.inventory.common.exception.UnauthorizedException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    // Constructor Injection (Zorunlu)
    public AuthService(UserRepository userRepository, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // BCrypt ile hashleme
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setRole("USER"); // Varsayılan rol

        User savedUser = userRepository.save(user);

        return new AuthResponse(null, savedUser.getUsername(), savedUser.getRole(), savedUser.getId());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Geçersiz kullanıcı adı veya şifre"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Geçersiz kullanıcı adı veya şifre");
        }

        // Session ID oluşturma
        String sessionId = UUID.randomUUID().toString();
        // İstenen kural: Redis'te session key formatı session:{userId}
        String sessionKey = "session:" + user.getId();

        // Redis'e kaydet (Örn. 24 saat geçerli)
        redisTemplate.opsForValue().set(sessionKey, sessionId, 24, TimeUnit.HOURS);

        return new AuthResponse(sessionId, user.getUsername(), user.getRole(), user.getId());
    }

    public void logout(Long userId) {
        String sessionKey = "session:" + userId;
        redisTemplate.delete(sessionKey);
    }
}
