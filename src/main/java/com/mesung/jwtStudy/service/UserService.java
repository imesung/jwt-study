package com.mesung.jwtStudy.service;

import java.util.Collections;
import java.util.Optional;

import com.mesung.jwtStudy.dto.UserDto;
import com.mesung.jwtStudy.entity.Authority;
import com.mesung.jwtStudy.entity.User;
import com.mesung.jwtStudy.repository.UserRepository;
import com.mesung.jwtStudy.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * username이 DB에 존재하지 않으면 Authority와 User 정보를 생성해서 UserRepository의 save 메소드를 통해 DB에 정보를 저장
     * @param userDto
     * @return
     */
    @Transactional
    public User signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        //signup 메소드를 통해 가입한 회원은 ROLE_USER 권한을 가지고 있고
        //data.sql에서 자동 생성된 admin 계정은 ROLE_USER, ROLE_ADMIN을 가지고 있음
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * username을 기준으로 정보를 가져옴
     * @param username
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    /**
     * SecurityContext에 저장된 username 정보만 가져옴
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}
