package com.alorma.github.sdk.services.issues.story;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.bean.info.RelType;
import com.alorma.github.sdk.bean.issue.IssueEvent;
import com.alorma.github.sdk.bean.issue.IssueStoryComparators;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryLabelList;
import com.alorma.github.sdk.bean.issue.IssueStoryUnlabelList;
import com.alorma.github.sdk.bean.issue.ListIssueEvents;
import com.alorma.github.sdk.services.client.GithubClient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by Bernat on 07/04/2015.
 */
public class IssueStoryLoader extends GithubClient<IssueStory> {

    private final IssueInfo issueInfo;
    private List<IssueStoryDetail> storyDetailMap;
    private IssueStory issueStory;

    public IssueStoryLoader(Context context, IssueInfo info) {
        super(context);
        this.issueInfo = info;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {

        IssueStoryService issueStoryService = restAdapter.create(IssueStoryService.class);

        issueStory = new IssueStory();
        storyDetailMap = new ArrayList<>();

        new IssueCallback(issueInfo, issueStoryService).execute();
    }

    @Override
    protected IssueStory executeServiceSync(RestAdapter restAdapter) {
        issueStory = new IssueStory();
        storyDetailMap = new ArrayList<>();


        IssueStoryService issueStoryService = restAdapter.create(IssueStoryService.class);
        issueStory.issue = issueStoryService.detail(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num);

        addComments(issueStoryService.comments(issueInfo.repoInfo.owner,
                issueInfo.repoInfo.name, issueInfo.num, 1));

        for (int i = nextPage; i < lastPage; i++)
            addComments(issueStoryService.comments(issueInfo.repoInfo.owner,
                    issueInfo.repoInfo.name, issueInfo.num, i));

        addEvents(issueStoryService.events(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num, 1));

        for (int i = nextPage; i < lastPage; i++)
            addEvents(issueStoryService.events(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num, i));

        return parseIssueStoryDetails();
    }

    private IssueStory parseIssueStoryDetails() {
        Collections.sort(storyDetailMap, IssueStoryComparators.ISSUE_STORY_DETAIL_COMPARATOR);
        issueStory.details = storyDetailMap;

        if (getOnResultCallback() != null) {
            getOnResultCallback().onResponseOk(issueStory, null);
        }
        return issueStory;
    }

    private class IssueCallback extends BaseInfiniteCallback<Issue> {

        private final IssueInfo info;
        private final IssueStoryService issueStoryService;

        public IssueCallback(IssueInfo info, IssueStoryService issueStoryService) {
            this.info = info;
            this.issueStoryService = issueStoryService;
        }

        @Override
        public void execute() {
            issueStoryService.detail(info.repoInfo.owner, info.repoInfo.name, info.num, this);
        }

        @Override
        protected void executePaginated(int nextPage) {

        }

        @Override
        protected void executeNext() {
            new IssueCommentsCallback(info, issueStoryService).execute();
        }

        @Override
        protected void response(Issue issue) {
            issueStory.issue = issue;
        }

    }

    private class IssueCommentsCallback extends BaseInfiniteCallback<List<GithubComment>> {

        private final IssueInfo info;
        private final IssueStoryService issueStoryService;

        public IssueCommentsCallback(IssueInfo info, IssueStoryService issueStoryService) {
            this.info = info;
            this.issueStoryService = issueStoryService;
        }


        @Override
        public void execute() {
            issueStoryService.comments(info.repoInfo.owner, info.repoInfo.name, info.num, this);
        }

        @Override
        protected void executePaginated(int nextPage) {
            issueStoryService.comments(info.repoInfo.owner, info.repoInfo.name, info.num, nextPage, this);
        }

        @Override
        protected void executeNext() {
            new IssueEventsCallbacks(info, issueStoryService).execute();
        }

        @Override
        protected void response(List<GithubComment> issueComments) {
            for (GithubComment comment : issueComments) {
                long time = getMilisFromDateClearDay(comment.created_at);
                IssueStoryComment detail = new IssueStoryComment(comment);
                detail.created_at = time;
                storyDetailMap.add(detail);
            }
        }
    }

    private class IssueEventsCallbacks extends BaseInfiniteCallback<List<IssueEvent>> {

        private IssueInfo info;
        private IssueStoryService issueStoryService;

        public IssueEventsCallbacks(IssueInfo info, IssueStoryService issueStoryService) {
            this.info = info;
            this.issueStoryService = issueStoryService;
        }

        @Override
        public void execute() {
            issueStoryService.events(info.repoInfo.owner, info.repoInfo.name, info.num, this);
        }

        @Override
        protected void executePaginated(int nextPage) {
            issueStoryService.events(info.repoInfo.owner, info.repoInfo.name, info.num, nextPage, this);
        }

        @Override
        protected void executeNext() {
            parseIssueStoryDetails();
        }

        @Override
        protected void response(List<IssueEvent> issueEvents) {
            addEvents(issueEvents);
        }
    }

    private void addEvents(List<IssueEvent> issueEvents){
        List<IssueStoryDetail> details = new ArrayList<>();

        Map<Long, IssueStoryLabelList> labeledEvents = new HashMap<>();
        Map<Long, IssueStoryUnlabelList> unlabeledEvents = new HashMap<>();
        for (IssueEvent event : issueEvents) {
            String createdAt = event.created_at;
            long time = getMilisFromDateClearDay(createdAt);
            if (event.event.equals("labeled")) {
                if (labeledEvents.get(time) == null) {
                    labeledEvents.put(time, new IssueStoryLabelList());
                    labeledEvents.get(time).created_at = time;
                    labeledEvents.get(time).user = event.actor;
                }
                labeledEvents.get(time).add(event.label);
            } else if (event.event.equals("unlabeled")) {
                if (unlabeledEvents.get(time) == null) {
                    unlabeledEvents.put(time, new IssueStoryUnlabelList());
                    unlabeledEvents.get(time).created_at = time;
                    unlabeledEvents.get(time).user = event.actor;
                }
                unlabeledEvents.get(time).add(event.label);
            } else {
                IssueStoryEvent issueStoryEvent = new IssueStoryEvent(event);
                if (validEvent(issueStoryEvent.getType())) {
                    issueStoryEvent.created_at = time;
                    details.add(issueStoryEvent);
                }
            }
        }

        for (Long aLong : labeledEvents.keySet()) {
            IssueStoryLabelList issueLabels = labeledEvents.get(aLong);
            issueLabels.created_at = aLong;
            details.add(issueLabels);
        }

        for (Long aLong : unlabeledEvents.keySet()) {
            IssueStoryUnlabelList issueLabels = unlabeledEvents.get(aLong);
            issueLabels.created_at = aLong;
            details.add(issueLabels);
        }
        storyDetailMap.addAll(details);
    }

    private void addComments(List<GithubComment> issueComments){
        for (GithubComment comment : issueComments) {
            long time = getMilisFromDateClearDay(comment.created_at);
            IssueStoryComment detail = new IssueStoryComment(comment);
            detail.created_at = time;
            storyDetailMap.add(detail);
        }
    }

    private long getMilisFromDateClearDay(String createdAt) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DateTime dt = formatter.parseDateTime(createdAt);

        return dt.minuteOfDay().roundFloorCopy().getMillis();
    }

    private boolean validEvent(String event) {
        return !(event.equals("mentioned") ||
                event.equals("subscribed") ||
                event.equals("unsubscribed"));
    }

    @Override
    public void log(String message) {
        Log.i("IssueStoryLoader", message);
    }

    @Override
    public String getAcceptHeader() {
        return "application/vnd.github.v3.full+json";
    }
}
