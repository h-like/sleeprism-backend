package com.example.sleeprism.repository;

import com.example.sleeprism.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  // JpaRepository<엔티티 타입, 엔티티의 ID 타입>

  // email로 사용자 조회 (CustomUserDetailsService에서 사용)
  Optional<User> findByEmail(String email);

  // nickname으로 사용자 조회
  Optional<User> findByNickname(String nickname);

  // socialProvider와 socialId로 사용자 조회 (소셜 로그인)
  Optional<User> findBySocialProviderAndSocialId(String socialProvider, String socialId);

  // 특정 필드의 존재 여부 확인 (중복 체크 등에 활용)
  boolean existsByEmail(String email);
  boolean existsByNickname(String nickname);
}
