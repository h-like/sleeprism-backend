package com.example.sleeprism.oauth2.info;

import java.util.Map;

public class KakaoUserInfo extends OAuth2UserInfo {

  public KakaoUserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getProviderId() {
    // Kakao는 id를 Long 타입으로 반환하므로 String으로 변환해줍니다.
    return String.valueOf(attributes.get("id"));
  }

  @Override
  public String getEmail() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    if (kakaoAccount == null) {
      return null;
    }
    return (String) kakaoAccount.get("email");
  }

  @Override
  public String getNickname() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    if (kakaoAccount == null) {
      return null;
    }
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    if (profile == null) {
      return null;
    }
    return (String) profile.get("nickname");
  }

  @Override
  public String getProfileImageUrl() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    if (kakaoAccount == null) {
      return null;
    }
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    if (profile == null) {
      return null;
    }
    return (String) profile.get("profile_image_url");
  }
}