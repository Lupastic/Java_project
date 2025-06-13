package com.example.serving_web_content.Controllers;

import com.example.serving_web_content.Repo.ComRepository;
import com.example.serving_web_content.Repo.PostRepository;
import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.models.Comments;
import com.example.serving_web_content.models.Post;
import com.example.serving_web_content.models.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommentController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ComRepository comRepository;
    @Autowired
    private UsersRepository usersRepository;


    @PostMapping("/comm/add")
    public String commPostAdd(@RequestParam(value = "postId") long postId, @RequestParam("commentText") String commentText, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Comments comm = new Comments(commentText);
        Post post = postRepository.findById(postId).orElseThrow();
        Users user = usersRepository.findByUsername(userDetails.getUsername());
        comm.setUser(user);
        comm.setPost(post);
        comRepository.save(comm);
        return "redirect:/blog/" + postId;
    }
    @PostMapping("/comm/update")
    public String updateComment(@RequestParam("id") long commentId,@RequestParam("comment") String commentText, @AuthenticationPrincipal UserDetails userDetails) {
        Comments comm = comRepository.findById(commentId).orElseThrow();
        comm.setComment(commentText);
        comRepository.save(comm);
        long postId = comm.getPost().getId();
        return "redirect:/blog/" + postId;
    }
    @PostMapping("/comm/delete")
    public String removeComment(@RequestParam("id") long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        Comments comm = comRepository.findById(commentId).orElseThrow();
        long postId = comm.getPost().getId();
        comRepository.deleteById(commentId);
        return "redirect:/blog/" + postId;
    }
}
