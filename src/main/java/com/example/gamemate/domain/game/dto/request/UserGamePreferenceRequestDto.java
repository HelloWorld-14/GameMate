package com.example.gamemate.domain.game.dto.request;

import com.example.gamemate.domain.user.entity.User;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserGamePreferenceRequestDto {

    private User user;

    private String preferredGenres;

    private String playStyle;

    private String playTime;

    private String difficulty;

    private String platform;

    @Size(max = 500, message = "추가 요청은 500자 이내로 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9\\s가-힣.,!?]*$",
            message = "특수 문자는 일부만 허용됩니다 (.,!?)")
    private String extraRequest;

}
