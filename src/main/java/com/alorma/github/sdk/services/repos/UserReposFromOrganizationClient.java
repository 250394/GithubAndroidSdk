package com.alorma.github.sdk.services.repos;

public class UserReposFromOrganizationClient extends GithubReposClient {

  public UserReposFromOrganizationClient() {
    super();
  }

  public UserReposFromOrganizationClient(String username, String sort) {
    super(username, sort);
  }

  public UserReposFromOrganizationClient(String username, String sort, int page) {
    super(username, sort, page);
  }

  @Override
  protected void executeUserFirstPage(String sort, ReposService usersService,
      ApiSubscriber apiSubscriber) {
    usersService.userReposListFromOrgs(sort, apiSubscriber);
  }

  @Override
  protected void executeFirstPageByUsername(String username, String sort, ReposService usersService,
      ApiSubscriber apiSubscriber) {

  }

  @Override
  protected void executeUserPaginated(int page, String sort, ReposService usersService,
      ApiSubscriber apiSubscriber) {
    usersService.userReposListFromOrgs(page, sort, apiSubscriber);
  }

  @Override
  protected void executePaginatedByUsername(String username, int page, String sort,
      ReposService usersService, ApiSubscriber apiSubscriber) {

  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.moondragon+json";
  }
}
