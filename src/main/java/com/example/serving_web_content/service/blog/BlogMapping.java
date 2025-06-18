package com.example.serving_web_content.service.blog;

import com.example.serving_web_content.DTO.blog.CreateBlogRequestDto;
import com.example.serving_web_content.models.Post;
import com.example.serving_web_content.models.Users;
import org.springframework.stereotype.Component;

@Component
public class BlogMapping {
    public Post mapToBlog(CreateBlogRequestDto dto, Users user) {
        Post blog = new Post();
        blog.setTitle(dto.getTitle());
        blog.setAnons(dto.getAnons());
        blog.setText(dto.getText());
        blog.setUser(user);
        return blog;
    }
}
