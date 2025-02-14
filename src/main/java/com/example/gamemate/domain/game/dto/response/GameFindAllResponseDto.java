package com.example.gamemate.domain.game.dto.response;

import com.example.gamemate.domain.game.entity.Game;
import com.example.gamemate.domain.review.entity.Review;
import lombok.Getter;

import java.util.List;

@Getter
public class GameFindAllResponseDto {
    private final Long id;
    private final String title;
    private final String genre;
    private final String platform;
    private final Long reviewCount;
    private final Double averageStar;
    private final String fileName;
    private final String imageUrl;

    public GameFindAllResponseDto(Game game) {
        // game 객체의 필드들을 이용해 DTO의 필드들을 초기화
        this.id = game.getId();
        this.title = game.getTitle();
        this.genre = game.getGenre();
        this.platform = game.getPlatform();
        this.reviewCount = (long) game.getReviews().size();
        this.averageStar = calculateAverageStar(game.getReviews());
        this.fileName = game.getImages().isEmpty() ? null :
                game.getImages().get(0).getFileName();
        this.imageUrl = game.getImages().isEmpty() ? null :
                game.getImages().get(0).getFilePath();
    }

    private Double calculateAverageStar(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double average = reviews.stream()
                .mapToInt(Review::getStar)
                .average()
                .orElse(0.0);

        // 소수점 둘째 자리에서 반올림
        return Math.round(average * 10.0) / 10.0;
    }
}
