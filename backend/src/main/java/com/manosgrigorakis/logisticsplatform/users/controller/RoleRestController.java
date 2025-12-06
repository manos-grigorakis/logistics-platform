package com.manosgrigorakis.logisticsplatform.users.controller;

import com.manosgrigorakis.logisticsplatform.users.dto.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.service.RoleService;
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
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "CRUD operation for roles")
public class RoleRestController {
    private final RoleService roleService;

    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Get All Roles", description = "Lists all the roles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List all the roles"),
    })
    @GetMapping()
    public List<RoleResponseDTO> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "Get Role by Id", description = "Find role by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded role"),
            @ApiResponse(responseCode = "404", description = "Role doesn't exist"),
    })
    @GetMapping("/{id}")
    public RoleResponseDTO getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @Operation(summary = "Create a Role", description = "Create a new role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody @Valid RoleRequestDTO dto) {
        RoleResponseDTO response = roleService.createRole(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a Role by Id", description = "Update a role by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "403", description = "Role is protected and cannot been modified"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    @PutMapping("/{id}")
    public RoleResponseDTO updateRole(@PathVariable Long id, @RequestBody @Valid RoleRequestDTO dto) {
        return roleService.updateRole(id, dto);
    }

    @Operation(summary = "Delete a Role by Id", description = "Delete a role by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Role has active users assigned"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoleById(@PathVariable Long id) {
        roleService.deleteRoleById(id);

        return ResponseEntity.noContent().build();
    }
}
