package de.uni_freiburg.informatik.es.cigtrack;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.senseable.sparklib.Spark;

import static de.uni_freiburg.informatik.es.cigtrack.R.drawable.foxyemoji2;
import static de.uni_freiburg.informatik.es.cigtrack.R.drawable.foxyemoji31;

public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    UserData myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button RemoveSmoke = (Button) findViewById(R.id.RemoveButton);
        RemoveSmoke.setOnClickListener(mRemoveEventAction);

        Button AddSmoke = (Button) findViewById(R.id.AddButton);
        AddSmoke.setOnClickListener(mAddEventAction);

        Intent intro = new Intent(this, IntroActivity.class);
        startActivity(intro);
        updateWelcome();

        mSpark = new Spark(this, mSparkCalls);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateWelcome();
    }

    // Make window fullscreen when opened
    //TODO: make entire layout so that it's fullscreen when it opens and no bar is seen at the top initially

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public Spark mSpark = null;
    public int last_numcigs;

    public Spark.Callbacks mSparkCalls = new Spark.Callbacks.Stub() {
        @Override
        public void onEventsChanged(List<Spark.Event> events) {
            super.onEventsChanged(events);

            prefs = getSharedPreferences("de.uni_freiburg.informatik.es.cigtrack", MODE_PRIVATE);

            int numcigs = events.size();
            String msg = getResources().getQuantityString(R.plurals.numcigs, numcigs, numcigs);
            TextView txt = (TextView) findViewById(R.id.cigText);
            txt.setText(msg);

            /*
            * Generate a Pup-up Dialog to disturb the user.
            */

            ///// TODO: last_numcigs should be read from the DB and not from the callback function of
            ///// TODO: event creation. Maybe move this to another function?

            if (numcigs > last_numcigs && last_numcigs != 0) {
                ingnitionPopup();
            }

            if (prefs.getBoolean("firstrun", true)) {
                // Do first run stuff here then set 'firstrun' as false
                // using the following line to edit/commit prefs
                prefs.edit().putBoolean("firstrun", false).commit();
            }
            else {
                last_numcigs = numcigs;
                updateImage(last_numcigs);
            }
        }
    };

    private View.OnClickListener mRemoveEventAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                List<Spark.Event> evs = mSpark.getEvents();
                if (evs.size() == 0)
                    return;

                evs.remove(evs.size() - 1);
                mSpark.setEvents(evs);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener mAddEventAction = new View.OnClickListener() {
        @Override
        public void onClick(View b) {
            try {
                List<Spark.Event> evs = mSpark.getEvents();

                int numcigs = evs.size();
                //Spark.Event e = evs.get(0);
                //evs.add(numcigs,e);
                //mSpark.setEvents(evs);

                ////////////////////////////////////////////////////////////////////////////////////
                final Context context = getApplicationContext();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
                Date date = new Date();
                String time = sdf.format(date);
                Spark.Event ee = new Spark(context, mSparkCalls).new Event(time);
                // Spark.Event p = new Spark.Event(time);
                evs.add(numcigs,ee);
                mSpark.setEvents(evs);

                int numcigsnew = evs.size();
                String msg = getResources().getQuantityString(R.plurals.numcigs, numcigsnew, numcigsnew);
                TextView txt = (TextView) findViewById(R.id.cigText);
                txt.setText(msg);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public String updatePetName(){
        UserData u = new UserData(this);
        String petname = u.readPetname();
        return petname;
    }

    public String updateUserName(){
        UserData u = new UserData(this);
        String username = u.readUsername();
        return username;
    }

    public void updateWelcome(){
        String username = updateUserName();
        String petname = updatePetName();

        TextView txt_head = (TextView) findViewById(R.id.headerText);
        String message = getResources().getString(R.string.app_main,username,petname);
        txt_head.setText(message);
    }

    public void updateImage(int cigCheck){
        ImageView mainImage = (ImageView) findViewById(R.id.imageMain);
        myDb = new UserData(this);
        Cursor result = myDb.readData();
        result.moveToFirst();
        int avgcigs = result.getInt(5);
        if(avgcigs > 0) {
            if (cigCheck > avgcigs) {
                mainImage.setImageResource(foxyemoji31);
            } else
                mainImage.setImageResource(foxyemoji2);
        }
        else
            mainImage.setImageResource(foxyemoji2);
    }

    public void ingnitionPopup() {

        /*
        * Generate a Pup-up Dialog to disturb the user.
        */

        final Dialog dialog_popup = new Dialog(MainActivity.this);
        dialog_popup.setTitle("Ignition Detected!");
        dialog_popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_popup.setContentView(R.layout.dialog_smokedetected);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        dialog_popup.show();

    }

    public void resumePopup() {

        /*
        * Generate a Pup-up Dialog to show time since last cigarette.
        */

        // List<Spark.Event> evs = mSpark.getEvents();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_launchspark) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("eu.senseable.companion");
            if (launchIntent != null) {
                startActivity(launchIntent); //null pointer check in case package name was not found
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
