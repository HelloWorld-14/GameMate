package com.example.gamemate.review.controller;

import com.example.gamemate.review.dto.ReviewCreateRequestDto;
import com.example.gamemate.review.dto.ReviewCreateResponseDto;
import com.example.gamemate.review.dto.ReviewUpdateRequestDto;
import com.example.gamemate.review.dto.ReviewUpdateResponseDto;
import com.example.gamemate.review.entity.Review;
import com.example.gamemate.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/{gameId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 리뷰등록
     *
     * @param gameId
     * @param requestDto
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReviewCreateResponseDto> createReview(@PathVariable Long gameId, @RequestBody ReviewCreateRequestDto requestDto) {

        ReviewCreateResponseDto responseDto = reviewService.createReview(gameId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 리뷰 수정
     *
     * @param gameId
     * @param id
     * @param requestDto
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(@PathVariable Long gameId, @PathVariable Long id, @RequestBody ReviewUpdateRequestDto requestDto) {

        ReviewUpdateResponseDto responseDto = reviewService.updateReview(gameId, id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 리뷰 삭제
     *
     * @param gameId
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long gameId, @PathVariable Long id) {

        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}
