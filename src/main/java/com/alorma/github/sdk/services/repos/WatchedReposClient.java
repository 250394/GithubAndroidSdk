package com.alorma.github.sdk.services.repos;

import android.content.Context;

/**
 * Created by Bernat on 17/07/2014.
 */
public class WatchedReposClient extends BaseReposClient{

    public WatchedReposClient(Context context) {
        super(context);
    }

    public WatchedReposClient(Context context, String username) {
        super(context, username);
    }

    public WatchedReposClient(Context context, String username, int page) {
        super(context, username, page);
    }

    @Override
    protected void executeUserFirstPage(String sort, ReposService usersService) {
        usersService.userSubscribedReposList(this);
    }

    @Override
    protected void executeFirstPageByUsername(String username, String sort, ReposService usersService) {
        usersService.userSubscribedReposList(username, this);
    }

    @Override
    protected void executeUserPaginated(int page, String sort, ReposService usersService) {
        usersService.userSubscribedReposList(page, this);
    }

    @Override
    protected void executePaginatedByUsername(String username, int page, String sort, ReposService usersService) {
        usersService.userSubscribedReposList(username, page, this);
    }


}
