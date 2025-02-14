package com.example.gamemate.domain.like.repository;

import com.example.gamemate.domain.like.entity.BoardLike;
import com.example.gamemate.domain.like.enums.LikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    @Query("SELECT bl FROM BoardLike bl WHERE bl.board.id = :id AND bl.user.id = :userId")
    Optional<BoardLike> findByBoardIdAndUserId(@Param("id") Long boardId, @Param("userId") Long userId);

    Long countByBoardIdAndStatus(Long boardId, LikeStatus status);


}
