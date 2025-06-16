package com.example.serving_web_content.Controllers;

import com.example.serving_web_content.DTO.comment.CreateCommentRequestDto;
import com.example.serving_web_content.DTO.comment.UpdateCommentRequestDTO;
import com.example.serving_web_content.service.comments.CommentOperationResult;
import com.example.serving_web_content.service.comments.ComServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentController {
    private final ComServiceImpl comService;

    @Autowired
    public CommentController(ComServiceImpl comService) {
        this.comService = comService;
    }

    @PostMapping("/comm/add")
        public String commPostAdd(@ModelAttribute CreateCommentRequestDto dto,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {


        CommentOperationResult result = comService.createComment(
                dto.getPostID(),
                dto.getComment(),
                userDetails.getUsername());

        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", "Комментарий успешно добавлен!");
            return "redirect:/blog/" + result.getPostIdForRedirect();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось добавить комментарий: " + result.getErrorMessage());
                return "redirect:/blog";
        }
    }

    @PostMapping("/comm/update")
    public String updateComment(@ModelAttribute UpdateCommentRequestDTO dto,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        CommentOperationResult result = comService.updateComment(
                dto.getCommentID(),
                dto.getComment(),
                userDetails.getUsername());
        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", "Комментарий успешно обновлен!");
            return "redirect:/blog/" + result.getPostIdForRedirect();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось обновить комментарий: " + result.getErrorMessage());
            return "redirect:/blog";
        }
    }
    @PostMapping("/comm/delete")
    public String removeComment(@ModelAttribute UpdateCommentRequestDTO dto,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        CommentOperationResult result = comService.deleteComment(
                dto.getCommentID(),
                userDetails.getUsername());
        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", "Комментарий успешно удален!");
            return "redirect:/blog/" + result.getPostIdForRedirect();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось удалить комментарий: " + result.getErrorMessage());
            return "redirect:/blog";
        }
    }
}
