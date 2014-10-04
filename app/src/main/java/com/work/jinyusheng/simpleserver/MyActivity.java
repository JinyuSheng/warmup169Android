package com.work.jinyusheng.simpleserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class MyActivity extends Activity {
    private static Drawable bg;
    private Button loginBut;
    private Button addBut;
    private EditText username;
    private EditText password;
    private TextView errorText;
    private static boolean bgset;
    private Button changeBut;
    private static String bgpicpath;
    private static final String ServerUrl = "http://secret-woodland-4532.herokuapp.com/users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        this.loginBut = (Button) this.findViewById(R.id.loginBut);
        this.addBut = (Button) this.findViewById(R.id.addBut);
        this.username = (EditText) this.findViewById(R.id.usernameEdit);
        this.password = (EditText) this.findViewById(R.id.pwEdit);
        this.errorText = (TextView) this.findViewById(R.id.errorText);
        this.changeBut = (Button) this.findViewById(R.id.changeBut);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
//        if(isConnected()){
//            Toast.makeText(getApplicationContext(),"Network problem", Toast.LENGTH_LONG).show();
//        }
//        else{
//            Toast.makeText(getApplicationContext(), "Ready to connect", Toast.LENGTH_LONG).show();
//        }
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.myact);
        if(bgset){
            relativelayout.setBackground(bg);
        }else{
            bg = getResources().getDrawable( R.drawable.starbg);
            bgpicpath = "";
        }
        relativelayout.setBackground(bg);
        this.addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userString = username.getText().toString();
                String pwString = password.getText().toString();
                if(userString.matches("")){
                    errorText.setText("Please enter a username");
                    return;
                }
                Intent loginIntent = new Intent();
                loginIntent.putExtra("bgpicpath", bgpicpath);
                loginIntent.putExtra("username", userString);
                loginIntent.setClass(MyActivity.this, LoggedActivity.class);
                //Send this to the server
                if(post(ServerUrl + "/add", userString, pwString, loginIntent)){
                    startActivity(loginIntent);
                }else{
                    int errCode = loginIntent.getIntExtra("errCode", 0);
                    if(errCode == 0){
                        errorText.setText("");
                        Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                    }else{
                        Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vi.vibrate(new long[]{100,300}, -1);
                        errorText.setText(errCodeToMsg(errCode) + "\nError Code = " + errCode);
                    }
                }
            }
        });

        this.loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected()){
                    Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_LONG);
                }
                String userString = username.getText().toString();
                String pwString = password.getText().toString();
                if(userString.matches("")){
                    errorText.setText("Please enter a username");
                    return;
                }
                //Send this to the server
                Intent loginIntent = new Intent();
                loginIntent.putExtra("username", userString);
                loginIntent.putExtra("bgpicpath", bgpicpath);
                loginIntent.setClass(MyActivity.this, LoggedActivity.class);
                if(post(ServerUrl + "/login", userString, pwString, loginIntent)){
                    startActivity(loginIntent);
                }else{
                    int errCode = loginIntent.getIntExtra("errCode", 0);
                    if(errCode == 0){
                        errorText.setText("");
                        Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();

                    }else{
                        Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vi.vibrate(new long[]{100,300}, -1);
                        errorText.setText(errCodeToMsg(errCode) + "\nError Code = " + errCode);
                    }
                }
            }
        });

        this.changeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

//            String picturePath contains the path of selected Image
            RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.myact);
            Drawable d = Drawable.createFromPath(picturePath);
            relativelayout.setBackground(d);
            this.bg = d;
            this.bgset = true;
            this.bgpicpath = picturePath;
//            Toast.makeText(getApplicationContext(), Drawable.createFromPath(picturePath).toString(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  boolean  post(String url, String username, String password, Intent intent){
        boolean success =  false;
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("user", username);
            jsonObject.accumulate("password", password);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. Grab the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
            String jsonResponse = reader.readLine();
            JSONObject jsonObjResponse = new JSONObject(jsonResponse);
            int errCode = jsonObjResponse.optInt("errCode");
            int count = jsonObjResponse.optInt("count");
//            errorText.setText("errcode is " + errCode + "count is " + count);
            intent.putExtra("errCode", errCode);
            intent.putExtra("count", count);
            if(errCode == 1){
                success = true;
            }
        } catch (Exception e) {
//            errorText.setText(e.toString());
        }
        // 11. return true if errCode == 1
        return success;

    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public String errCodeToMsg(int errCode){
        switch (errCode){
            case 1:
                return "Success";
            case -1:
                return "Cannot find the user/password pair in the database";
            case -2:
                return "User already exists";
            case -3:
                return "Invalid username: username must be at most 128 characters long";
            case -4:
                return "Invalid password: password must be at most 128 characters long";
            default:
                return "Network connection failed";

        }
    }
}
