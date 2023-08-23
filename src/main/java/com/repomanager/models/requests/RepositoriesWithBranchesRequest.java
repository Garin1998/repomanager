package com.repomanager.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RepositoriesWithBranchesRequest(

        @JsonProperty("user_name")
        String userName

) {}
