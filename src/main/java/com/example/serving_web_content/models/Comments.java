package com.example.serving_web_content.models;

import jakarta.persistence.*;

@Entity
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Integer likes;
    @ManyToOne
    private Post post;
    @ManyToOne
    @JoinColumn(name = "userid")
    private Users user;

    public Comments() {
    }
    public Comments(String comment) {
        this.comment = comment;
    }

    public Post getPost(){return post;}
    public void setPost(Post post){this.post=post;}
    public Users getUser() {return user;}
    public void setUser(Users user) {
        this.user = user;
    }
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getComment() {return comment;}
    public void setComment(String comment) {this.comment = comment;}
    public Integer getLikes() {return likes;}
    public void setLikes(Integer likes) {this.likes = likes;    }
}
