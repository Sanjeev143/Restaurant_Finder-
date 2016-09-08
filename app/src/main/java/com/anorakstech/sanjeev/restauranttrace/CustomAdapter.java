package com.anorakstech.sanjeev.restauranttrace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by RajatSharma on 22-03-2016.
 */
public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Restaurant> restaurantsList;
    Restaurant restaurant;
    public CustomAdapter(Context context,ArrayList<Restaurant> restaurantsList) {
        this.context = context;
        this.restaurantsList = restaurantsList;
    }

    @Override
    public int getCount() {
        return restaurantsList.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurantsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        restaurant = restaurantsList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.restaurant_list_item, null);

        }
        TextView title = (TextView) convertView.findViewById(R.id.list_item_title);
        title.setText(restaurant.getName());
        TextView street = (TextView) convertView.findViewById(R.id.list_item_street);
        street.setText(restaurant.getStreet());
        TextView phone = (TextView) convertView.findViewById(R.id.list_item_phone);
        phone.setText(restaurant.getPhone());
        ImageView image = (ImageView) convertView.findViewById(R.id.list_item_image);
        if(restaurant.getImage()!=null)
        {
            if(restaurant.getImage().length!=0)
            {
                Bitmap b1= BitmapFactory.decodeByteArray(restaurant.getImage(), 0, restaurant.getImage().length);
                image.setImageBitmap(b1);
            }
        }
        //image.setText(contactListItems.getPhone());

        return convertView;
    }
}
