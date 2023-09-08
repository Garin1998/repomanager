package com.repomanager.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.Set;

@RecordBuilder
public record AllRepositoriesWithBranchesResponse(

        @JsonProperty("name")
        String repoName,
        @JsonProperty("owner")
        String userName,
        @JsonProperty("branches")
        Set<BranchResponse> branches

) {}
