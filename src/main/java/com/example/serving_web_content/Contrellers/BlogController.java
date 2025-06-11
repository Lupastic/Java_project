package com.example.serving_web_content.Contrellers;

import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.Repo.ComRepository;
import com.example.serving_web_content.models.Comments;
import com.example.serving_web_content.models.Users;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.ui.Model;
import com.example.serving_web_content.Repo.PostRepository;
import com.example.serving_web_content.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.List;
import java.util.Optional;

@Controller
public class BlogController {
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private Users user;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ComRepository comRepository;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/blog")
    public String blogAdd(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "blog-Main";
    }
    @GetMapping("/blog/add")
    public String blogMain(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "blog-Add";
    }
    @PostMapping("/blog/add")
    public String blogPostAdd(@RequestParam String title,@RequestParam String anons,@AuthenticationPrincipal UserDetails userDetails,@RequestParam String text ,Model model) {
        Post post = new Post(title,anons,text);
        Users user = usersRepository.findByUsername(userDetails.getUsername());
        post.setUser(user);
        postRepository.save(post);
        return "redirect:/blog";
    }
    @GetMapping("/blog/{id}")
    public String blogViewCount(@PathVariable("id") long postId, Model model) {
        Post post = postRepository.findById(postId).orElseThrow();
        post.incrementViewCount();
        postRepository.save(post);
        List<Comments> comms = comRepository.findByPost(post);
        model.addAttribute("post", post);
        model.addAttribute("comms", comms);

        return "blog-details";
    }

    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") long postId, Model model) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return "redirect:/blog";
        }
        model.addAttribute("post", post.get());
        return "blog-edit.html";
    }
    @PostMapping("/blog/{id}/edit")
    public String blogPostUpdate(@PathVariable ("id") long id,@RequestParam String title,@RequestParam String anons,@RequestParam String text ,Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setAnons(anons);
        post.setText(text);
        postRepository.save(post);
        return "redirect:/blog";
    }
    @PostMapping("/blog/{id}/remove")
    public String blogPostDelete(@PathVariable ("id") long id,Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
        return "redirect:/blog";
    }
}
