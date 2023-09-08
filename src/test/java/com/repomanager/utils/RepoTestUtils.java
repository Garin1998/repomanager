package com.repomanager.utils;

import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.requests.RepositoriesWithBranchesRequestBuilder;
import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.responses.AllRepositoriesWithBranchesResponseBuilder;
import com.repomanager.models.responses.BranchResponse;
import com.repomanager.models.responses.BranchResponseBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RepoTestUtils {

    public static RepositoriesWithBranchesRequest getRequest(String userName) {

        return RepositoriesWithBranchesRequestBuilder.builder()
                .userName(userName)
                .build();

    }

    public static JSONArray getReposFromGH(List<RepoInfo> repos, String userName) {
        JSONArray jsonArray = new JSONArray();
        JSONObject owner = new JSONObject();
        owner.put("id", "449626721");
        owner.put("login", userName);

        int i = 0;
        for (RepoInfo repo : repos) {
            JSONObject repoAsJson = new JSONObject();
            repoAsJson.put("id", "54962672" + i++);
            repoAsJson.put("name", repo.repoName());
            repoAsJson.put("owner", owner);
            repoAsJson.put("fork", repo.fork());
            jsonArray.put(repoAsJson);

        }

        return jsonArray;

    }

    public static JSONArray getRepoBranchesFromGH(int amountOfBranches) {

        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < amountOfBranches; i++) {

            JSONObject commit = new JSONObject();
            commit.put("sha", "aac24e83e177f276acee6c32488623e02fe2255" + i);

            JSONObject branch = new JSONObject();
            branch.put("name", "Branch" + i);
            branch.put("commit", commit);


            jsonArray.put(branch);

        }

        return jsonArray;

    }

    public static Set<AllRepositoriesWithBranchesResponse> createExpectedResult(List<RepoInfo> repos, String userName) {

        Set<AllRepositoriesWithBranchesResponse> result = new HashSet<>();

        for(RepoInfo repo : repos) {

            result.add(
                    AllRepositoriesWithBranchesResponseBuilder.builder()
                            .repoName(repo.repoName())
                            .userName(userName)
                            .branches(createBranches(repo.amountOfBranches()))
                            .build()
            );

        }

        return result;

    }

    public static Set<BranchResponse> createBranches(int amountOfBranches) {
        Set<BranchResponse> branches = new HashSet<>();

        for (int i = 0; i < amountOfBranches; i++) {

            BranchResponse branch = BranchResponseBuilder.builder()
                    .name("Branch" + i)
                    .lastSha("aac24e83e177f276acee6c32488623e02fe2255" + i)
                    .build();

            branches.add(branch);

        }

        return branches;

    }

}
