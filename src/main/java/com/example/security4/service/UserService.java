package com.example.security4.service;

import com.example.security4.model.Otp;
import com.example.security4.model.User;
import com.example.security4.repository.OtpRepository;
import com.example.security4.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, OtpRepository otpRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
    }

    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * 사용자 인증하고
     * 인증이 되었다면 OTP 발급해주는 로직
     */
    public void auth(User user) {
        // DB 에서 사용자 검색
        Optional<User> userByUsername = userRepository.findUserByUsername(user.getUsername());

        // 사용자가 있으면 암호 확인
        if(userByUsername.isPresent()) {
            User userA = userByUsername.get();
            if (passwordEncoder.matches(
                    user.getPassword(),
                    userA.getPassword()
            )) {
                // 암호가 맞으면 새 OTP 생성
                renewOtp(userA);
            } else { // 암호가 틀리면 예외 발생
                throw new BadCredentialsException("BAD CREDENTIALS");
            }
        } else { // 아이디가 틀리면 예외 발생
            throw new BadCredentialsException("BAD CREDENTIALS");
        }
    }

    /**
     * 인증 된 사용자의 정보를 가져와서
     * OTP 를 발급해주는 로직
     */
    private void renewOtp(User userA) {
        // OTP 코드 생성
        String code = GenerateCodeUtil.generateCode();

        // 사용자 이름으로 OTP 검색
        Optional<Otp> userOtp = otpRepository.findOtpByUsername(userA.getUsername());

        // 이 사용자에 대한 OTP 가 있다면 값 업데이트
        if (userOtp.isPresent()) {
            Otp otp = userOtp.get();
            otp.setCode(code);
        } else {
            // 사용자에 대한 OTP 가 없다면 생성된 값으로 새 DB 생성
            Otp otp = new Otp();
            otp.setUsername(userA.getUsername());
            otp.setCode(code);
            otpRepository.save(otp);
        }
    }

    // otp 검증 메서드
    public boolean check(Otp otpToValidate) {
        Optional<Otp> userOtp = otpRepository.findOtpByUsername(otpToValidate.getUsername());

        if (userOtp.isPresent()) {
            Otp otp = userOtp.get();
            if (otpToValidate.getCode().equals(otp.getCode())) {
                return true;
            }
        }
        return false;
    }
}
