package haedal.Bootcamp2024_2.controller;

import haedal.Bootcamp2024_2.domain.User;
import haedal.Bootcamp2024_2.dto.request.UserUpdateRequestDto;
import haedal.Bootcamp2024_2.dto.response.PostResponseDto;
import haedal.Bootcamp2024_2.dto.response.UserDetailResponseDto;
import haedal.Bootcamp2024_2.service.AuthService;
import haedal.Bootcamp2024_2.service.PostService;
import haedal.Bootcamp2024_2.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping
public class UserController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;


    @PutMapping("/users/profile")
    public ResponseEntity<UserDetailResponseDto> updateUser(@RequestBody UserUpdateRequestDto userUpdateRequestDto, HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        UserDetailResponseDto updatedUser = userService.updateUser(currentUser.getId(), userUpdateRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/images/userImages/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/userImages";

            Path imagePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(imagePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/image")
    public ResponseEntity<String> updateUserImage(@RequestParam("image") MultipartFile image, HttpServletRequest request) throws IOException {
        User currentUser = authService.getCurrentUser(request);

        String savedImage = userService.updateImage(currentUser.getId(), image);
        return ResponseEntity.ok(savedImage);
    }

    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<UserDetailResponseDto> getUserDetail(@PathVariable Long userId) {
        UserDetailResponseDto userDetailResponseDto = userService.getUserDetail(userId);
        return ResponseEntity.ok(userDetailResponseDto);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<Page<PostResponseDto>> getPostsByUser(@PathVariable Long userId, Pageable pageable) {
        Page<PostResponseDto> posts = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(posts);
    }
}