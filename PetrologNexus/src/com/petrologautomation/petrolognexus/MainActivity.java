package com.petrologautomation.petrolognexus;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;


public class MainActivity extends Activity {
    ActionBar bar;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        bar = getActionBar();
        bar.setSubtitle("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connected:

                // app icon in action bar clicked; go home

                bar = getActionBar();

                bar.setSubtitle("Well Name");
                return true;
            case R.id.disconnected:

                // app icon in action bar clicked; go home
                bar = getActionBar();
                bar.setSubtitle("");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
