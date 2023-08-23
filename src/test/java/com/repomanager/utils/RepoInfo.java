package com.repomanager.utils;

import lombok.Builder;

@Builder
public record RepoInfo(
        String repoName,
        boolean fork,
        int amountOfBranches
) {}
