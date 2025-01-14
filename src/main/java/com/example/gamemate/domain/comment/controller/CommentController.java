package com.example.gamemate.domain.comment.controller;

import com.example.gamemate.domain.comment.dto.CommentRequestDto;
import com.example.gamemate.domain.comment.dto.CommentResponseDto;
import com.example.gamemate.domain.comment.service.CommentService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 API
     * @param boardId
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        CommentResponseDto dto = commentService.createComment(customUserDetails.getUser(),boardId, requestDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * 댓글 수정 API
     * @param id
     * @param requestDto
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        commentService.updateComment(customUserDetails.getUser(), id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        commentService.deleteComment(customUserDetails.getUser(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
