package com.repomanager.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record RepositoryResponse(

        @JsonProperty("repository")
        String repoName,
        @JsonProperty("owner")
        String userName

) {}
