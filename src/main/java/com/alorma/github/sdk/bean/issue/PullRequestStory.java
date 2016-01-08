package com.alorma.github.sdk.bean.issue;

import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.dto.response.PullRequest;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bernat on 07/04/2015.
 */
public class PullRequestStory implements Serializable {
  public PullRequest pullRequest;
  public List<IssueStoryDetail> details;
  public GithubStatusResponse statusResponse;
}
