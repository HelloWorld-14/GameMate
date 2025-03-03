package com.example.gamemate.domain.board.service;

import com.example.gamemate.domain.board.dto.BoardFindAllResponseDto;
import com.example.gamemate.domain.board.dto.BoardFindOneResponseDto;
import com.example.gamemate.domain.board.dto.BoardRequestDto;
import com.example.gamemate.domain.board.dto.BoardResponseDto;
import com.example.gamemate.domain.board.entity.Board;
import com.example.gamemate.domain.board.enums.BoardCategory;
import com.example.gamemate.domain.board.enums.ListSize;
import com.example.gamemate.domain.board.repository.BoardRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.repository.UserRepository;
import org.jboss.marshalling.ObjectTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
class BoardServiceIntegrationTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardViewService boardViewService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("viewCountRedisTemplate") // 조회수 RedisTemplate을 명시적으로 지정
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("게시글 생성 성공 - 통합 테스트")
    void createBoard_success_integration() {
        // given
        User mockUser = new User("abc123@naver", "테스트1", "테스트1","1234");
        userRepository.save(mockUser);

        BoardRequestDto requestDto = new BoardRequestDto(BoardCategory.FREE, "제목", "내용");

        // when
        BoardResponseDto responseDto = boardService.createBoard(mockUser,requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals("제목", responseDto.getTitle());

        Board savedBoard = boardRepository.findById(responseDto.getId()).orElseThrow();
        assertEquals("제목", savedBoard.getTitle());
    }

    @Test
    @DisplayName("게시글 리스트 조회 - 통합 테스트")
    void findAllBoards_success_integration() {
        // given
        User user1 = new User("abc123@naver", "테스트1", "테스트1", "1234");
        User user2 = new User("def456@naver", "테스트2", "테스트2", "1234");

        userRepository.save(user1);
        userRepository.save(user2);

        boardRepository.save(new Board(BoardCategory.FREE, "테스트 제목 1", "테스트 내용 1", user1));
        boardRepository.save(new Board(BoardCategory.FREE, "테스트 제목 2", "테스트 내용 2", user1));
        boardRepository.save(new Board(BoardCategory.INFO, "테스트 제목 3", "테스트 내용 3", user2));

        int page = 0;
        BoardCategory category = BoardCategory.FREE;
        String title = "제목";
        String content = "내용";

        Pageable pageable = PageRequest.of(page, ListSize.BOARD_LIST_SIZE.getSize(), Sort.by(Sort.Order.desc("createdAt")));

        // when
        List<BoardFindAllResponseDto> boardList = boardService.findAllBoards(page,category,title,content);

        // then
        assertThat(boardList).hasSize(2);  // 2개의 게시글이 조회되어야 함
        assertThat(boardList.get(0).getTitle()).isEqualTo("테스트 제목 2");
        assertThat(boardList.get(1).getTitle()).isEqualTo("테스트 제목 1");
    }

    @Test
    @DisplayName("게시글 단건 조회- 로그인한 사용자")
    void findBoardById_withLoginUser_success_integration() {
        // given
        User user1 = new User("abc123@naver", "테스트1", "테스트1", "1234");
        User createUser = userRepository.save(user1);
        Board savedBoard = boardRepository.save(new Board(BoardCategory.FREE, "테스트 제목 1", "테스트 내용 1", user1));
        Long boardId = savedBoard.getId();

        // when
        BoardFindOneResponseDto result = boardService.findBoardById(boardId, user1);

        // then
        // 게시글 조회 정상인지 확인
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(boardId);
        assertThat(result.getTitle()).isEqualTo("테스트 제목 1");

        // Redis 확인
        String redisKey = "board:view:" + boardId + ":" + createUser.getId();
        Object viewCount = stringRedisTemplate.opsForValue().get(redisKey);
        assertThat(viewCount).isNotNull();
        assertThat(viewCount).isEqualTo("1");
    }

    @Test
    @DisplayName("로그인한 사용자가 본인 게시글 수정")
    void updateBoard_withLoginUser_success_integration() {
        // given
        User user1 = new User("abc123@naver", "테스트1", "테스트1", "1234");
        User loginUser = userRepository.save(user1);
        Board savedBoard = boardRepository.save(new Board(BoardCategory.FREE, "테스트 제목 1", "테스트 내용 1", loginUser));
        Long boardId = savedBoard.getId();

        BoardRequestDto requestDto = new BoardRequestDto(BoardCategory.INFO, "수정 제목", "수정 내용");

        // when
        boardService.updateBoard(loginUser, boardId, requestDto);

        // then
        assertThat(savedBoard.getTitle()).isEqualTo(requestDto.getTitle());
    }

    @Test
    @DisplayName("로그인한 사용자가 본인 게시글 삭제")
    void deleteBoard_withLoginUser_success_integration() {
        // given
        User user1 = new User("abc123@naver", "테스트1", "테스트1", "1234");
        User loginUser = userRepository.save(user1);
        Board savedBoard = boardRepository.save(new Board(BoardCategory.FREE, "테스트 제목 1", "테스트 내용 1", loginUser));
        Long boardId = savedBoard.getId();

        // when
        boardService.deleteBoard(loginUser, boardId);

        // then
        // 삭제된 글이 DB에 없어야한다
        Optional<Board> deletedBoard = boardRepository.findById(boardId);
        assertThat(deletedBoard).isEmpty();

    }
}