package com.mwg.bombdemo.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mwg.bombdemo.R;
import com.mwg.bombdemo.adapter.PostAdapter;
import com.mwg.bombdemo.app.MyApp;
import com.mwg.bombdemo.bean.MyPost;
import com.mwg.bombdemo.bean.MyUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class ShowActivity extends Activity {

    MyUser user;

    @BindView(R.id.lv_show_listview)
    ListView listView;
    List<MyPost> datas;
    PostAdapter adapter;

    @BindView(R.id.iv_show_tip)
    ImageView ivTip;

    MyPostReceiver myPostReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        user = (MyUser) getIntent().getSerializableExtra("user");
        Log.d("TAG:", "输入的用户名是：" + user.getUserName());
        ButterKnife.bind(this);
        initListView();
    }

    private void initListView() {
        datas = new ArrayList<MyPost>();
        adapter = new PostAdapter(this, datas, user);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myPostReceiver = new MyPostReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("COM.MWG.ACTION_NEW_POST");
        registerReceiver(myPostReceiver,filter);
        refresh();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myPostReceiver);
        super.onPause();
    }

    private void refresh() {

        ivTip.setVisibility(View.INVISIBLE);

        BmobQuery<MyPost> query = new BmobQuery<MyPost>();

        //查询MyUser表中保存的用户信息
        query.include("user");
        //对查询到的帖子按时间进行排序（最新发的贴在在最上面显示）
        query.order("-createdAt");

        query.findObjects(this, new FindListener<MyPost>() {
            @Override
            public void onSuccess(List<MyPost> list) {
//                //对查询到的帖子按时间进行排序（最新发的贴在在最上面显示）
//                Collections.sort(list, new Comparator<MyPost>() {
//                    @Override
//                    public int compare(MyPost o1, MyPost o2) {
//                        return 0;
//                    }
//                });

                adapter.addAll(list, true);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowActivity.this, "获取帖子列表失败，请稍后重试...", Toast.LENGTH_SHORT).show();
                Log.d("TAG:", "查询帖子失败原因：" + i + "" + s);
            }
        });
    }

    @OnClick(R.id.iv_show_add)
    public void post(View view) {
        Intent intent = new Intent(ShowActivity.this, PostActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("from","add");
        startActivity(intent);
    }

    @OnClick(R.id.iv_show_refresh)
    public void refresh(View view){
        refresh();
    }

    public class MyPostReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("COM.MWG.ACTION_NEW_POST".equals(action)){
                ivTip.setVisibility(View.VISIBLE);
                MyApp.player.start();
            }
        }
    }
}
