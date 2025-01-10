package com.example.gamemate.game.dto;

import com.example.gamemate.game.entity.GamaEnrollRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameEnrollRequestResponseDto {
    private final String message;
    private Long id;
    private String title;
    private String genre;
    private String platform;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isAccepted;

    public GameEnrollRequestResponseDto(GamaEnrollRequest gameEnrollRequest ) {
        // game 객체의 필드들을 이용해 DTO의 필드들을 초기화
        this.message = "게임등록 요청이 완료되었습니다.";
        this.id = gameEnrollRequest.getId();
        this.title = gameEnrollRequest.getTitle();
        this.genre = gameEnrollRequest.getGenre();
        this.platform = gameEnrollRequest.getPlatform();
        this.description = gameEnrollRequest.getDescription();
        this.createdAt = gameEnrollRequest.getCreatedAt();
        this.modifiedAt = gameEnrollRequest.getModifiedAt();
        this.isAccepted = gameEnrollRequest.getIsAccepted();

    }
}
