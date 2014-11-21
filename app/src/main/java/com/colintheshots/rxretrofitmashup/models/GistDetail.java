package com.colintheshots.rxretrofitmashup.models;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by colintheshots on 4/6/14.
 */
public class GistDetail {

    @Expose
    private Map<String, GistFile> files;

    public Map<String, GistFile> getFiles() {
        return files;
    }

    public void setFiles(Map<String, GistFile> files) {
        this.files = files;
    }
}
