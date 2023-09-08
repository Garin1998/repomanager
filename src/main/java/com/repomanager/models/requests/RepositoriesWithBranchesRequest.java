package com.repomanager.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record RepositoriesWithBranchesRequest(

        @JsonProperty("user_name")
        String userName

) {}
