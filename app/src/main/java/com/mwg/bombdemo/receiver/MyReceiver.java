package com.mwg.bombdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by mwg on 2018/3/1.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (PushConstants.ACTION_MESSAGE.equals(action)) {
            try {
                //收到了服务器推送过来的内容
                String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
                Log.d("TAG:", "服务器推送过来的内容：" + message);
                JSONObject jsonObject = new JSONObject(message);

                if (jsonObject.has("tag")){
                    String tag = jsonObject.getString("tag");
                    if ("newpost".equals(tag)){
                        //有人发布了新帖子，让showAvtivity上出现红点提示
                        Intent intent2 = new Intent("COM.MWG.ACTION_NEW_POST");
                        context.sendBroadcast(intent2);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
