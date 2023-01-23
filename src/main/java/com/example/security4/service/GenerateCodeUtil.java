package com.example.security4.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * OTP 코드를
 * 생성하는 로직
 */
public class GenerateCodeUtil {

    public GenerateCodeUtil() {

    }

    public static String generateCode() {
        String code;

        try {
            SecureRandom random = SecureRandom.getInstanceStrong();

            int c = random.nextInt(9000) + 1000;

            code = String.valueOf(c);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Problem when generating the random code");
        }
        return code;
    }
}
