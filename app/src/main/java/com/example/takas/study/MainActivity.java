package com.example.takas.study;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements  OnParingListChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, MatchTableFragment.newInstance(), MatchTableFragment.TAG);
            transaction.commit();
        }
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
        if (id == R.id.select_member) {


            // DialogFragment を表示します
            SelectPlayerDialogFragment exampleDialogFragment = new SelectPlayerDialogFragment();
            exampleDialogFragment.show(getSupportFragmentManager(),
                    SelectPlayerDialogFragment.class.getSimpleName());

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onParingListChanged(List<String> data) {
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        MatchTableFragment contentFragment = (MatchTableFragment) fragmentManager.findFragmentByTag(MatchTableFragment.TAG);
        if(!(contentFragment == null
                || !contentFragment.isVisible())){
            contentFragment.makeFirstPar(true,data);
        }
    }
}
