package com.example.serving_web_content.service.comments;

public interface comInterface {
    CommentOperationResult createComment(Long postID, String comment, String username);
    CommentOperationResult deleteComment(Long commentID, String username);
    CommentOperationResult updateComment(Long commentID, String newComment, String username);
}
