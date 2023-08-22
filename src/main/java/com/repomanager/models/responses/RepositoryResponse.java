package com.repomanager.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record RepositoryResponse(

        @JsonProperty("repository")
        String repoName,
        @JsonProperty("owner")
        String userName

) {}
