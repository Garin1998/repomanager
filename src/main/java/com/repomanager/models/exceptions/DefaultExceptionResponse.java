package com.repomanager.models.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record DefaultExceptionResponse(

        @JsonProperty("status")
        int status,
        @JsonProperty("Message")
        String message

) {}
