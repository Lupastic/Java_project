package com.example.serving_web_content.service.blog;

import com.example.serving_web_content.DTO.blog.CreateBlogRequestDto;
import com.example.serving_web_content.Repo.PostRepository;
import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.models.Comments;
import com.example.serving_web_content.models.Post;
import com.example.serving_web_content.models.Users;
import com.example.serving_web_content.service.comments.CommentOperationResult;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class blogService implements blogInterface {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public blogOperationRresult createPost(String postTitle, String postAnons, String postText, String username) {
        Users userOptional = usersRepository.findByUsername(username);
        CreateBlogRequestDto dto = new CreateBlogRequestDto();
        dto.setTitle(postTitle);
        dto.setAnons(postAnons);
        dto.setText(postText);
        dto.setUserID(userOptional.getId());
        Post savedBlog = postRepository.save(mapToBlog(dto, userOptional));
        return blogOperationRresult.success(savedBlog.getId());
    }

    @Override
    @Transactional
    public blogOperationRresult updatePost(Long postID, String newPostTitle, String newPostAnons, String newPostText, String username) {
        if (postID == null) {
            return blogOperationRresult.failure(blogOperationRresult.Status.INVALID_DATA, null, "Post ID is required for update");
        }

        Optional<Post> postOptional = postRepository.findById(postID);
        if (!postOptional.isPresent()) {
            return blogOperationRresult.failure(blogOperationRresult.Status.NOT_FOUND, postID, "Post not found with ID: " + postID);
        }
        Post postToUpdate = postOptional.get();

        Users user = usersRepository.findByUsername(username);
        if (user == null) {
            return blogOperationRresult.failure(blogOperationRresult.Status.NOT_FOUND, postID, "Authenticated user not found: " + username);
        }

        if (newPostTitle != null) {
            postToUpdate.setTitle(newPostTitle.trim());
        }
        if (newPostAnons != null) {
            postToUpdate.setAnons(newPostAnons.trim());
        }
        if (newPostText != null) {
            postToUpdate.setText(newPostText.trim());
        }

        Post updatedPost = postRepository.save(postToUpdate);
        return blogOperationRresult.success(updatedPost.getId());
    }


    @Override
    @Transactional
    public blogOperationRresult deletePost(Long postID, String username) {
        if (postID == null) {
            return blogOperationRresult.failure(blogOperationRresult.Status.INVALID_DATA, null, "Post ID is required for deletion");
        }

        Optional<Post> postOptional = postRepository.findById(postID);
        if (!postOptional.isPresent()) {
            return blogOperationRresult.failure(blogOperationRresult.Status.NOT_FOUND, postID, "Post not found with ID: " + postID);
        }
        Post postToDelete = postOptional.get();

        Users user = usersRepository.findByUsername(username);
        if (user == null) {
            return blogOperationRresult.failure(blogOperationRresult.Status.NOT_FOUND, postID, "Authenticated user not found: " + username);
        }

        postRepository.delete(postToDelete);
        return blogOperationRresult.success(postID);
    }


    private Post mapToBlog(CreateBlogRequestDto dto, Users user) {
        Post blog = new Post();
        blog.setTitle(dto.getTitle());
        blog.setAnons(dto.getAnons());
        blog.setText(dto.getText());
        blog.setUser(user);
        return blog;
    }
}
