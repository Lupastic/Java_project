package com.example.serving_web_content.Repo;

import com.example.serving_web_content.models.Comments;
import com.example.serving_web_content.models.Post;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ComRepository extends CrudRepository<Comments, Long> {
    List<Comments> findByPost(Post post);
}
