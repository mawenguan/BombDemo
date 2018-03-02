package com.mwg.bombdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mwg.bombdemo.R;
import com.mwg.bombdemo.bean.MyUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegistActivity extends Activity {

    @BindView(R.id.iv_regist_avatar)
    ImageView ivAvatar;
    @BindView(R.id.et_regist_username)
    EditText editText_username;
    @BindView(R.id.et_regist_password)
    EditText editText_password;
    @BindView(R.id.rg_regist_gender)
    RadioGroup rgGerder;

    String avatar;//用于存储头像图片在服务器中的地址
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_regist_register)
    public void regist(View view) {
        final String username = editText_username.getText().toString();
        String password = editText_password.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        MyUser myUser = new MyUser();
        myUser.setUserName(username);

        String md5 = new String(Hex.encodeHex(DigestUtils.sha(password)));
        myUser.setPassWord(md5);

        boolean gender = true;
        if (rgGerder.getCheckedRadioButtonId() == R.id.rb_regist_girl) {
            gender = false;
        }
        myUser.setGender(gender);

        myUser.setAvatar(avatar);

        //将myUser对象中的信息保存至bmob服务器上
        myUser.save(this, new SaveListener() {
            @Override
            public void onSuccess() {

                ivAvatar.setImageResource(R.mipmap.ic_launcher);
                avatar =null;
                editText_username.setText("");
                editText_password.setText("");
                rgGerder.check(R.id.rb_regist_boy);

                Toast.makeText(RegistActivity.this,
                        "注册成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                switch (i) {
                    case 401:
                        Toast.makeText(RegistActivity.this, "用户名重复", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(RegistActivity.this,
                                "注册失败！失败原因" + i + ":" + s, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 利用Intent选择器实现多种途径（图库选图，相机拍照）设定用户头像
     *
     * @param view
     */
    @OnClick(R.id.iv_regist_avatar)
    public void setAvatar(View view) {
        //开启系统图库的Intent
        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        //打开系统相机的Intent
        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
        path = file.getAbsolutePath();
        Uri path = Uri.fromFile(file);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, path);

        //创建Intent选择器
        Intent chooser = Intent.createChooser(intent1, "选择头像...");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});

        startActivityForResult(chooser, 99);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 99) {
            //处理用户选择、拍摄的图片
            final String filePath;
            if (data != null) {
                //头像是从图库选择的图片
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                        null, null, null);
                cursor.moveToNext();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                //头像是从相机拍摄的图片
                filePath = path;
            }

            //上传图片文件到Bmob服务器
            final BmobFile bf = new BmobFile(new File(filePath));
            bf.uploadblock(RegistActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    //获得头像图片在服务器中的地址
                    avatar = bf.getFileUrl(RegistActivity.this);
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    ivAvatar.setImageBitmap(bitmap);
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(RegistActivity.this, "头像上传失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "头像上传失败，错误原因" + i + ":" + s);
                }
            });
        }
    }
}
