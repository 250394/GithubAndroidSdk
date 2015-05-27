package com.alorma.github.sdk.bean.dto.response;

public class Commit extends ShaUrl{

	private static final int MAX_COMMIT_LENGHT = 80;

    public Commit commit;
    public User author;
    public ListShaUrl parents;
	public GitChangeStatus stats;
    public User committer;
	public String message;
	public boolean distinct;
	public GitCommitFiles files;
	public int days;

	@Override
	public String toString() {
		return "[" + sha + "] " + commit.message;
	}

	public String shortMessage() {
		if (message != null) {
			int start = 0;
			int end = Math.min(MAX_COMMIT_LENGHT, message.length());

			return message.substring(start, end);
		}
		return null;
	}
}