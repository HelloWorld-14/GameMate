package com.example.gamemate.domain.board.entity;

import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    @DisplayName("게시글 생성 테스트")
    void createBoard() {
        // given
        User boardAuthorUser = new User("abc1234@naver", "테스트2", "테스트2","1234");
        ReflectionTestUtils.setField(boardAuthorUser, "id", 2L);

        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", boardAuthorUser);

        // when & then
        assertThat(board).isNotNull();
        assertThat(board.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("게시글 업데이트 테스트")
    void updateBoard() {
        // given
        User boardAuthorUser = new User("abc1234@naver", "테스트2", "테스트2","1234");
        ReflectionTestUtils.setField(boardAuthorUser, "id", 2L);

        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", boardAuthorUser);

        // when
        board.updateBoard(BoardCategory.INFO, "수정 제목" ,"수정 내용");

        // then
        assertThat(board.getTitle()).isEqualTo("수정 제목");
    }
}