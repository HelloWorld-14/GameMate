package com.example.gamemate.domain.board.service;

import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindOneResponseDto;
import com.example.gamemate.domain.board.dto.BoardRequestDto;
import com.example.gamemate.domain.board.dto.BoardResponseDto;
import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.entity.QBoard;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.enums.ListSize;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import io.swagger.v3.oas.annotations.media.DependentSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardViewService boardViewService;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("게시글 생성 성공")
    void createBoard_success() {
        // Given
        User mockUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        BoardRequestDto requestDto = new BoardRequestDto(BoardCategory.FREE, "제목", "내용");
        Board mockBoard = new Board(BoardCategory.FREE,"제목","내용", mockUser);

        when(boardRepository.save(any(Board.class))).thenReturn(mockBoard);

        //when
        BoardResponseDto responseDto = boardService.createBoard(mockUser, requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(BoardCategory.FREE, responseDto.getCategory());
        assertEquals("제목", responseDto.getTitle());
        assertEquals("내용", responseDto.getContent());
        verify(boardRepository, times(1)).save(any(Board.class));
    }


    @Test
    @DisplayName("게시글 리스트 조회 성공")
    void findAllBoards_success() {
        // given
        int page = 0;
        BoardCategory category = BoardCategory.FREE;
        String title = "제목";
        String content = "내용";

        Board board1 = new Board(BoardCategory.FREE,"테스트 제목1","테스트 내용 1",null);
        Board board2 = new Board(BoardCategory.FREE,"테스트 제목2","테스트 내용 2",null);

        Pageable pageable = PageRequest.of(page, ListSize.BOARD_LIST_SIZE.getSize(), Sort.by(Sort.Order.desc("createdAt")));
        Page<Board> boardPage = new PageImpl<>(List.of(board2, board1), pageable, 2);

        when(boardRepository.searchBoardQuerydsl(category, title, content, pageable)).thenReturn(boardPage).thenReturn(boardPage);
        when(boardViewService.getViewCount(board1.getId())).thenReturn(100);
        when(boardViewService.getViewCount(board2.getId())).thenReturn(50);

        // when
        List<BoardFindAllResponseDto> result = boardService.findAllBoards(page, category, title, content);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("테스트 제목2");
        assertThat(result.get(0).getViews()).isEqualTo(50);
        assertThat(result.get(1).getTitle()).isEqualTo("테스트 제목1");

    }

    @Test
    @DisplayName("로그인한 사용자가 게시글을 조회하면 조회수가 증가하고 DTO가 반환됨")
    void findBoardById_withLoginUser_success() {
        // given
        Long boardId = 1L;
        User loginUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", loginUser);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when
        BoardFindOneResponseDto resultDto = boardService.findBoardById(boardId, loginUser);

        // then
        verify(boardViewService).increaseViewCount(boardId, loginUser.getId());
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getCategory()).isEqualTo(BoardCategory.FREE);
        assertThat(resultDto.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("비로그인 사용자가 게시글을 조회하면 조회수가 증가하고 DTO가 반환됨")
    void findBoardById_withoutLoginUser_success() {
        // given
        Long boardId = 1L;
        User loginUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", loginUser);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when
        BoardFindOneResponseDto resultDto = boardService.findBoardById(boardId, null);

        // then
        verify(boardViewService).increaseViewCount(boardId, null);
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getCategory()).isEqualTo(BoardCategory.FREE);
        assertThat(resultDto.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 조회하면 예외 발생")
    void findBoardById_boardNotFound_throwsException() {
        // given
        Long boardId = 1L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.findBoardById(boardId, null))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.BOARD_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인한 사용자가 본인의 게시글 수정")
    void updateBoard_withLoginUser_success() {
        // given
        Long boardId = 1L;
        User loginUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        ReflectionTestUtils.setField(loginUser, "id", 1L);
        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", loginUser);

        BoardRequestDto requestDto = new BoardRequestDto(BoardCategory.INFO, "수정 제목", "수정 내용");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when
        boardService.updateBoard(loginUser, boardId, requestDto);

        // then
        assertThat(board.getCategory()).isEqualTo(BoardCategory.INFO);
        assertThat(board.getTitle()).isEqualTo("수정 제목");
        assertThat(board.getContent()).isEqualTo("수정 내용");
        verify(boardRepository, times(1)).save(any(Board.class));

    }

    @Test
    @DisplayName("다른 사용자의 게시글을 수정하려하면 예외발생")
    void updateBoard_forbidden_throwsException() {
        // given
        Long boardId = 1L;
        User loginUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        ReflectionTestUtils.setField(loginUser, "id", 1L);

        User boardAuthorUser = new User("abc1234@naver", "테스트2", "테스트2","1234");
        ReflectionTestUtils.setField(boardAuthorUser, "id", 2L);
        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", boardAuthorUser);

        BoardRequestDto requestDto = new BoardRequestDto(BoardCategory.INFO, "수정 제목", "수정 내용");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when & then
        assertThatThrownBy(() -> boardService.updateBoard(loginUser, boardId, requestDto))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getMessage());

    }

    @Test
    @DisplayName("로그인한 사용자가 본인의 게시글 삭제")
    void deleteBoard_withLoginUser_success() {
        // given
        Long boardId = 1L;
        User loginUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        ReflectionTestUtils.setField(loginUser, "id", 1L);
        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", loginUser);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when
        boardService.deleteBoard(loginUser, boardId);

        // then
        verify(boardRepository, times(1)).delete(board);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 시 예외 발생")
    void deleteBoard_boardNotFound_throwsException() {
        // given
        Long boardId = 1L;
        User loginUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        ReflectionTestUtils.setField(loginUser, "id", 1L);

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.deleteBoard(loginUser, boardId))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.BOARD_NOT_FOUND.getMessage());
        verify(boardRepository, never()).delete(any(Board.class));
    }

    @Test
    @DisplayName("다른 사용자의 게시글 삭제 시 예외 발생")
    void deleteBoard_forbidden_throwsException() {
        // given
        Long boardId = 1L;
        User loginUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        ReflectionTestUtils.setField(loginUser, "id", 1L);

        User boardAuthorUser = new User("abc1234@naver", "테스트2", "테스트2","1234");
        ReflectionTestUtils.setField(boardAuthorUser, "id", 2L);

        Board board = new Board(BoardCategory.FREE, "테스트 제목", "테스트 내용", boardAuthorUser);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // when & then
        assertThatThrownBy(() -> boardService.deleteBoard(loginUser, boardId))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.FORBIDDEN.getMessage());
        verify(boardRepository, never()).delete(any(Board.class));
    }
}