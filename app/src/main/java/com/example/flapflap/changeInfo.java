package com.example.flapflap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flapflap.javabean.User;
import com.example.flapflap.retrofit.ApiService;
import com.example.flapflap.retrofit.Constant;
import com.example.flapflap.utils.BitmapUtils;
import com.example.flapflap.utils.CameraUtils;
import com.hb.dialog.myDialog.ActionSheetDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class changeInfo extends AppCompatActivity implements View.OnClickListener {
    //权限请求
    private RxPermissions rxPermissions;
    private boolean hasPermissions = false;
    //存储拍完照后的图片
    private File outputImagePath;
    //启动相机标识
    public static final int TAKE_PHOTO = 1;
    //启动相册标识
    public static final int SELECT_PHOTO = 2;

    private View snickname, sgender, sbirth, spassword,ssign,savatar;
    private TextView nickname, gender, birth, sign;
    private Retrofit retrofit;
    private MYsqliteopenhelper mYsqliteopenhelper;
    private ImageView avatar;
    private ApiService apiService;
    int yourChoice;
    private ImageButton backButton;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_info);
        find();
        menu();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setSettingItem(snickname, "名称");
        setSettingItem(sgender, "性别");
        setSettingItem(sbirth, "生日");
        setSettingItem(spassword, "修改密码");

        snickname.setOnClickListener(this);
        sgender.setOnClickListener(this);
        sbirth.setOnClickListener(this);
        ssign.setOnClickListener(this);
        savatar.setOnClickListener(this);
        spassword.setOnClickListener(this);

        mYsqliteopenhelper = new MYsqliteopenhelper(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        apiService = retrofit.create(ApiService.class);

        // 获取用户名
        name = mYsqliteopenhelper.getName();
        Log.d("rita", "onCreate: " + name);
        if (name != null && !name.isEmpty()) {
            fetchUserIdAndInfo(name);
        } else {
            Log.e("Error", "Username is null or empty");
        }
    }

    private void menu() {
        backButton = findViewById(R.id.back_button);

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                finish(); // 返回上一页
            });
        }
        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索页面
                Intent intent = new Intent(changeInfo.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出菜单
                PopupMenu popupMenu = new PopupMenu(changeInfo.this, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_find) {
                            // 跳转到发现页面
                            Intent intent = new Intent(changeInfo.this, DiscoverActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_community) {
                            // 跳转到社区页面
                            Intent intent = new Intent(changeInfo.this, AllCommunityActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_me) {
                            // 跳转到我的页面
                            Intent intent = new Intent(changeInfo.this, MeActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void find() {
        savatar = findViewById(R.id.savatar);
        snickname = findViewById(R.id.snickname);
        sgender = findViewById(R.id.sgender);
        sbirth = findViewById(R.id.sbirth);
        ssign = findViewById(R.id.ssign);
        spassword = findViewById(R.id.spassword);
        savatar = findViewById(R.id.savatar);
        avatar = findViewById(R.id.my_savatar);
        nickname = snickname.findViewById(R.id.item_info);
        gender = sgender.findViewById(R.id.item_info);
        birth = sbirth.findViewById(R.id.item_info);
        sign = findViewById(R.id.infoSign);
    }

    public void setSettingItem(View includeView, String name){
        TextView iname = includeView.findViewById(R.id.item_name);
        iname.setText(name);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        mYsqliteopenhelper = new MYsqliteopenhelper(this);
        String myname = mYsqliteopenhelper.getName();
        if(id == R.id.snickname){
            final EditText editText = new EditText(changeInfo.this);
            editText.setText(nickname.getText());
            AlertDialog.Builder inputDialog =
                    new AlertDialog.Builder(changeInfo.this);
            inputDialog.setTitle("输入新名称").setView(editText);
            inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            inputDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(editText == null){
                                Toast.makeText(changeInfo.this,"名称不能为空",Toast.LENGTH_SHORT).show();
                            }else{
                                String input = editText.getText().toString();
                                changeNickname(myname,input);
                            }
                        }
                    }).show();
        } else if (id == R.id.sgender) {
            final String[] items = { "男","女"};
            yourChoice = -1;
            AlertDialog.Builder singleChoiceDialog =
                    new AlertDialog.Builder(changeInfo.this);
            singleChoiceDialog.setTitle("请选择");
            singleChoiceDialog.setSingleChoiceItems(items, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changeGender(myname,items[which]);
                            dialog.dismiss();
                        }
                    });
            singleChoiceDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            singleChoiceDialog.show();
        }else if(id == R.id.sbirth){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, null, 2024, 6,27);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    changeBirth(myname, i + "-" + i1 + "-" + i2);
                }
            });
            datePickerDialog.show();
        }else if(id == R.id.ssign){
            final EditText editText = new EditText(changeInfo.this);
            AlertDialog.Builder inputDialog =
                    new AlertDialog.Builder(changeInfo.this);
            inputDialog.setTitle("请输入").setView(editText);
            inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            inputDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = editText.getText().toString();
                            changeSign(myname,input);
                        }
                    }).show();
        }else if(id == R.id.savatar){
            ActionSheetDialog dialog = new ActionSheetDialog(this).builder().setTitle("请选择")
                    .addSheetItem("相册", null, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,100);
                        }
                    }).addSheetItem("拍照", null, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            Intent intent2=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent2,200);
                        }
                    });
            dialog.show();
        }else if(id == R.id.spassword){
            showDialog();
        }
    }

    protected void showDialog() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.changepassword, null);
        final EditText oldPassword = (EditText) textEntryView.findViewById(R.id.oldpassword);
        final EditText newPassword = (EditText)textEntryView.findViewById(R.id.newpassword);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(changeInfo.this);
        ad1.setTitle("修改密码");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("是", (dialog, i) ->
                changePassword(name,oldPassword.getText().toString(),newPassword.getText().toString()));
        ad1.setNegativeButton("否", (dialog, i) -> {
        });
        ad1.show();// 显示对话框

    }

    private void changePassword(String userName,String oldPassword,String newPassword){
        Call<Boolean> call = apiService.changePassword(userName,oldPassword,newPassword);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean res = response.body();
                    Log.e("rita", "res" + res);
                    Toast.makeText(changeInfo.this,"修改成功!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(changeInfo.this,"修改失败!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }
    private void fetchUserIdAndInfo(String name) {
        Call<Integer> getUserCall = apiService.getUser(name);
        getUserCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer userId = response.body();
                    Log.d("User ID", "User ID: " + userId);
                    fetchUserInfo(userId);
                } else {
                    Log.e("Error", "Failed to get user ID");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    private void fetchUserInfo(Integer userId) {
        Call<User> call = apiService.getInfo(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user1 = response.body();
                    Log.e("rita", "onCreate: " + user1);
                    runOnUiThread(() -> {
                        if (user1.getAvatar() != null && !user1.getAvatar().isEmpty()) {
                            byte[] decodedString = Base64.decode(user1.getAvatar(), Base64.NO_WRAP);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            avatar.setImageBitmap(bitmap);
                        } else {
                            Log.e("Error", "Avatar is null or empty");
                        }
                        nickname.setText(user1.getNickname());
                        gender.setText(String.valueOf(user1.getGender()));
                        birth.setText(user1.getBirth());
                        sign.setText(user1.getSign());
                    });
                } else {
                    Log.e("Error", "Failed to get user info");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }


    private void changeNickname(String name, String nnickname) {
        User user = new User();
        user.setName(name);
        user.setNickname(nnickname);
        Call<Boolean> call = apiService.changeNickname(user);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean res = response.body();
                    Log.e("rita", "res" + res);
                    Toast.makeText(changeInfo.this,"修改成功!",Toast.LENGTH_SHORT).show();
                    runOnUiThread(()->{
                        nickname.setText(nnickname);
                    });
                } else {
                    Toast.makeText(changeInfo.this,"修改失败!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    private void changeGender(String name, String ggender) {
        User user = new User();
        user.setName(name);
        user.setGender(ggender);
        Call<Boolean> call = apiService.changeGender(user);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean res = response.body();
                    Log.e("rita", "res" + res);
                    Toast.makeText(changeInfo.this,"修改成功!",Toast.LENGTH_SHORT).show();
                    runOnUiThread(()->{
                        gender.setText(ggender);
                    });
                } else {
                    Toast.makeText(changeInfo.this,"修改失败!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    private void changeBirth(String name, String bbirth) {
        User user = new User();
        user.setName(name);
        user.setBirth(bbirth);
        Call<Boolean> call = apiService.changeBirth(user);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean res = response.body();
                    Log.e("rita", "res" + res);
                    Toast.makeText(changeInfo.this,"修改成功!",Toast.LENGTH_SHORT).show();
                    runOnUiThread(()->{
                        birth.setText(bbirth);
                    });
                } else {
                    Toast.makeText(changeInfo.this,"修改失败!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    private void changeSign(String name, String ssign) {
        User user = new User();
        user.setName(name);
        user.setSign(ssign);
        Call<Boolean> call = apiService.changeSign(user);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean res = response.body();
                    Log.e("rita", "res" + res);
                    Toast.makeText(changeInfo.this,"修改成功!",Toast.LENGTH_SHORT).show();
                    runOnUiThread(()->{
                        sign.setText(ssign);
                    });
                } else {
                    Toast.makeText(changeInfo.this,"修改失败!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    private void changeAvatar(String name, String aavatar) {
        User user = new User();
        user.setName(name);
        user.setAvatar(aavatar);
        Call<Boolean> call = apiService.changeAvatar(user);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean res = response.body();
                    Log.e("rita", "res" + res);
                    Toast.makeText(changeInfo.this,"修改成功!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(changeInfo.this,"修改失败!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Error", "Network request failed", t);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getPath(Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        //高于4.4.2的版本
        if (sdkVersion >= 19) {
            Log.e("TAG", "uri auth: " + uri.getAuthority());
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(this, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(this, contentUri, selection, selectionArgs);
            } else if (isMedia(uri)) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = this.managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                return actualimagecursor.getString(actual_image_column_index);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     * uri路径查询字段
     *
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static boolean isMedia(Uri uri) {
        return "media".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());}

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mYsqliteopenhelper = new MYsqliteopenhelper(this);
        String myname = mYsqliteopenhelper.getName();

        if(requestCode==100&&resultCode==RESULT_OK&&data!=null){//系统相册
            Uri imageData = data.getData();
            String path= getPath(imageData);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap bitmap1 = BitmapUtils.zoom(bitmap, avatar.getWidth(), avatar.getHeight());
            //加载显示
            avatar.setImageBitmap(bitmap1);
            byte[] b = BitmapUtils.convertBitmapToByteArray(bitmap1);
            String changeAvatar = Base64.encodeToString(b, Base64.NO_WRAP);
            changeAvatar(myname, changeAvatar);
            //bitmap图片上传到服务器......
            //bitmap图片保存到本地
        }else if(requestCode==200&&resultCode==RESULT_OK&&data!=null){//系统相机
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = BitmapUtils.zoom(bitmap,avatar.getWidth(),avatar.getHeight());
            //加载显示
            avatar.setImageBitmap(bitmap);
            byte[] b = BitmapUtils.convertBitmapToByteArray(bitmap);
            String changeAvatar = Base64.encodeToString(b, Base64.NO_WRAP);
            changeAvatar(myname, changeAvatar);
            //bitmap图片上传到服务器......
            //bitmap图片保存到本地
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}