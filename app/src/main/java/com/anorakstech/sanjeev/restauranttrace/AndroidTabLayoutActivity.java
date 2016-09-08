package com.anorakstech.sanjeev.restauranttrace;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class AndroidTabLayoutActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tab_layout);


        TabHost tabHost = getTabHost();


        // Tab for Find Restaurant
        TabHost.TabSpec restaurantfinder = tabHost.newTabSpec("Find Restaurant");
        restaurantfinder.setIndicator("Find Restaurant", getResources().getDrawable(R.drawable.common_full_open_on_phone));
        Intent findRestaurantIntent = new Intent(this, MainActivity.class);
        restaurantfinder.setContent(findRestaurantIntent);

        // Tab for Add Restaurant
        TabHost.TabSpec addRestaurant = tabHost.newTabSpec("Add Restaurant");
        addRestaurant.setIndicator("Add Restaurant", getResources().getDrawable(R.drawable.search));
        Intent addRestaurantIntent = new Intent(this, AddRestaurant.class);
        addRestaurant.setContent(addRestaurantIntent);

        // Tab for Add Restaurant
        TabHost.TabSpec searchRestaurant = tabHost.newTabSpec("Search Restaurant");
        searchRestaurant.setIndicator("Search Restaurant", getResources().getDrawable(R.drawable.search));
        Intent searchRestaurantIntent = new Intent(this, SearchCuisine.class);
        searchRestaurant.setContent(searchRestaurantIntent);

        // Adding all TabSpec to TabHost
        tabHost.addTab(restaurantfinder);
        tabHost.addTab(addRestaurant);
        tabHost.addTab(searchRestaurant);

    }
}
