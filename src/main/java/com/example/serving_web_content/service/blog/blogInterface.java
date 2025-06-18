package com.example.serving_web_content.service.blog;

public interface blogInterface {
    blogOperationRresult createPost(String postTitle,String postAnons,String postText,String username);
    blogOperationRresult updatePost(Long postID,String newPostTitle,String newPostAnons, String newPostText, String username);
    blogOperationRresult deletePost(Long postID,String username);
}
