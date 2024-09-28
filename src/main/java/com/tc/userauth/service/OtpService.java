package com.tc.userauth.service;

import com.tc.userauth.config.OtpConfig.OtpConfigProperties;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OtpConfigProperties configProperties;

    private final RedisTemplate<String, String> redisTemplate;

    public String generateAndStoreOtp(final UUID id) {
        final var otp = generateOtp(configProperties.characters(), configProperties.length());
        final var cacheKey = getCacheKey(id);

        redisTemplate.opsForValue().set(cacheKey, otp, configProperties.ttl());

        return otp;
    }

    public boolean isOtpValid(final UUID id, final String otp) {
        final var cacheKey = getCacheKey(id);

        return Objects.equals(redisTemplate.opsForValue().get(cacheKey), otp);
    }

    public void deleteOtp(final UUID id) {
        final var cacheKey = getCacheKey(id);

        redisTemplate.delete(cacheKey);
    }

    private String getCacheKey(UUID id) {
        return configProperties.cachePrefix().formatted(id);
    }

    private String generateOtp(String characters, Integer length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(characters.length());
            otp.append(characters.charAt(index));
        }
        return otp.toString();
    }
}
