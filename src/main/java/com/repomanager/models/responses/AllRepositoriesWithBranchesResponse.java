package com.repomanager.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Set;

@Builder
public record AllRepositoriesWithBranchesResponse(

        @JsonProperty("name")
        String repoName,
        @JsonProperty("owner")
        String userName,
        @JsonProperty("branches")
        Set<BranchResponse> branches

) {}
