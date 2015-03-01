package com.alorma.github.sdk.services.notifications;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.request.LastDate;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by Bernat on 01/03/2015.
 */
public class MarkRepoNotificationsRead extends BaseClient<Response> {
	private RepoInfo repoInfo;

	public MarkRepoNotificationsRead(Context context, RepoInfo repoInfo) {
		super(context);
		this.repoInfo = repoInfo;
	}

	@Override
	protected void executeService(RestAdapter restAdapter) {
		DateTime dateTime = DateTime.now().withZone(DateTimeZone.UTC);
		String date = ISODateTimeFormat.date().print(dateTime);
		LastDate lastDate = new LastDate(date);
		restAdapter.create(NotificationsService.class).markAsReadRepo(repoInfo.owner, repoInfo.name, lastDate, this);
	}

	@Override
	public String getAcceptHeader() {
		return "application/json";
	}
	

}
