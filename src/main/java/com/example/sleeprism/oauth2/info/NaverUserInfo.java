package com.example.sleeprism.oauth2.info;

import java.util.Map;

public class NaverUserInfo extends OAuth2UserInfo {

  public NaverUserInfo(Map<String, Object> attributes) {
    // Naver의 경우 response라는 키 안에 실제 사용자 정보가 있습니다.
    super((Map<String, Object>) attributes.get("response"));
  }

  @Override
  public String getProviderId() {
    return (String) attributes.get("id");
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getNickname() {
    return (String) attributes.get("nickname");
  }

  @Override
  public String getProfileImageUrl() {
    return (String) attributes.get("profile_image");
  }
}

