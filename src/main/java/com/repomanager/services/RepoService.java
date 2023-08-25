package com.repomanager.services;

import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;

import java.util.Set;

public interface RepoService {

    Set<AllRepositoriesWithBranchesResponse> getAllRepositoriesAndItsBranches(RepositoriesWithBranchesRequest request);

}
