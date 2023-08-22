package com.repomanager.services;

import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;

import java.util.List;

public interface RepoService {

    List<AllRepositoriesWithBranchesResponse> getAllRepositoriesAndItsBranches(RepositoriesWithBranchesRequest request);

}
