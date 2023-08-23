package com.repomanager.units;

import com.repomanager.models.errors.UserNotFound;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.responses.BranchResponse;
import com.repomanager.services.RepoService;
import com.repomanager.utils.RepoInfo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.repomanager.utils.RepoTestUtils.createBranches;
import static com.repomanager.utils.RepoTestUtils.getRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ActiveProfiles(value = "dev")
class RepoControllerUnitTest {

    @Value("${server.address}")
    String address;

    @Value("${server.port}")
    int port;

    @MockBean
    RepoService repoService;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://" + address;
        RestAssured.basePath = "/repo_manager";
        RestAssured.port = port;

    }

    @Test
    void whenRequestIsOKAndUserIsFoundReturnResponseAndStatus200() {

        String userName = "Garin1998";

        RepositoriesWithBranchesRequest request = getRequest(userName);

        List<RepoInfo> repos = new ArrayList<>();
        repos.add(
                RepoInfo.builder()
                        .repoName("AlgoLearn")
                        .amountOfBranches(4)
                        .build()
        );
        repos.add(
                RepoInfo.builder()
                        .repoName("Chronos")
                        .amountOfBranches(2)
                        .build()
        );
        repos.add(
                RepoInfo.builder()
                        .repoName("magazineDB")
                        .amountOfBranches(1)
                        .build()
        );

        List<AllRepositoriesWithBranchesResponse> expectedResponse =
                getExpectedResponse(
                        repos, userName
                );

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenReturn(expectedResponse);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/getAllRepositoriesAndBranches")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);

        verify(repoService).getAllRepositoriesAndItsBranches(request);

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
                .body("Message", equalTo("Request content not valid"));

    }

    @Test
    void whenUserIsNotFoundThenReturn404() {

        RepositoriesWithBranchesRequest request = getRequest("Garin1997");

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenThrow(UserNotFound.class);

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
                .body("Message", equalTo("User not found"));

        verify(repoService).getAllRepositoriesAndItsBranches(request);

    }



    List<AllRepositoriesWithBranchesResponse> getExpectedResponse(List<RepoInfo> repos, String userName) {

        List<AllRepositoriesWithBranchesResponse> expectedResponse = new ArrayList<>();

        for(RepoInfo repo : repos) {
            AllRepositoriesWithBranchesResponse singleResponse = AllRepositoriesWithBranchesResponse.builder()
                    .repoName(repo.repoName())
                    .userName(userName)
                    .branches(createBranches(repo.amountOfBranches()))
                    .build();

            expectedResponse.add(singleResponse);

        }

        return expectedResponse;

    }

}
