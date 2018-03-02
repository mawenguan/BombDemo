package com.mwg.bombdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mwg.bombdemo.R;
import com.mwg.bombdemo.bean.MyUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

public class MainActivity extends Activity {

    @BindView(R.id.et_main_username)
    EditText etUsername;
    @BindView(R.id.et_main_password)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_main_login)
    public void login(View view) {
        //分别拿到用户用户所输入的用户名和密码
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入完整的用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        //以用户输入的用户名作为查询条件，查询Bmob服务器MyUser数据表中的内容
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        //query.addWhereEqualTo(数据表的列名,用户输入的用户名)
        query.addWhereEqualTo("userName", username);
        //发起查询
        query.findObjects(this, new FindListener<MyUser>() {
            //服务器给了你正常的响应
            @Override
            public void onSuccess(List<MyUser> list) {
                //根据List中是否有元素来判断查询的结果
                if (list.size() > 0) {
                    //数据表中有一条数据记录，其userName字段值为用户输入的用户名
                    MyUser myUser = list.get(0);
                    String md5 = new String(Hex.encodeHex(DigestUtils.sha(password)));
                    if (myUser.getPassWord().equals(md5)) {
                        //将user登录成功的消息告诉给所有客户端
                        BmobPushManager<BmobInstallation> manager =
                                new BmobPushManager<BmobInstallation>(MainActivity.this);
                        manager.pushMessageAll(myUser.getUserName() + "登录啦", new PushListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG:", "发布登录消息");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.d("TAG:", "登录消息发布失败：" + i + ":" + s);
                            }
                        });

                        //登录成功！
                        Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                        intent.putExtra("user", myUser);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                }


            }

            //服务器不能正常响应/未响应
            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this, "服务器繁忙！", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @OnClick(R.id.btn_main_register)
    public void jump(View view) {
        Intent intent = new Intent(MainActivity.this, RegistActivity.class);
        startActivity(intent);
    }
}
