package com.alorma.github.sdk.bean.info;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.alorma.github.sdk.bean.dto.response.IssueState;

/**
 * Created by Bernat on 06/09/2014.
 */
public class IssueInfo implements Parcelable {

    public RepoInfo repoInfo;
    public int num;
    public int commentNum;
    public IssueState state = IssueState.open;

    public IssueInfo() {

    }

    public IssueInfo(RepoInfo repoInfo) {
        this.repoInfo = repoInfo;
    }

    protected IssueInfo(Parcel in) {
        repoInfo = (RepoInfo) in.readValue(RepoInfo.class.getClassLoader());
        num = in.readInt();
        commentNum = in.readInt();
        int stateValue = in.readInt();
        state = IssueState.fromValue(stateValue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(repoInfo);
        dest.writeInt(num);
        dest.writeInt(commentNum);
        dest.writeInt(state.value);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<IssueInfo> CREATOR = new Parcelable.Creator<IssueInfo>() {
        @Override
        public IssueInfo createFromParcel(Parcel in) {
            return new IssueInfo(in);
        }

        @Override
        public IssueInfo[] newArray(int size) {
            return new IssueInfo[size];
        }
    };

    @Override
    public String toString() {
        return repoInfo.toString() + " #" + num;
    }
}
