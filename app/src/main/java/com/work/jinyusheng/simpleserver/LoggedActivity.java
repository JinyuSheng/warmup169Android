package com.work.jinyusheng.simpleserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class LoggedActivity extends Activity {

    private Button logoutBut;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        this.logoutBut = (Button) this.findViewById(R.id.logoutBut);
        this.welcomeText = (TextView) this.findViewById(R.id.WelcomeLoggedText);
        this.logoutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.setClass(LoggedActivity.this, MyActivity.class);
                startActivity(returnIntent);
            }

        });
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        Intent myIntent = getIntent();
        String picpath = myIntent.getStringExtra("bgpicpath");
        RelativeLayout mylayout = (RelativeLayout) findViewById(R.id.actlog);
        Toast.makeText(getApplicationContext(), "picpath" + picpath, Toast.LENGTH_LONG).show();

        if(! picpath.matches("") ){
            mylayout.setBackground(Drawable.createFromPath(picpath));
        }else{
            Toast.makeText(getApplicationContext(), "picpath" + picpath, Toast.LENGTH_LONG);
            mylayout.setBackground(getResources().getDrawable(R.drawable.starbg));
        }
        int count = myIntent.getIntExtra("count", 1000);
        String times;
        if(count == 1){
            times = "once.";
        }else if(count == 2){
            times = "twice.";
        }else{
            times = count + " times";
        }
        welcomeText.setText("Welcome " + myIntent.getStringExtra("username") + "\nYou have logged in " + times);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logged, menu);
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
}
