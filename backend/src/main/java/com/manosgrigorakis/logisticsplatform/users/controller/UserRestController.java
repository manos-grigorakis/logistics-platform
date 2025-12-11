package com.manosgrigorakis.logisticsplatform.users.controller;

import com.manosgrigorakis.logisticsplatform.users.dto.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.UserResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "CRUD operation on users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get All Users", description = "Lists all the users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List all the users"),
    })
    @GetMapping()
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get User by Id", description = "Find user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded user"),
            @ApiResponse(responseCode = "404", description = "User doesn't exist"),
    })
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Create a User", description = """
            Creates a new user without a password.\s
            Sends an email with a generated token, so user can activate their account
           \s""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "404", description = "Role doesn't exist"),
            @ApiResponse(responseCode = "409", description = "User email already exist"),
    })
    @PostMapping()
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO dto) {
        UserResponseDTO response = userService.createUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a User by Id", description = "Updates a user account by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User doesn't exists </br> Role doesn't exist"),
            @ApiResponse(responseCode = "409", description = "User email already exists")
    })
    @PutMapping("/{id}")
    public UserResponseDTO updateUserById(@PathVariable Long id, @RequestBody @Valid UserRequestDTO dto) {
        return userService.updateUserById(id, dto);
    }

    @Operation(summary = "Delete a User by Id", description = "Deletes a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User doesn't exist"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }
}
