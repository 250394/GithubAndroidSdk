package com.alorma.github.sdk.services.pullrequest.story;

import com.alorma.github.sdk.PullRequest;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Bernat on 22/08/2014.
 */
public interface PullRequestStoryService {

    @GET("/repos/{owner}/{name}/pulls/{num}")
    void detail(@Path("owner") String owner, @Path("name") String repo, @Path("num") int num, Callback<PullRequest> issueCallback);


}
