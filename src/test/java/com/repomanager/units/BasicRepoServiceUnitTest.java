package com.repomanager.units;

import com.repomanager.models.errors.UserNotFound;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.responses.BranchResponse;
import com.repomanager.services.implementations.BasicRepoService;
import com.repomanager.utils.RepoInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.repomanager.utils.RepoTestUtils.createBranches;
import static com.repomanager.utils.RepoTestUtils.getRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest
@ActiveProfiles(value = "dev")
class BasicRepoServiceUnitTest {

    @InjectMocks
    BasicRepoService repoService;

    @Mock
    RestTemplate restTemplate;

    @Test
    void whenRequestIsValidAndGitHubAPIWithProvidedUserNameReturnReposThenReturnList() {

        String userName = "Garin1998";
        String validRepo = "AlgoLearn";
        int amountOfBranchesForValidRepo = 4;

        RepositoriesWithBranchesRequest request = getRequest(userName);
        String repoUri = "https://api.github.com/users/" + userName + "/repos";
        String validRepoBranchesUri = "https://api.github.com/repos/" + userName + "/" + validRepo + "/branches";

        List<RepoInfo> repos = new ArrayList<>();
        repos.add(
                RepoInfo.builder()
                        .repoName(validRepo)
                        .fork(false)
                        .build()
        );
        repos.add(
                RepoInfo.builder()
                        .repoName("Chronos")
                        .fork(true)
                        .build()
        );

        JSONArray resultReposFromGitHubAPI = getReposFromGH(repos, userName);
        JSONArray resultRepoBranchesFromGitHubAPI = getRepoBranchesFromGH(amountOfBranchesForValidRepo);

        when(restTemplate.getForEntity(repoUri, String.class)).thenReturn(ResponseEntity.ok(resultReposFromGitHubAPI.toString()));
        when(restTemplate.getForEntity(validRepoBranchesUri, String.class)).thenReturn(ResponseEntity.ok(resultRepoBranchesFromGitHubAPI.toString()));

        List<AllRepositoriesWithBranchesResponse> actual = repoService.getAllRepositoriesAndItsBranches(request);
        List<AllRepositoriesWithBranchesResponse> expected = createExpectedResult(List.of(repos.get(0)), userName);

        assertThat(actual, is(expected));

        verify(restTemplate).getForObject(repoUri, String.class);

    }

    @Test
    void whenProvidedUserNameNotExistInGitHubThenThrowAndException() {

        String userName = "Garin1997";
        RepositoriesWithBranchesRequest request = getRequest(userName);
        String repoUri = "https://api.github.com/users/" + userName + "/repos";

        when(restTemplate.getForEntity(repoUri, String.class)).thenReturn(ResponseEntity.notFound().build());

        Exception exception = assertThrows(UserNotFound.class, () -> {
            repoService.getAllRepositoriesAndItsBranches(request);
        });

        String actualMessage = exception.getMessage();
        String expectedMessage = "User Not Found";

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(restTemplate).getForObject(repoUri, String.class);

    }

    JSONArray getReposFromGH(List<RepoInfo> repos, String userName) {

        JSONArray jsonArray = new JSONArray();

        JSONObject owner = new JSONObject();
        owner.put("id", "449626721");
        owner.put("login", userName);

        int i = 0;
        for(RepoInfo repo : repos) {

            JSONObject repoAsJson = new JSONObject();
            repoAsJson.put("id", "54962672" + i++);
            repoAsJson.put("name", repo.repoName());
            repoAsJson.put("owner", owner);
            repoAsJson.put("fork", repo.fork());

            jsonArray.put(repoAsJson);

        }

        return jsonArray;

    }


    JSONArray getRepoBranchesFromGH(int amountOfBranches) {

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


    List<AllRepositoriesWithBranchesResponse> createExpectedResult(List<RepoInfo> repos, String userName) {

        List<AllRepositoriesWithBranchesResponse> result = new ArrayList<>();

        for(RepoInfo repo : repos) {

            AllRepositoriesWithBranchesResponse.builder()
                    .repoName(repo.repoName())
                    .userName(userName)
                    .branches(createBranches(repo.amountOfBranches()))
                    .build();

        }

        return result;

    }

}
