package com.example.sleeprism.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_sound_mix") // 테이블명 지정
public class UserSoundMix {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와 다대일 관계
  @JoinColumn(name = "user_id", nullable = false) // 외래키
  private User user;

  @Column(nullable = false)
  private String mixName;

  @Column(nullable = false, columnDefinition = "TEXT") // 내용이 길 수 있으므로 TEXT 타입 지정
  private String mixData; // JSON 형태의 문자열로 저장

  @Builder
  public UserSoundMix(User user, String mixName, String mixData) {
    this.user = user;
    this.mixName = mixName;
    this.mixData = mixData;
  }
}