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
                    .lastSha("aac24e83e177f276acee6c32488623e02fe2255" + i)
                    .build();

            branches.add(branch);

        }

        return branches;

    }

}
