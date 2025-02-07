package com.example.gamemate.domain.game.entity;

import com.example.gamemate.domain.review.entity.Review;
import com.example.gamemate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "game")
@NoArgsConstructor
public class Game extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255 ,unique = false)
    private String title;

    @Column(name = "genre", nullable = false, length = 10)
    private String genre;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "platform", nullable = false, length = 20)
    private String platform;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    public Game(String title, String genre, String platform, String description) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;
    }

    public void updateGame(String title, String genre, String platform, String description){
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.description = description;
    }

    public void addImage(GameImage gameImage) {
        this.images.add(gameImage);
    }

    public void removeGameImage(GameImage gameImage) {
        this.images.remove(gameImage);
    }

    public void clearImages() {
        this.images.clear();
    }



}
