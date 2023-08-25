package com.repomanager.controllers;

import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.services.RepoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.repomanager.consts.ControllerConsts.REQUEST_REPO_URL;
import static com.repomanager.consts.ErrorResponseConsts.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(REQUEST_REPO_URL)
@Slf4j
public class RepoController {

    private final RepoService repoService;

    @PostMapping(value = "/getAllRepositoriesAndBranches", produces = MediaType.APPLICATION_JSON_VALUE,  consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved"),
            @ApiResponse(
                    responseCode = "404",
                    description = USER_NOT_FOUND,
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = REQUEST_NOT_VALID,
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = EXTERNAL_API_ERROR,
                    content = @Content()
            )
    })
    ResponseEntity<Set<AllRepositoriesWithBranchesResponse>> getAllRepositoriesAndItsBranches(
            @RequestBody RepositoriesWithBranchesRequest request) {

        log.info("POST body: " + request);
        return ResponseEntity.ok(repoService.getAllRepositoriesAndItsBranches(request));

    }

}
