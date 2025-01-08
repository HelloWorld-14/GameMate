package com.example.gamemate.review.dto;

import com.example.gamemate.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewUpdateResponseDto {
    private  Long id;
    private  String content;
    private  Integer star;
    private  Long gameId;
    private  Long userId;
    private  LocalDateTime modifiedAt;

    public ReviewUpdateResponseDto(Review review) {
        this.id = review.getId();
        this.content = review.getContent();
        this.star = review.getStar();
        this.gameId = review.getGame().getId();
        this.userId = review.getUserId();
        this.modifiedAt = review.getModifiedAt();
    }
}
