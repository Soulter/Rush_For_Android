package com.soulter.replygf;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static String serIpStr;
    public static String serPortStr;
    public static String serIdStr;
    private Button rush_button;
    public static boolean isLink = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.v("tag","hhhh");
        rush_button = findViewById(R.id.rush_button);









        rush_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BufferedWriter bwID=new BufferedWriter(new OutputStreamWriter(MainActivity.socket.getOutputStream()));
                                bwID.write("rush "+(new Date().getTime()-deltaT));
                                bwID.newLine();
                                bwID.flush();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();


            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        serIdStr = prefs.getString("user_id","null");
        serIpStr = prefs.getString("server_ip","null");
        serPortStr = prefs.getString("server_port","null");

        infoIsNotBeFilled();


//        setTitle("Rush - 已连接");



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.link){
            if (!infoIsNotBeFilled()){
                    Log.v("tag","link start!");
                    init();
                 rush_button.setText("抢答"+"\n编号:"+serIdStr);


            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean infoIsNotBeFilled(){
        if (serIdStr == "null" || serIpStr == "null" || serPortStr == "null"){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("提示").setMessage("似乎还没有配置网络，请前往设置~")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(intent);
                        }
                    }).setCancelable(false);
            builder.create().show();

            return true;

        }
        return false;

    }

    public void init(){




//
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//        serIdStr = prefs.getString("user_id","null");
//        serIpStr = prefs.getString("server_ip","null");
//        serPortStr = prefs.getString("server_port","null");

        if (serIpStr != null && serPortStr != null && serIdStr!= null){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        if(conned){
                            Toast.makeText(MainActivity.this,String.valueOf(conned),Toast.LENGTH_LONG).show();
                            return;
                        }


                        MainActivity.socket=new Socket(MainActivity.serIpStr,Integer.parseInt(MainActivity.serPortStr));
                        conned=true;

                        BufferedWriter bwID=new BufferedWriter(new OutputStreamWriter(MainActivity.socket.getOutputStream()));
                        long t0=new Date().getTime();
                        bwID.write("id "+serIdStr);
                        bwID.newLine();
                        bwID.flush();

                        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(MainActivity.socket.getInputStream()));
                        String msg[]=bufferedReader.readLine().split(" ");
                        long t1=new Date().getTime();
                        long ts=Long.parseLong(msg[1]);
                        deltaT=t1-(ts+(t1-t0)/2);

;
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            }).start();
        }



    }





    public static Socket socket = null;
    public static long deltaT=0;
    public static boolean conned=false;


    @Override
    protected void onResume() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        serIdStr = prefs.getString("user_id","null");
        serIpStr = prefs.getString("server_ip","null");
        serPortStr = prefs.getString("server_port","null");
        super.onResume();
    }

}
