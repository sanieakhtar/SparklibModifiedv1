package de.uni_freiburg.informatik.es.cigtrack;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.transition.TransitionManager;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import eu.senseable.sparklib.Spark;

public class MainActivity extends AppCompatActivity {

    public Spark mSpark = null;
    public Spark.Callbacks mSparkCalls = new Spark.Callbacks.Stub() {
        @Override
        public void onEventsChanged(List<Spark.Event> events) {
            super.onEventsChanged(events);

            int numcigs = events.size();
            String msg = getResources().getQuantityString(R.plurals.numcigs, numcigs, numcigs);
            TextView txt = (TextView) findViewById(R.id.text);
            txt.setText(msg);
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

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String time = sdf.format(date);
                Spark.Event ee = new Spark(context, mSparkCalls).new Event(time);
                // Spark.Event p = new Spark.Event(time);
                evs.add(numcigs,ee);
                mSpark.setEvents(evs);

                ////////////////////////////////////////////////////////////////////////////////////

                int numcigsnew = evs.size();
                String msg = getResources().getQuantityString(R.plurals.numcigs, numcigsnew, numcigsnew);
                TextView txt = (TextView) findViewById(R.id.text);
                txt.setText(msg);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    //scenes to transition
    private Scene scene1, scene2;
    //transition to move between scenes
    private Transition transition;
    //flag to swap between scenes
    private boolean start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(mRemoveEventAction);

        Button RemoveSmoke = (Button) findViewById(R.id.RemoveButton);
        RemoveSmoke.setOnClickListener(mRemoveEventAction);

        //FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        //fab2.setOnClickListener(mAddEventAction);

        Button AddSmoke = (Button) findViewById(R.id.AddButton);
        AddSmoke.setOnClickListener(mAddEventAction);

        mSpark = new Spark(this, mSparkCalls);
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

        return super.onOptionsItemSelected(item);
    }

//    //get the layout ID
//    RelativeLayout baseLayout = (RelativeLayout)findViewById(R.id.base);
//
//    //first scene
//    ViewGroup startViews = (ViewGroup)getLayoutInflater()
//            .inflate(R.layout.opening, baseLayout, false);
//
//    //second scene
//    ViewGroup endViews = (ViewGroup)getLayoutInflater()
//            .inflate(R.layout.activity_main, baseLayout, false);
//
//    //create the two scenes
//
//    scene1 = new Scene (baseLayout, startViews);
//
//    public void setScene1(Scene scene1) {
//        this.scene1 = scene1;
//    }
//
//    @Override
//    public Scene (ViewGroup sceneRoot) {
//        return super.Scene(opening);
//    }
//
//    scene2 = new Scene(baseLayout, endViews);
}