// src/main/java/com/example/sleeprism/oauth2/OAuth2AuthenticationSuccessHandler.java
package com.example.sleeprism.oauth2;

import com.example.sleeprism.entity.User;
import com.example.sleeprism.jwt.JwtTokenProvider;
import com.example.sleeprism.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository; // UserRepository 주입

  @Value("${oauth2.redirect.front-url}")
  private String frontRedirectUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = extractEmailFromOAuth2User(oAuth2User);

    if (email == null) {
      log.error("Could not find email from OAuth2 principal. Authentication: {}", authentication);
      getRedirectStrategy().sendRedirect(request, response, createErrorRedirectUrl("이메일 정보를 가져올 수 없습니다."));
      return;
    }

    // --- 핵심 로직 수정 ---
    // 1. 이메일을 기반으로 데이터베이스에서 사용자 정보를 조회합니다.
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("소셜 로그인 사용자를 DB에서 찾을 수 없습니다: " + email));

    // 2. 조회된 사용자 정보(ID, 이메일, 닉네임, 권한)로 JWT 토큰을 생성합니다.
    String accessToken = jwtTokenProvider.generateToken(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getAuthorities()
    );
    log.info("Generated JWT Token for user {}: {}", user.getEmail(), accessToken);

    // 3. 생성된 토큰을 URL 파라미터에 담아 프론트엔드로 리디렉션합니다.
    String targetUrl = createRedirectUrlWithToken(accessToken);

    if (response.isCommitted()) {
      log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  // OAuth2User 객체에서 이메일을 추출하는 로직 분리
  private String extractEmailFromOAuth2User(OAuth2User oAuth2User) {
    if (oAuth2User instanceof DefaultOidcUser) {
      // Google (OIDC) 로그인인 경우
      DefaultOidcUser oidcUser = (DefaultOidcUser) oAuth2User;
      log.info("OIDC user detected. Attributes: {}", oidcUser.getAttributes());
      return oidcUser.getAttribute("email");
    } else if (oAuth2User instanceof CustomOAuth2User) {
      // Naver, Kakao 등 일반 OAuth2 로그인인 경우
      CustomOAuth2User customUser = (CustomOAuth2User) oAuth2User;
      log.info("CustomOAuth2User detected. User: {}", customUser.getEmail());
      return customUser.getEmail();
    }
    log.warn("Unrecognized OAuth2User type: {}. Trying to get 'email' attribute.", oAuth2User.getClass().getName());
    return oAuth2User.getAttribute("email");
  }

  private String createRedirectUrlWithToken(String token) {
    return UriComponentsBuilder.fromUriString(frontRedirectUrl)
        .queryParam("token", token)
        .build()
        .toUriString();
  }

  private String createErrorRedirectUrl(String error) {
    return UriComponentsBuilder.fromUriString(frontRedirectUrl)
        .queryParam("error", error)
        .build()
        .encode(StandardCharsets.UTF_8)
        .toUriString();
  }
}