package com.repomanager.services.implementations;

import com.repomanager.models.exceptions.GenericException;
import com.repomanager.models.exceptions.UserNotFoundException;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.*;
import com.repomanager.services.RepoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.repomanager.consts.ErrorResponseConsts.EXTERNAL_API_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicRepoService implements RepoService {

    private final WebClient webClientForGH;

    @Override
    public Set<AllRepositoriesWithBranchesResponse> getAllRepositoriesAndItsBranches(RepositoriesWithBranchesRequest request) throws HttpClientErrorException {

        Set<AllRepositoriesWithBranchesResponse> repositoriesWithBranchesResponses = new HashSet<>();
        String userReposRespondFromApi = retrieveUsersRepositoriesFromApi(request.userName());
        JSONArray repositoriesWithBranchesResponsesAsJsonArray = new JSONArray(userReposRespondFromApi);

        Set<RepositoryResponse> list = retrieveFromUserReposInfoToBeIncludedInReturnAsList(repositoriesWithBranchesResponsesAsJsonArray);

        for (RepositoryResponse dto : list) {

            String repoBranchesResultFromApi = retrieveRepositoryBranchesFromApi(dto);

            JSONArray repoBranchesResultAsJson = new JSONArray(repoBranchesResultFromApi);

            Set<BranchResponse> branches = retrieveFromBranchesInfoToBeIncludedInReturnAsList(repoBranchesResultAsJson);

            repositoriesWithBranchesResponses.add(AllRepositoriesWithBranchesResponseBuilder.builder()
                    .repoName(dto.repoName())
                    .userName(dto.userName())
                    .branches(branches)
                    .build()
            );

        }

        log.info("Successfully created info about user's repos");
        return repositoriesWithBranchesResponses;

    }

    public String retrieveUsersRepositoriesFromApi(String userName) {

        return webClientForGH.get()
                .uri("/users/" + userName + "/repos")
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        clientResponse -> Mono.error(new GenericException())
                )
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new UserNotFoundException())
                )
                .bodyToMono(String.class)
                .block();

    }

    public Set<RepositoryResponse> retrieveFromUserReposInfoToBeIncludedInReturnAsList(JSONArray resultsToBeProcessed) {
        return StreamSupport.stream(resultsToBeProcessed.spliterator(), false)
                .map(e -> (JSONObject) e)
                .filter(e -> !e.getBoolean("fork"))
                .map(
                        e -> RepositoryResponseBuilder.builder()
                                .userName(e.getJSONObject("owner").getString("login"))
                                .repoName(e.getString("name"))
                                .build()
                ).collect(Collectors.toSet());

    }

    public String retrieveRepositoryBranchesFromApi(RepositoryResponse repositoryResponse) {

        return webClientForGH.get()
                .uri("/repos/" + repositoryResponse.userName() + "/" + repositoryResponse.repoName() + "/branches")
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        clientResponse -> Mono.error(new GenericException(EXTERNAL_API_ERROR))
                )
                .bodyToMono(String.class)
                .block();

    }

    public Set<BranchResponse> retrieveFromBranchesInfoToBeIncludedInReturnAsList(JSONArray resultsToBeProcessed) {

        return StreamSupport.stream(resultsToBeProcessed.spliterator(), false)
                .map(e -> (JSONObject) e)
                .map(
                        e -> BranchResponseBuilder.builder()
                                .name(e.getString("name"))
                                .lastSha(e.getJSONObject("commit").getString("sha"))
                                .build()
                ).collect(Collectors.toSet());

    }

}
