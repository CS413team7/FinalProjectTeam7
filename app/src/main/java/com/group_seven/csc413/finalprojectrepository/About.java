package com.group_seven.csc413.finalprojectrepository;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Steven on 4/29/2015.
 */

/**
 * Create and start an intent of this class to display the about page
 */
public class About extends Activity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        TextView about = new TextView(this);

        String text = "ABOUT \n\n\n" +
                "CSC 413, Spring 2015, Team 7 \n\n" +
                "Use this app at your own risk! \n\n" +
                "We are not responsible for any damage done to your device!";

        about.setTextSize(25);
        about.setPadding(30, 100, 0, 0);
        about.setBackgroundColor(Color.BLACK);
        about.setTextColor(Color.WHITE);
        about.setText(text);

        setContentView(about);
    }
}
