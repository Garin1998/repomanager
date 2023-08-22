package com.repomanager.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepositoriesWithBranchesRequest(

        @JsonProperty("user_name")
        String userName

) {}
