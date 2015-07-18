package com.alorma.github.sdk.bean.issue;

import com.alorma.github.sdk.bean.dto.response.User;

import java.util.ArrayList;

/**
 * Created by Bernat on 18/07/2015.
 */
public class IssueStoryLabelList extends ArrayList<IssueLabel> implements IssueStoryDetail {

    public long created_at;
    public User user;

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public String getType() {
        return "labeled";
    }

    @Override
    public long createdAt() {
        return created_at;
    }

    @Override
    public User user() {
        return user;
    }
}
