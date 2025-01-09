package com.cyberpro.social_pub_project.controller;
import com.cyberpro.social_pub_project.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.cyberpro.social_pub_project.service.UserServiceImpl;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl theUserService) {

        this.userService = theUserService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        System.out.println(userService.findAll());
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam int age,
            @RequestParam int idNumber) {

        userService.registerUser(username, password, first_name, last_name, age, idNumber);

        return "redirect:/login";
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
