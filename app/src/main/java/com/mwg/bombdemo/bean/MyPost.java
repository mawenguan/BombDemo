package com.mwg.bombdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by mwg on 2018/2/28.
 */

public class MyPost extends BmobObject {

    String title;
    String content;
    MyUser user;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "MyPost{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", user=" + user +
                '}';
    }
}
