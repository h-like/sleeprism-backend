package com.example.sleeprism.service.Sound;

import com.example.sleeprism.dto.sound.SoundMixDTO;
import com.example.sleeprism.entity.User;
import com.example.sleeprism.entity.UserSoundMix;
import com.example.sleeprism.repository.UserRepository;
import com.example.sleeprism.repository.UserSoundMixRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoundMixService {

  private final UserSoundMixRepository soundMixRepository;
  private final UserRepository userRepository;

  @Transactional
  public SoundMixDTO.Response createSoundMix(Long userId, SoundMixDTO.CreateRequest requestDto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

    UserSoundMix newMix = UserSoundMix.builder()
        .user(user)
        .mixName(requestDto.getMixName())
        .mixData(requestDto.getMixData())
        .build();

    UserSoundMix savedMix = soundMixRepository.save(newMix);
    return new SoundMixDTO.Response(savedMix);
  }

  public List<SoundMixDTO.Response> getMySoundMixes(Long userId) {
    return soundMixRepository.findByUserId(userId).stream()
        .map(SoundMixDTO.Response::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public void deleteSoundMix(Long mixId, Long userId) {
    UserSoundMix mix = soundMixRepository.findById(mixId)
        .orElseThrow(() -> new EntityNotFoundException("SoundMix not found with ID: " + mixId));

    // 본인만 삭제 가능하도록 권한 확인
    if (!mix.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("You do not have permission to delete this mix.");
    }
    soundMixRepository.delete(mix);
  }
}
