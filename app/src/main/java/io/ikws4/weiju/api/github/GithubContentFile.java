package io.ikws4.weiju.api.github;

import android.util.Base64;

import io.ikws4.weiju.util.Logger;

public class GithubContentFile {
    private String encoding;
    private String content;

    private GithubContentFile() {}

    public String getContent() {
        if (encoding.equals("base64")) {
            return new String(Base64.decode(content, Base64.DEFAULT));
        } else {
            Logger.e("Unexpected encoding", encoding);
            return "";
        }
    }
}
