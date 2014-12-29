package com.alorma.github.sdk.services.client;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.security.UnAuthIntent;

import java.net.Proxy;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public abstract class BaseClient<K> implements Callback<K>, RequestInterceptor, RestAdapter.Log {

	private final StoreCredentials storeCredentials;

	protected final Context context;
	private OnResultCallback<K> onResultCallback;
	protected Handler handler;

	public BaseClient(Context context) {
		this.context = context.getApplicationContext();
		storeCredentials = new StoreCredentials(context);
		handler = new Handler();
	}

	public void execute() {

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setClient(new OkClient())
				.setEndpoint(ApiConstants.API_URL)
				.setRequestInterceptor(this)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				.setLog(this)
				.build();

		executeService(restAdapter);
	}

	protected abstract void executeService(RestAdapter restAdapter);

	@Override
	public void success(final K k, final Response response) {
		if (handler != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					sendResponse(k, response);
				}
			});
		} else {
			sendResponse(k, response);
		}
	}

	private void sendResponse(K k, Response response) {
		if (onResultCallback != null) {
			onResultCallback.onResponseOk(k, response);
		}
	}

	@Override
	public void failure(final RetrofitError error) {
		if (handler != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					sendError(error);
				}
			});
		} else {
			sendError(error);
		}
	}

	private void sendError(RetrofitError error) {
		if (error.getResponse() != null && error.getResponse().getStatus() == 401) {
			storeCredentials.clear();
			LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
			manager.sendBroadcast(new UnAuthIntent());
		} else {
			if (onResultCallback != null) {
				onResultCallback.onFail(error);
			}
		}
	}

	public OnResultCallback<K> getOnResultCallback() {
		return onResultCallback;
	}

	public void setOnResultCallback(OnResultCallback<K> onResultCallback) {
		this.onResultCallback = onResultCallback;
	}

	@Override
	public void intercept(RequestFacade request) {
		request.addHeader("Accept", getAcceptHeader());
		request.addHeader("Authorization", "token " + storeCredentials.token());
	}

	@Override
	public void log(String message) {
		Log.v("RETROFIT_LOG", message);
	}

	public String getAcceptHeader() {
		return "application/vnd.github.v3.full+json";
	}

	public Context getContext() {
		return context;
	}
	
	public interface OnResultCallback<K> {
		void onResponseOk(K k, Response r);

		void onFail(RetrofitError error);
	}
}
