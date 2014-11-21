package com.colintheshots.rxretrofitmashup.models;

import com.google.gson.annotations.Expose;

/**
 * Created by colintheshots on 4/6/14.
 */
public class GistFile {

    @Expose
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
