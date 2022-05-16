package com.mesung.jwtStudy.repository;

import com.mesung.jwtStudy.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //@EntityGraph는 쿼리 수행 시 Lazy 조회가 아니고 Eager(즉시) 조회로 authorities 정보를 같이 가져옴
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
