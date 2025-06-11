package com.example.serving_web_content.Repo;

import com.example.serving_web_content.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post,Long> {

    Long id(Long id);
}
