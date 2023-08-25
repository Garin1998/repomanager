package com.repomanager.units;

import com.repomanager.models.exceptions.GenericException;
import com.repomanager.models.exceptions.UserNotFoundException;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.services.RepoService;
import com.repomanager.utils.RepoInfo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.repomanager.consts.ControllerConsts.REQUEST_REPO_URL;
import static com.repomanager.consts.ErrorResponseConsts.*;
import static com.repomanager.utils.RepoTestUtils.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(value = "dev")
class RepoControllerUnitTest {

    @LocalServerPort
    int port;

    @MockBean
    RepoService repoService;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = REQUEST_REPO_URL;
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
                        .fork(true)
                        .amountOfBranches(4)
                        .build()
        );
        repos.add(
                RepoInfo.builder()
                        .repoName("Chronos")
                        .fork(true)
                        .amountOfBranches(2)
                        .build()
        );
        repos.add(
                RepoInfo.builder()
                        .repoName("magazineDB")
                        .fork(false)
                        .amountOfBranches(1)
                        .build()
        );

        Set<AllRepositoriesWithBranchesResponse> expectedResponseAsList = createExpectedResult(List.of(repos.get(2)), userName);

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenReturn(expectedResponseAsList);

        String response =
                given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(request)
                .when()
                    .post("/getAllRepositoriesAndBranches")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .contentType(ContentType.JSON)
                    .extract().asString();

        JSONArray actualResponse = new JSONArray(response);
        JSONArray expectedResponseAsJson = new JSONArray(expectedResponseAsList);

        JSONAssert.assertEquals(expectedResponseAsJson, actualResponse, false);

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
                .body("Message", equalTo(REQUEST_NOT_VALID));

    }

    @Test
    void whenUserIsNotFoundThenReturn404() {

        RepositoriesWithBranchesRequest request = getRequest("Garin1997");

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenThrow(UserNotFoundException.class);

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

        verify(repoService).getAllRepositoriesAndItsBranches(request);

    }

    @Test
    void whenExternalApiReturnForbiddenThenReturn500() {

        RepositoriesWithBranchesRequest request = getRequest("Garin1997");

        when(repoService.getAllRepositoriesAndItsBranches(request)).thenThrow(new GenericException(EXTERNAL_API_ERROR));

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

        verify(repoService).getAllRepositoriesAndItsBranches(request);

    }

}
