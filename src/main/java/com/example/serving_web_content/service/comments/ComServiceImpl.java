package com.example.serving_web_content.service.comments;
import com.example.serving_web_content.DTO.comment.CreateCommentRequestDto;
import com.example.serving_web_content.DTO.comment.UpdateCommentRequestDTO;
import com.example.serving_web_content.DTO.user.ChangeDetailsFormData;
import com.example.serving_web_content.Repo.CityRepository;
import com.example.serving_web_content.Repo.ComRepository;
import com.example.serving_web_content.Repo.PostRepository;
import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.DTO.user.ChangeUserDetailsDto;
import com.example.serving_web_content.models.Comments;
import com.example.serving_web_content.models.Post;
import com.example.serving_web_content.models.Users;
import com.example.serving_web_content.service.comments.CommentOperationResult.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class ComServiceImpl implements comInterface {
    private final CommentMapper commentMapper;
    private final ComRepository comRepository;
    private final UsersRepository usersRepository;
    private final PostRepository postRepository;
    private final CityRepository cityRepository;

    @Autowired
    public ComServiceImpl(CommentMapper commentMapper, ComRepository comRepository, UsersRepository usersRepository, PostRepository postRepository, CityRepository cityRepository) {
        this.commentMapper = commentMapper;
        this.comRepository = comRepository;
        this.usersRepository = usersRepository;
        this.postRepository = postRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public CommentOperationResult createComment(Long postID, String commentText, String username) {
        if (postID == null) {
            return CommentOperationResult.failure(Status.INVALID_DATA, null, "Post ID is required");
        }
        if (!StringUtils.hasText(commentText)) {
            return CommentOperationResult.failure(Status.INVALID_DATA, postID, "Comment text cannot be empty");
        }
        commentText = commentText.trim();

        Post post = postRepository.findById(postID).orElse(null);
        if (post == null) {
            return CommentOperationResult.failure(Status.NOT_FOUND, postID, "Post not found with ID: " + postID);
        }
        Users user = usersRepository.findByUsername(username);
        if (user == null) {
            return CommentOperationResult.failure(Status.NOT_FOUND, postID, "Authenticated user not found: " + username);
        }
        CreateCommentRequestDto dto = new CreateCommentRequestDto();
        dto.setPostID(postID);
        dto.setComment(commentText);

        Comments comment = commentMapper.toEntity(dto,user,post);
        Comments savedComment = comRepository.save(comment);
        return CommentOperationResult.success(savedComment.getPost().getId());
    }

    @Override
    @Transactional
    public CommentOperationResult deleteComment(Long commentID, String username) {
        if (commentID == null) {
            return CommentOperationResult.failure(Status.INVALID_DATA, null, "Comment ID is required");
        }

        Comments commentsToDelete = comRepository.findById(commentID).orElse(null);
        if (commentsToDelete == null) {
            return CommentOperationResult.failure(Status.NOT_FOUND, null, "Comment not found with ID: " + commentID);
        }



        Long postIDForRedirect = commentsToDelete.getPost().getId();
        comRepository.delete(commentsToDelete);
        return CommentOperationResult.success(postIDForRedirect);
    }

    @Override
    @Transactional
    public CommentOperationResult updateComment(Long commentID, String newCommentText, String username) {
        if (!StringUtils.hasText(newCommentText)) {
            return CommentOperationResult.failure(Status.INVALID_DATA, null, "Comment text cannot be empty");
        }
        newCommentText = newCommentText.trim();
        Comments commToUpdate = comRepository.findById(commentID).orElse(null);
        UpdateCommentRequestDTO dto = new UpdateCommentRequestDTO();
        dto.setComment(newCommentText);
        dto.setCommentID(commentID);

        commToUpdate.setComment(newCommentText);
        Comments updatedComment = comRepository.save(commToUpdate);
        return CommentOperationResult.success(updatedComment.getPost().getId());
    }
}
