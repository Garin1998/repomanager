package com.repomanager.services.implementations;

import com.repomanager.models.responses.AllRepositoriesWithBranchesResponse;
import com.repomanager.models.requests.RepositoriesWithBranchesRequest;
import com.repomanager.services.RepoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasicRepoService implements RepoService {

    @Override
    public List<AllRepositoriesWithBranchesResponse> getAllRepositoriesAndItsBranches(RepositoriesWithBranchesRequest request) {

        return null;

    }

}
