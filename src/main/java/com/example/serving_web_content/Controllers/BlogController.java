package com.example.serving_web_content.Controllers;
import com.example.serving_web_content.DTO.blog.CreateBlogRequestDto;
import com.example.serving_web_content.DTO.blog.UpdateBlogRequestDto;
import com.example.serving_web_content.Repo.ComRepository;
import com.example.serving_web_content.models.Comments;
import com.example.serving_web_content.service.blog.blogOperationRresult;
import com.example.serving_web_content.service.blog.blogService;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.ui.Model;
import com.example.serving_web_content.Repo.PostRepository;
import com.example.serving_web_content.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
public class BlogController {
    private final blogService BlogService;
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ComRepository comRepository;
    @Autowired
    public BlogController(blogService blogService) {
        BlogService = blogService;
    }

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
    public String blogPostAdd(@ModelAttribute CreateBlogRequestDto dto,
                              @AuthenticationPrincipal UserDetails userDetails) {
        blogOperationRresult result = BlogService.createPost(
                dto.getTitle(),
                dto.getAnons(),
                dto.getText(),
                userDetails.getUsername()
        );
        if (result.isSucces()){
            return "redirect:/blog";
        }
        else{
            return "redirect:/blog";
        }
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
    public String blogPostUpdate(@PathVariable("id") Long postIDFromPath,
                                 @ModelAttribute UpdateBlogRequestDto dto,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {

        if (postIDFromPath == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Не указан ID поста для редактирования.");
            return "redirect:/blog";
        }
        blogOperationRresult result = BlogService.updatePost(
                postIDFromPath,
                dto.getTitle(),
                dto.getAnons(),
                dto.getText(),
                userDetails.getUsername());
        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", "Пост успешно обновлен!");
            return "redirect:/blog/" + postIDFromPath;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось обновить пост: " + result.getErrorMessage());
            return "redirect:/blog";
        }
    }
    @PostMapping("/blog/{id}/remove")
    public String blogPostDelete(@PathVariable("id") Long postIDFromPath,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        blogOperationRresult result = BlogService.deletePost(
                postIDFromPath,
                userDetails.getUsername());
        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", "Комментарий успешно удален!");
            return "redirect:/blog";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось удалить комментарий: " + result.getErrorMessage());
            return "redirect:/blog";
        }
    }
}
