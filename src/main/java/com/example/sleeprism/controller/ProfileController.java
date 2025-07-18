package com.example.sleeprism.controller;

import com.example.sleeprism.dto.PostResponseDTO;
import com.example.sleeprism.entity.User;
import com.example.sleeprism.service.PostService;
import com.example.sleeprism.service.BookmarkService;
import com.example.sleeprism.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 현재 로그인한 사용자의 정보("나의 정보")와 관련된 API를 처리하는 컨트롤러
 * 경로: /api/me/*
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class ProfileController {

  private final PostService postService;
  private final PostLikeService postLikeService;
  private final BookmarkService bookmarkService;

  // UserDetails에서 userId를 추출하는 헬퍼 메서드
  private Long extractUserIdFromUserDetails(UserDetails userDetails) {
    if (userDetails instanceof User) {
      return ((User) userDetails).getId();
    }
    throw new IllegalArgumentException("사용자 정보를 가져올 수 없습니다. UserDetails 구현을 확인하세요.");
  }

  /**
   * 현재 로그인한 사용자가 작성한 모든 게시글을 조회합니다.
   * @param userDetails Spring Security가 주입해주는 현재 사용자 정보
   * @return 게시글 목록
   */
  @GetMapping("/posts")
  public ResponseEntity<List<PostResponseDTO>> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = extractUserIdFromUserDetails(userDetails);
    // TODO: PostService에 userId로 게시글을 조회하는 메서드 추가 필요
    List<PostResponseDTO> myPosts = postService.getPostsByAuthor(userId);
    return ResponseEntity.ok(myPosts);
  }

  /**
   * 현재 로그인한 사용자가 좋아요를 누른 모든 게시글을 조회합니다.
   * @param userDetails Spring Security가 주입해주는 현재 사용자 정보
   * @return 게시글 목록
   */
  @GetMapping("/liked-posts")
  public ResponseEntity<List<PostResponseDTO>> getLikedPosts(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = extractUserIdFromUserDetails(userDetails);
    // TODO: PostLikeService에 userId로 좋아요 누른 게시글을 조회하는 메서드 추가 필요
    List<PostResponseDTO> likedPosts = postLikeService.getLikedPostsByUser(userId);
    return ResponseEntity.ok(likedPosts);
  }

  /**
   * 현재 로그인한 사용자가 북마크한 모든 게시글을 조회합니다.
   * @param userDetails Spring Security가 주입해주는 현재 사용자 정보
   * @return 게시글 목록
   */
  @GetMapping("/bookmarked-posts")
  public ResponseEntity<List<PostResponseDTO>> getBookmarkedPosts(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = extractUserIdFromUserDetails(userDetails);
    // TODO: BookmarkService에 userId로 북마크한 게시글을 조회하는 메서드 추가 필요
    List<PostResponseDTO> bookmarkedPosts = bookmarkService.getBookmarkedPostsByUser(userId);
    return ResponseEntity.ok(bookmarkedPosts);
  }

  // TODO: 필요하다면 /api/me/notifications, /api/me/chat-rooms 등 "나의 활동" 관련 API 추가
}
