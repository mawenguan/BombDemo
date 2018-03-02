package com.mwg.bombdemo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a.a.V;
import com.mwg.bombdemo.R;
import com.mwg.bombdemo.bean.MyPost;
import com.mwg.bombdemo.bean.MyUser;
import com.mwg.bombdemo.ui.PostActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.DeleteListener;

/**
 * Created by mwg on 2018/3/1.
 */

public class PostAdapter extends BaseAdapter {

    Context context;
    List<MyPost> datas;
    LayoutInflater inflater;

    MyUser currentUser;//当前登录用户

    public PostAdapter(Context context, List<MyPost> datas, MyUser currentUser) {
        this.context = context;
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
        this.currentUser = currentUser;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public MyPost getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_post_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MyPost myPost = getItem(position);
        MyUser user = myPost.getUser();

        String avatar = user.getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            //用户未设置头像,用系统默认的图片
            viewHolder.ivAvatar.setImageResource(R.mipmap.ic_launcher);
        } else {
            //加载用户存在服务器端的头像
            Picasso.with(context).load(user.getAvatar()).into(viewHolder.ivAvatar);
        }

        viewHolder.tvUsername.setText(user.getUserName());
        viewHolder.tvTitle.setText(myPost.getTitle());
        viewHolder.tvPosttime.setText(myPost.getCreatedAt());
        viewHolder.tvContent.setText(myPost.getContent());

        //判断帖子作者和当前登录用户是否为同一人
        if (currentUser.getUserName().equals(user.getUserName())) {
            viewHolder.tvDelete.setVisibility(View.VISIBLE);
            viewHolder.tvUpdate.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvDelete.setVisibility(View.INVISIBLE);
            viewHolder.tvUpdate.setVisibility(View.INVISIBLE);
        }

        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击删除该帖子,弹出提示框，请用户确认后再删除
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("确认删除");
                builder.setIcon(android.R.drawable.ic_menu_info_details);
                builder.setMessage("您确认要删除该帖子吗？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //方法一：

//                        //仅从服务器上删除数据
//                        myPost.delete(context, new DeleteListener() {
//                            @Override
//                            public void onSuccess() {
//                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
//                                //如果有本地缓存，且缓存数据被优先加载，应从缓存中也将数据删除
//
//                                //从数据源中删除MyPost
//                                remove(myPost);
//                            }
//
//                            @Override
//                            public void onFailure(int i, String s) {
//                                Toast.makeText(context, "删除失败，请稍后再试...", Toast.LENGTH_SHORT).show();
//                            }
//                        });

                        //方法二：

//                        MyPost newPost = new MyPost();
//                        newPost.setObjectId(myPost.getObjectId());
//                        newPost.delete(context, new DeleteListener() {
//                            @Override
//                            public void onSuccess() {
//                                remove(myPost);
//                            }
//
//                            @Override
//                            public void onFailure(int i, String s) {
//
//                            }
//                        });

                        //方法三：
                        MyPost newPost = new MyPost();
                        newPost.delete(context, myPost.getObjectId(), new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                remove(myPost);
                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("取消删除", null);
                builder.create().show();
            }
        });

        viewHolder.tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击更新该帖子
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("from","update");
                intent.putExtra("post",myPost);

                context.startActivity(intent);
            }
        });

        return convertView;
    }

    /**
     * 增加全部数据
     *
     * @param list
     * @param isClear
     */
    public void addAll(List<MyPost> list, boolean isClear) {
        if (isClear) {
            datas.clear();
        }
        datas.addAll(list);
        notifyDataSetChanged();
    }

    /*
     * 删一个数据
     */
    public void remove(MyPost post) {
        datas.remove(post);
        notifyDataSetChanged();
    }

    public class ViewHolder {

        @BindView(R.id.iv_item_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_item_username)
        TextView tvUsername;
        @BindView(R.id.tv_item_title)
        TextView tvTitle;
        @BindView(R.id.tv_item_posttime)
        TextView tvPosttime;
        @BindView(R.id.tv_item_content)
        TextView tvContent;

        @BindView(R.id.tv_item_update)
        TextView tvUpdate;
        @BindView(R.id.tv_item_delete)
        TextView tvDelete;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
