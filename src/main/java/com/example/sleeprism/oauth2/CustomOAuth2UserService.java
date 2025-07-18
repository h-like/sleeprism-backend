package com.example.sleeprism.oauth2;

import com.example.sleeprism.entity.User;
import com.example.sleeprism.entity.UserRole; // UserRole 임포트 (최상위 enum)
import com.example.sleeprism.entity.SocialProvider; // SocialProvider 임포트 (최상위 enum)
import com.example.sleeprism.entity.UserStatus; // UserStatus 임포트 (최상위 enum)
import com.example.sleeprism.oauth2.info.GoogleUserInfo;
import com.example.sleeprism.oauth2.info.KakaoUserInfo;
import com.example.sleeprism.oauth2.info.NaverUserInfo;
import com.example.sleeprism.oauth2.info.OAuth2UserInfo;
import com.example.sleeprism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
    String email = oAuth2UserInfo.getEmail();
    SocialProvider socialProvider = SocialProvider.valueOf(registrationId.toUpperCase());

    // [수정 1] Kakao 이메일 null 체크 강화
    if (email == null) {
      if ("kakao".equalsIgnoreCase(registrationId)) {
        log.error("Kakao login failed because email is null. Check if 'account_email' scope is requested and consented by the user.");
        // 사용자에게 더 친절한 에러 메시지를 전달하기 위해 예외 메시지 수정
        throw new OAuth2AuthenticationException("카카오 로그온에 실패했습니다. 이메일 정보 제공에 동의해주세요. (필수 동의 항목)");
      }
      throw new OAuth2AuthenticationException("Email not found from OAuth2 provider.");
    }

    User user = userRepository.findByEmail(email)
        .map(entity -> {
          log.info("Existing user found. User info: {}", entity);
          // 선택 사항: 매번 로그인 시 사용자 정보(닉네임, 프로필 사진 등)를 최신으로 업데이트 할 수 있습니다.
          // entity.updateSocialInfo(oAuth2UserInfo.getNickname(), oAuth2UserInfo.getProfileImageUrl());
          return entity;
        })
        .orElseGet(() -> {
          log.info("New user detected. Creating user for email: {}", email);
          return createUser(oAuth2UserInfo, socialProvider);
        });

    // [수정 2] saveAndFlush 사용하여 DB에 즉시 반영
    // Google 신규 회원가입 시, SuccessHandler가 트랜잭션 커밋 전에 사용자를 조회하려다 발생하는 문제를 해결합니다.
    userRepository.saveAndFlush(user);

    return new CustomOAuth2User(user, oAuth2User.getAttributes(), user.getSocialId(), user.getSocialProvider());
  }

  private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
    if ("google".equalsIgnoreCase(registrationId)) {
      return new GoogleUserInfo(attributes);
    } else if ("naver".equalsIgnoreCase(registrationId)) {
      return new NaverUserInfo(attributes);
    } else if ("kakao".equalsIgnoreCase(registrationId)) {
      return new KakaoUserInfo(attributes);
    }
    throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
  }

  private User createUser(OAuth2UserInfo oAuth2UserInfo, SocialProvider socialProvider) {
    return User.builder()
        .email(oAuth2UserInfo.getEmail())
        .nickname(oAuth2UserInfo.getNickname())
        .username(oAuth2UserInfo.getNickname()) // username 필드가 있다면 닉네임 등으로 채움
        .password("OAUTH2_TEMP_PASSWORD") // 소셜 로그인은 비밀번호가 필요 없으므로 임의의 값 설정
        .role(UserRole.USER)
        .socialId(oAuth2UserInfo.getProviderId())
        .socialProvider(socialProvider)
        .profileImageUrl(oAuth2UserInfo.getProfileImageUrl())
        .status(UserStatus.ACTIVE)
        .isDeleted(false)
        .build();
  }
}
