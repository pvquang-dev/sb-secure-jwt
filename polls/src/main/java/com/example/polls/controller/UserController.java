package com.example.polls.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.polls.config.security.CurrentUser;
import com.example.polls.config.security.UserPrincipal;
import com.example.polls.constants.AppConstants;
import com.example.polls.dto.response.PagedResponse;
import com.example.polls.dto.response.PollResponse;
import com.example.polls.dto.response.UserIdentityAvailability;
import com.example.polls.dto.response.UserProfile;
import com.example.polls.dto.response.UserSummary;
import com.example.polls.entity.User;
import com.example.polls.exeption.ResourceNotFoundException;
import com.example.polls.repository.PollRepository;
import com.example.polls.repository.UserRepository;
import com.example.polls.repository.VoteRepository;
import com.example.polls.service.PollService;

@RestController
@RequestMapping("/api")
public class UserController {
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PollRepository pollRepository;

  @Autowired
  private VoteRepository voteRepository;

  @Autowired
  private PollService pollService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @GetMapping("/user/me")
  @PreAuthorize("hasRole('USER')")
  public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
    UserSummary userSummary =
        new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
    return userSummary;
  }

  @GetMapping("/user/checkUsernameAvailability")
  public UserIdentityAvailability checkUsernameAvailability(
      @RequestParam(value = "username") String username) {
    Boolean isAvailable = !userRepository.existsByUsername(username);
    return new UserIdentityAvailability(isAvailable);
  }

  @GetMapping("/user/checkEmailAvailability")
  public UserIdentityAvailability checkEmailAvailability(
      @RequestParam(value = "email") String email) {
    Boolean isAvailable = !userRepository.existsByEmail(email);
    return new UserIdentityAvailability(isAvailable);
  }

  @GetMapping("/users/{username}")
  public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    long pollCount = pollRepository.countByCreatedBy(user.getId());
    long voteCount = voteRepository.countByUserId(user.getId());

    UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(),
        user.getCreatedAt(), pollCount, voteCount);

    return userProfile;
  }

  @GetMapping("/users/{username}/polls")
  public PagedResponse<PollResponse> getPollsCreatedBy(
      @PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser,
      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
    return pollService.getPollsCreatedBy(username, currentUser, page, size);
  }


  @GetMapping("/users/{username}/votes")
  public PagedResponse<PollResponse> getPollsVotedBy(
      @PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser,
      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
    return pollService.getPollsVotedBy(username, currentUser, page, size);
  }
}
