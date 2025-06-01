package com.example.sleeprism.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bookmark",
    uniqueConstraints = {
        @UniqueConstraint(name = "bookmark_uk",
            columnNames = {"user_id", "post_id"}
        )
    }
)

public class Bookmark {
  @Id
  @GeneratedValue
  @Column
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist // 엔티티가 저장되기 전에 실행
  public void onPrePersist() {
    this.createdAt = LocalDateTime.now();
  }

  @Builder
  public Bookmark(User user, Post post) {
    this.user = user;
    this.post = post;
  }
}
