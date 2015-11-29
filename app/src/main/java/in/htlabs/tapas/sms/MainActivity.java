package in.htlabs.tapas.sms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener{
    Button am_btn_cre_timetable,am_btn_view_timetable;
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         cd = new ConnectionDetector(getApplicationContext());

        am_btn_cre_timetable=(Button)findViewById(R.id.am_btn_cre_timetable);
        am_btn_cre_timetable.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.am_btn_cre_timetable:
                Intent i = new Intent(MainActivity.this, CourseSection.class);
                startActivity(i);
                break;
        }
    }

}
