package com.example.sleeprism.controller.sound;

import com.example.sleeprism.dto.sound.SoundMixDTO;
import com.example.sleeprism.entity.User;
import com.example.sleeprism.service.Sound.SoundMixService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/me/sound-mixes")
@RequiredArgsConstructor
public class SoundMixController {

  private final SoundMixService soundMixService;

  // UserDetails에서 userId를 추출하는 헬퍼 메서드
  private Long extractUserId(UserDetails userDetails) {
    if (userDetails instanceof User) {
      return ((User) userDetails).getId();
    }
    throw new IllegalStateException("User details are not of the expected type.");
  }

  @PostMapping
  public ResponseEntity<SoundMixDTO.Response> createMyMix(
      @AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody SoundMixDTO.CreateRequest requestDto
  ) {
    Long userId = extractUserId(userDetails);
    SoundMixDTO.Response response = soundMixService.createSoundMix(userId, requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<SoundMixDTO.Response>> getMyMixes(
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    Long userId = extractUserId(userDetails);
    List<SoundMixDTO.Response> response = soundMixService.getMySoundMixes(userId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{mixId}")
  public ResponseEntity<Void> deleteMyMix(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long mixId
  ) {
    Long userId = extractUserId(userDetails);
    soundMixService.deleteSoundMix(mixId, userId);
    return ResponseEntity.noContent().build();
  }
}