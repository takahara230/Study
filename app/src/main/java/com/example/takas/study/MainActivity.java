package com.example.takas.study;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

//            transaction.add(R.id.container, MainFragment.createInstance("hoge", Color.RED));
//            transaction.add(R.id.container, MainFragment.createInstance("fuga", Color.BLUE));
            transaction.replace(R.id.container, IndexPairListFragment.newInstance());


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
        if (id == R.id.action_settings) {


            // DialogFragment を表示します
            ExampleDialogFragment exampleDialogFragment = new ExampleDialogFragment();
            exampleDialogFragment.show(getSupportFragmentManager(),
                    ExampleDialogFragment.class.getSimpleName());

            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
