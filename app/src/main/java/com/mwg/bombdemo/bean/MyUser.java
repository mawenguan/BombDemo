package com.mwg.bombdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * 一个对象要能上传至Bmob服务器，需满足：
 * 1、该对象必须继承自BmobObject；
 * 2、该对象的属性必须只能是:
 *    1)String，String[]，list<String>
 *    2)八大基本数据类型的包装类型
 *    3)其他BmobSDK中包含的数据类型
 *
 * Created by mwg on 2018/2/26.
 */

public class MyUser extends BmobObject{

    String userName;
    String passWord;
    Boolean gender;
    String avatar;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", gender=" + gender +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
