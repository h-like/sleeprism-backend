package com.example.sleeprism.dto.sound;

import com.example.sleeprism.entity.UserSoundMix;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SoundMixDTO {
  @Getter
  @NoArgsConstructor
  public static class CreateRequest {
    @NotBlank(message = "믹스 이름은 필수입니다.")
    private String mixName;

    @NotBlank(message = "믹스 데이터는 필수입니다.")
    private String mixData; // JSON 문자열
  }

  @Getter
  public static class Response {
    private final Long id;
    private final String mixName;
    private final String mixData;

    public Response(UserSoundMix entity) {
      this.id = entity.getId();
      this.mixName = entity.getMixName();
      this.mixData = entity.getMixData();
    }
  }
}
