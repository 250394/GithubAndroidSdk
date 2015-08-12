package com.alorma.github.sdk.services.orgs;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.GithubUsersClient;
import com.alorma.github.sdk.services.user.UsersService;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 22/02/2015.
 */
public class OrgsMembersClient extends GithubUsersClient<List<User>> {

	private final String org;
	private int page = 0;

	public OrgsMembersClient(Context context, String org) {
		super(context);
		this.org = org;
	}

	public OrgsMembersClient(Context context, String org, int page) {
		super(context);
		this.org = org;
		this.page = page;
	}

	@Override
	protected void executeService(UsersService usersService) {
		if (page == 0) {
			usersService.orgMembers(org, this);
		} else {
			usersService.orgMembers(org, page, this);
		}
	}

	@Override
	protected List<User> executeServiceSync(UsersService usersService) {
			if (page == 0) {
				return usersService.orgMembers(org);
			} else {
				return usersService.orgMembers(org, page);
			}
	}
}
