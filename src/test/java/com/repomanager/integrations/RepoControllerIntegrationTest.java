package com.repomanager.integrations;

import com.repomanager.models.exceptions.GenericException;
import com.repomanager.models.exceptions.UserNotFoundException;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.RepositoryResponse;
import com.repomanager.models.responses.RepositoryResponseBuilder;
import com.repomanager.services.implementations.BasicRepoService;
import com.repomanager.utils.RepoInfo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.repomanager.consts.ControllerConsts.REQUEST_REPO_URL;
import static com.repomanager.consts.ErrorResponseConsts.*;
import static com.repomanager.utils.RepoTestUtils.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("integration")
class RepoControllerIntegrationTest {

    @LocalServerPort
    int port;

    @MockBean
    BasicRepoService repoService;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = REQUEST_REPO_URL;
        RestAssured.port = port;

    }

    @Test
    void whenRequestIsOKAndUserIsFoundReturnResponseAndStatus200() {

        String userName = "Garin1998";
        String validRepo = "AlgoLearn";
        int amountOfBranchesForValidRepo = 4;

        RepositoriesWithBranchesRequest request = getRequest(userName);

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

        RepositoryResponse validRepositoryResponse =
                RepositoryResponseBuilder.builder()
                        .userName(request.userName())
                        .repoName(validRepo)
                        .build();

        JSONArray resultReposFromGitHubApi = getReposFromGH(repos, userName);
        JSONArray resultRepoBranchesFromGitHubApi = getRepoBranchesFromGH(amountOfBranchesForValidRepo);

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenCallRealMethod();
        when(repoService.retrieveFromUserReposInfoToBeIncludedInReturnAsList(ArgumentMatchers.any())).thenCallRealMethod();
        when(repoService.retrieveFromBranchesInfoToBeIncludedInReturnAsList(ArgumentMatchers.any())).thenCallRealMethod();
        when(repoService.retrieveUsersRepositoriesFromApi(request.userName())).thenReturn(resultReposFromGitHubApi.toString());
        when(repoService.retrieveRepositoryBranchesFromApi(validRepositoryResponse)).thenReturn(resultRepoBranchesFromGitHubApi.toString());


        String response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/getAllRepositoriesAndBranches")
        .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .asPrettyString();

        JSONArray actualResponse = new JSONArray(response);
        JSONArray expectedResponse = new JSONArray(createExpectedResult(List.of(repos.get(0)), userName));

        JSONAssert.assertEquals(expectedResponse, actualResponse, false);

    }

    @Test
    void whenAcceptedTypeIsNotAJSONThenReturnStatusCode406() {

        given()
                .accept(ContentType.XML)
                .body("")
        .when()
                .post("/getAllRepositoriesAndBranches").prettyPeek()
        .then()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(HttpStatus.NOT_ACCEPTABLE.value()))
                .body("Message", equalTo(REQUEST_NOT_VALID));

    }

    @Test
    void whenUserIsNotFoundThenReturn404() {

        RepositoriesWithBranchesRequest request = getRequest("Garin1997");

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenCallRealMethod();
        when(repoService.retrieveUsersRepositoriesFromApi(request.userName())).thenThrow(new UserNotFoundException());

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/getAllRepositoriesAndBranches")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
                .body("Message", equalTo(USER_NOT_FOUND));

    }

    @Test
    void whenExternalApiReturnForbiddenThenReturn500() {

        RepositoriesWithBranchesRequest request = getRequest("Garin1998");

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenCallRealMethod();
        when(repoService.retrieveUsersRepositoriesFromApi(request.userName())).thenThrow(new GenericException());

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/getAllRepositoriesAndBranches")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .body("Message", equalTo(EXTERNAL_API_ERROR));

    }

}

