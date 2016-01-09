package com.alorma.github.sdk.services.repos;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.client.GithubListClient;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 18/07/2015.
 */
public class MemberReposClient extends GithubListClient<List<Repo>> {

    private int page;

    public MemberReposClient() {
        super();
        page = 0;
    }

    public MemberReposClient(int page) {
        super();
        this.page = page;
    }

    @Override
    protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
        return new ApiSubscriber() {
            @Override
            protected void call(RestAdapter restAdapter) {
                ReposService reposService = restAdapter.create(ReposService.class);
                if (page == 0) {
                    reposService.userMemberRepos(this);
                } else {
                    reposService.userMemberRepos(page, this);
                }
            }
        };
    }
}
