package com.repomanager.units;

import com.repomanager.models.exceptions.GenericException;
import com.repomanager.models.exceptions.UserNotFoundException;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.responses.RepositoryResponse;
import com.repomanager.services.implementations.BasicRepoService;
import com.repomanager.utils.RepoInfo;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.repomanager.consts.ErrorResponseConsts.EXTERNAL_API_ERROR;
import static com.repomanager.utils.RepoTestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(value = "dev")
class BasicRepoServiceUnitTest {

    @MockBean
    BasicRepoService repoService;

    @Test
    void whenRequestIsValidAndGitHubAPIWithProvidedUserNameReturnReposAsSet() {

        String userName = "Garin1998";
        String validRepo = "AlgoLearn";
        int amountOfBranchesForValidRepo = 4;

        List<RepoInfo> repos = new ArrayList<>();
        repos.add(
                RepoInfo.builder()
                        .repoName(validRepo)
                        .amountOfBranches(amountOfBranchesForValidRepo)
                        .fork(false)
                        .build()
        );
        repos.add(
                RepoInfo.builder()
                        .repoName("Chronos")
                        .fork(true)
                        .build()
        );

        RepositoriesWithBranchesRequest request = getRequest(userName);

        RepositoryResponse validRepositoryResponse =
                RepositoryResponse.builder()
                        .userName(request.userName())
                        .repoName(validRepo)
                        .build();

        JSONArray resultReposFromGitHubApi = getReposFromGH(repos, userName);
        JSONArray resultRepoBranchesFromGitHubApi = getRepoBranchesFromGH(amountOfBranchesForValidRepo);

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenCallRealMethod();
        when(repoService.retrieveUsersRepositoriesFromApi(request.userName())).thenReturn(resultReposFromGitHubApi.toString());
        when(repoService.retrieveFromUserReposInfoToBeIncludedInReturnAsList(ArgumentMatchers.any())).thenCallRealMethod();
        when(repoService.retrieveRepositoryBranchesFromApi(validRepositoryResponse)).thenReturn(resultRepoBranchesFromGitHubApi.toString());
        when(repoService.retrieveFromBranchesInfoToBeIncludedInReturnAsList(ArgumentMatchers.any())).thenCallRealMethod();

        Set<AllRepositoriesWithBranchesResponse> actual = repoService.getAllRepositoriesAndItsBranches(request);
        Set<AllRepositoriesWithBranchesResponse> expected = createExpectedResult(List.of(repos.get(0)), userName);

        assertThat(actual, is(expected));

    }

    @Test
    void whenProvidedUserNameNotExistInGitHubThenThrowAndException() {

        String userName = "Garin1997";
        RepositoriesWithBranchesRequest request = getRequest(userName);

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenCallRealMethod();
        when(repoService.retrieveUsersRepositoriesFromApi(request.userName())).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> repoService.getAllRepositoriesAndItsBranches(request));

    }

    @Test
    void whenProvidedUserNameIsValidButExternalApiReturnErrorThenThrowException() {

        String userName = "Garin1998";
        RepositoriesWithBranchesRequest request = getRequest(userName);

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenCallRealMethod();
        when(repoService.retrieveUsersRepositoriesFromApi(request.userName())).thenThrow(new GenericException(EXTERNAL_API_ERROR));

        Exception exception = assertThrows(GenericException.class, () -> repoService.getAllRepositoriesAndItsBranches(request));

        assertThat(exception.getMessage(), equalTo(EXTERNAL_API_ERROR));

    }

}
