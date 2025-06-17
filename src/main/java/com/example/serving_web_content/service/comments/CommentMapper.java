package com.example.serving_web_content.service.comments;

import com.example.serving_web_content.DTO.comment.CreateCommentRequestDto;
import com.example.serving_web_content.models.Comments;
import com.example.serving_web_content.models.Post;
import com.example.serving_web_content.models.Users;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comments toEntity(CreateCommentRequestDto dto, Users user, Post post) {
        Comments comment = new Comments();
        comment.setComment(dto.getComment());
        comment.setPost(post);
        comment.setUser(user);
        return comment;
    }
}
