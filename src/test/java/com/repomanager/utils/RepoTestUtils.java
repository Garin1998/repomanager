package com.repomanager.utils;

import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.BranchResponse;

import java.util.ArrayList;
import java.util.List;

public class RepoTestUtils {

   public static RepositoriesWithBranchesRequest getRequest(String userName) {

        return RepositoriesWithBranchesRequest.builder()
                .userName(userName)
                .build();

    }

    public static List<BranchResponse> createBranches(int amountOfBranches) {
        List<BranchResponse> branches = new ArrayList<>();

        for(int i = 0; i < amountOfBranches;i++) {

            BranchResponse branch = BranchResponse.builder()
                    .name("Branch" + i)
                    .lastSha("4221218f7f89c8c110dfb8b8807de7779b976d5" + i)
                    .build();

            branches.add(branch);

        }

        return branches;

    }

}
