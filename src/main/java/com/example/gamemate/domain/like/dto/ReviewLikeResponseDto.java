package com.example.gamemate.domain.like.dto;

import com.example.gamemate.domain.like.entity.ReviewLike;
import lombok.Getter;

@Getter
public class ReviewLikeResponseDto {
    private Long reviewId;
    private Long userId;
    private Integer status;


    public ReviewLikeResponseDto(ReviewLike reviewLike){
        this.reviewId = reviewLike.getReview().getId();
        this.status = reviewLike.getStatus();
        this.userId = reviewLike.getUser().getId();
    }

}
