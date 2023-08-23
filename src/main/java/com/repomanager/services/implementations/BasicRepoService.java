package com.repomanager.services.implementations;

import com.repomanager.models.errors.UserNotFound;
import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.BranchResponse;
import com.repomanager.models.responses.RepositoryResponse;
import com.repomanager.services.RepoService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class BasicRepoService implements RepoService {

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<AllRepositoriesWithBranchesResponse> getAllRepositoriesAndItsBranches(RepositoriesWithBranchesRequest request) {

        List<AllRepositoriesWithBranchesResponse> repositoriesWithBranchesResponses = new ArrayList<>();

        String userReposUri = "https://api.github.com/users/" + request.userName() + "/repos";
        ResponseEntity<String> userReposResult = restTemplate.getForEntity(userReposUri, String.class);

        if(userReposResult.getStatusCode().is4xxClientError()) {
            throw new UserNotFound();
        }

        JSONArray results = new JSONArray(userReposResult.getBody());

        List<RepositoryResponse> list = StreamSupport.stream(results.spliterator(), false)
                .map(e -> (JSONObject) e)
                .filter(e -> !e.getBoolean("fork"))
                .map(
                        e -> RepositoryResponse.builder()
                                .userName(e.getJSONObject("owner").getString("login"))
                                .repoName(e.getString("name"))
                                .build()
                ).toList();

        for (RepositoryResponse dto : list) {
            String uri2 = "https://api.github.com/repos/" +
                    dto.userName() +
                    "/" +
                    dto.repoName() +
                    "/branches";

            String repoBranchesResult = restTemplate.getForObject(uri2, String.class);

            JSONArray repoBranchesResultAsJson = new JSONArray(repoBranchesResult);

            List<BranchResponse> branches = StreamSupport.stream(repoBranchesResultAsJson.spliterator(), false)
                    .map(e -> (JSONObject) e)
                    .map(
                            e -> BranchResponse.builder()
                                    .name(e.getString("name"))
                                    .lastSha(e.getJSONObject("commit").getString("sha"))
                                    .build()
                    ).toList();

            repositoriesWithBranchesResponses.add(AllRepositoriesWithBranchesResponse.builder()
                    .repoName(dto.repoName())
                    .userName(dto.userName())
                    .branches(branches)
                    .build()
            );


        }

        return repositoriesWithBranchesResponses;

    }

}
