package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Permissions;

/**
 * Created by Bernat on 07/09/2014.
 */
public class RepoInfo implements Parcelable {

  @SuppressWarnings("unused") public static final Parcelable.Creator<RepoInfo> CREATOR = new Parcelable.Creator<RepoInfo>() {
    @Override
    public RepoInfo createFromParcel(Parcel in) {
      return new RepoInfo(in);
    }

    @Override
    public RepoInfo[] newArray(int size) {
      return new RepoInfo[size];
    }
  };
  public String owner;
  public String name;
  public String branch;
  public Permissions permissions = new Permissions();

  public RepoInfo() {
  }

  protected RepoInfo(Parcel in) {
    owner = in.readString();
    name = in.readString();
    branch = in.readString();
    permissions = in.readParcelable(Permissions.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(owner);
    dest.writeString(name);
    dest.writeString(branch);
    dest.writeParcelable(permissions, flags);
  }

  @Override
  public String toString() {
    return owner + "/" + name;
  }
}