package com.example.sleeprism.repository;

import com.example.sleeprism.entity.UserSoundMix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSoundMixRepository extends JpaRepository<UserSoundMix, Long> {

  // 특정 사용자가 생성한 모든 사운드 믹스를 조회하는 메소드
  List<UserSoundMix> findByUserId(Long userId);
}
