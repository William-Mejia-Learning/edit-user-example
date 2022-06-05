package com.example.userproject.controllers;

import com.example.userproject.models.User;
import com.example.userproject.repositories.UserRepository;
import com.example.userproject.services.UserDetailsLoader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PreUpdate;

@Controller
public class UserController {
    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/edit-error")
    @ResponseBody
    public String editError(){
        return "Error editing User";
    }

    @GetMapping("/profile")
    public String showProfile(){
        return "profile";
    }

    @GetMapping("/")
    public String signIn(Model model){
        model.addAttribute("user", new User());

        return "sign-in";

    }

    @PostMapping("/")
    public String createUser(@ModelAttribute User user){
        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        userDao.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogIng(){
        return "login";
    }

    @GetMapping("/edit")
    public String editForm(Model model){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("useredit", userDao.findById(user.getId()));
        return "/edit-profile";
    }

    @PostMapping("/edit")
    public String submitEdit(@ModelAttribute User user){
        User userInfoPull = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User editUser = userDao.getById(userInfoPull.getId());

        editUser.setUsername(user.getUsername());
        editUser.setEmail(user.getEmail());

        if(userInfoPull.getUsername().equals(editUser.getUsername()) || userDao.existsByUsername(user.getUsername()) || userInfoPull.equals(editUser.getEmail()) || userDao.existsByEmail(user.getEmail())){
            return "redirect:/edit-error";
        }

        userDao.save(editUser);
        return "redirect:/profile";
    }
}
