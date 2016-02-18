package com.alorma.github.sdk.services.login;

import android.util.Base64;
import com.alorma.github.sdk.bean.dto.request.CreateAuthorization;
import com.alorma.github.sdk.bean.dto.response.GithubAuthorization;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.user.TwoFactorAuthException;
import com.alorma.github.sdk.services.user.UnauthorizedException;
import java.util.List;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Observable;

public class CreateAuthorizationClient extends GithubClient<GithubAuthorization> {

  private final String username;
  private final String password;
  private String otpCode;
  private CreateAuthorization createAuthorization;

  public CreateAuthorizationClient(String username, String password, CreateAuthorization createAuthorization) {
    super();

    this.username = username;
    this.password = password;
    this.createAuthorization = createAuthorization;
  }

  @Override
  public void intercept(RequestFacade request) {
    super.intercept(request);

    if (username != null && password != null) {
      String userCredentials = username + ":" + password;
      String basicAuth =
          "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));

      request.addHeader("Authorization", basicAuth);

      if (otpCode != null) {
        request.addHeader("X-GitHub-OTP", otpCode);
      }
    }
  }

  @Override
  protected Observable<GithubAuthorization> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(LoginService.class)
        .createAuthorization(createAuthorization)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RetrofitError) {
            Response response = ((RetrofitError) throwable).getResponse();
            if (response != null && response.getStatus() == 401) {
              List<Header> headers = response.getHeaders();
              if (headers != null) {
                for (Header header : headers) {
                  if (header.getName().equals("X-GitHub-OTP") && header.getValue()
                      .contains("required")) {
                    return Observable.error(new TwoFactorAuthException());
                  }
                }
                return Observable.error(new UnauthorizedException());
              }
            }
          }
          return Observable.error(throwable);
        });
  }

  public void setOtpCode(String otpCode) {
    this.otpCode = otpCode;
  }
}
