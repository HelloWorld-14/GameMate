package com.example.gamemate.global.eventListener;

import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.follow.entity.Follow;
import com.example.gamemate.domain.like.entity.BoardLike;
import com.example.gamemate.domain.like.entity.ReviewLike;
import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.service.NotificationService;
import com.example.gamemate.domain.reply.entity.Reply;
import com.example.gamemate.global.eventListener.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleCreateFollow(FollowCreatedEvent event) {
        log.info("새로운 팔로우 알림 전송 시작");
        Follow follow = event.getFollow();

        Notification notification = notificationService.createNotification(follow.getFollowee(), NotificationType.NEW_FOLLOWER, "/users/" + follow.getFollower().getId());
        notificationService.sendNotification(follow.getFollowee(), notification);

        log.info("새로운 팔로우 알림 전송 완료");
    }

    @Async
    @EventListener
    public void handleCreateMatch(MatchCreatedEvent event) {
        log.info("새로운 매칭 알림 전송 시작");
        Match match = event.getMatch();

        Notification notification = notificationService.createNotification(match.getReceiver(), NotificationType.NEW_MATCH, "/matches/" + match.getId());
        notificationService.sendNotification(match.getReceiver(), notification);

        log.info("새로운 매칭 알림 전송 완료");
    }

    @Async
    @EventListener
    public void handleAcceptMatch(MatchAcceptedEvent event) {
        log.info("매칭 수락 알림 전송 시작");
        Match match = event.getMatch();

        Notification notification = notificationService.createNotification(match.getSender(), NotificationType.MATCH_ACCEPTED, "/matches/" + match.getId());
        notificationService.sendNotification(match.getSender(), notification);

        log.info("매칭 수락 알림 전송 완료");
    }

    @Async
    @EventListener
    public void handleRejectMatch(MatchRejectedEvent event) {
        log.info("매칭 거절 알림 전송 시작");
        Match match = event.getMatch();

        Notification notification = notificationService.createNotification(match.getSender(), NotificationType.MATCH_REJECTED, "/matches/" + match.getId());
        notificationService.sendNotification(match.getSender(), notification);

        log.info("매칭 거절 알림 전송 완료");
    }

    @Async
    @EventListener
    public void handleCreateBoardLike(BoardLikeCreatedEvent event) {
        log.info("게시글 새로운 좋아요 알림 전송 시작");
        BoardLike boardLike = event.getBoardLike();

        Notification notification = notificationService.createNotification(boardLike.getBoard().getUser(), NotificationType.NEW_LIKE, "/boards/" + boardLike.getBoard().getBoardId());
        notificationService.sendNotification(boardLike.getBoard().getUser(), notification);

        log.info("게시글 새로운 좋아요 알림 전송 완료");
    }

    @Async
    @EventListener
    public void handleCreateReviewLike(ReviewLikeCreatedEvent event) {
        log.info("리뷰 새로운 좋아요 알림 전송 시작");
        ReviewLike reviewLike = event.getReviewLike();

        Notification notification = notificationService.createNotification(reviewLike.getReview().getUser(), NotificationType.NEW_LIKE, "/reviews/" + reviewLike.getReview().getId());
        notificationService.sendNotification(reviewLike.getReview().getUser(), notification);

        log.info("리뷰 새로운 좋아요 알림 전송 완료");
    }

    @Async
    @EventListener
    public void handleCreateComment(CommentCreatedEvent event) {
        log.info("새로운 댓글 알림 전송 시작");
        Comment comment = event.getComment();

        Notification notification = notificationService.createNotification(comment.getBoard().getUser(), NotificationType.NEW_COMMENT, "/comments/" + comment.getCommentId());
        notificationService.sendNotification(comment.getBoard().getUser(), notification);

        log.info("새로운 댓글 알림 전송 완료");
    }

    @Async
    @EventListener
    public void handleCreateReply(ReplyCreatedEvent event) {
        log.info("새로운 대댓글 알림 전송 시작");
        Reply reply = event.getReply();

        Notification boardNotification = notificationService.createNotification(reply.getComment().getBoard().getUser(), NotificationType.NEW_COMMENT, "/replies/" + reply.getReplyId());
        notificationService.sendNotification(reply.getComment().getBoard().getUser(), boardNotification);
        Notification commentNotification = notificationService.createNotification(reply.getComment().getUser(), NotificationType.NEW_COMMENT, "/replies/" + reply.getReplyId());
        notificationService.sendNotification(reply.getComment().getUser(), commentNotification);

        if (reply.getParentReply() != null) {
            Notification parentReplyNotification = notificationService.createNotification(reply.getParentReply().getUser(), NotificationType.NEW_COMMENT, "/replies/" + reply.getReplyId());
            notificationService.sendNotification(reply.getParentReply().getUser(), parentReplyNotification);
        }

        log.info("새로운 대댓글 알림 전송 완료");
    }
}
