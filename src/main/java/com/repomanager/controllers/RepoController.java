package com.repomanager.controllers;

import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.services.RepoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repo_manager")
public class RepoController {

    private final RepoService repoService;

    @PostMapping(value = "/getAllRepositoriesAndBranches", produces = MediaType.APPLICATION_JSON_VALUE,  consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "Request content not valid",
                    content = @Content()
            )
    })
    ResponseEntity<List<AllRepositoriesWithBranchesResponse>> getAllRepositoriesAndItsBranches(
            @RequestBody RepositoriesWithBranchesRequest request) {
        return ResponseEntity.ok(repoService.getAllRepositoriesAndItsBranches(request));
    }

}
