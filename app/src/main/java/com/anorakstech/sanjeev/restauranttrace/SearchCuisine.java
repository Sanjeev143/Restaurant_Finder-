package com.anorakstech.sanjeev.restauranttrace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchCuisine extends AppCompatActivity {

    CustomAdapter restaurantListAdapter;
    ArrayList<Restaurant> contactList;
    private static final String Download_URL = "http://hungerhunterz.esy.es/getSearchResult.php";
    private String TAG = "ReturnedResult";


    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    FloatingActionButton fab;
    ImageButton searchImgButton;
    EditText searchCuisine ;

    GetData getdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_cuisine);

        obj = (ListView)findViewById(R.id.listViewSearch);
        searchImgButton = (ImageButton) findViewById(R.id.searchButton);
        searchCuisine = (EditText)findViewById(R.id.search_cuisine);

        searchImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchCuisine.getText().toString().isEmpty())
                {
                    Log.v(TAG,"Search button clicked");
                    if(getdata==null)
                    {
                        if(contactList!=null)
                        {
                            contactList.clear();
                            restaurantListAdapter.notifyDataSetChanged();
                        }
                        Log.v(TAG,"Search button clicked");
                        getdata = new GetData(searchCuisine.getText().toString());
                        getdata.execute();
                    }
                }
            }
        });




        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Restaurant rs = contactList.get(arg2);
                int id_To_Search = rs.getId();

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                Intent intent = new Intent(getApplicationContext(), DispalyaDetailofRestaurant.class);
                intent.putExtras(dataBundle);
                intent.putExtra("Restaurant",rs);
                startActivity(intent);
            }
        });

        obj.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Restaurant rs = contactList.get(position);
                Log.v("RecordId", "" + rs.getId() + "Position : " + position);
                return true;
            }
        });
        fab = (FloatingActionButton)findViewById(R.id.fab_add_restaurant);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchCuisine.this,DispalyaDetailofRestaurant.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.item1:Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);

                Intent intent = new Intent(getApplicationContext(),DispalyaDetailofRestaurant.class);
                intent.putExtras(dataBundle);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }
    //new function
    private class GetData extends AsyncTask<Void,Void,String> {

        ProgressDialog loading;
        RequestHandler rh = new RequestHandler();
        String cuisine;
        GetData(String cuisine)
        {
            this.cuisine = cuisine;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loading = ProgressDialog.show(getApplicationContext(), "Downloading Data", "Please wait...", true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            contactList = new ArrayList<Restaurant>();
            contactList.clear();

            try {
                JSONArray arr = new JSONArray(s);
                Log.v(TAG, arr.toString() + arr.length());


                for(int i=0;i<arr.length();i++)
                {
                    JSONObject jsonObj = arr.getJSONObject(i);

                    Restaurant restaurantListItem = new Restaurant();
                    restaurantListItem.setId(Integer.parseInt(jsonObj.getString("id")));
                    restaurantListItem.setName(jsonObj.getString("name"));
                    restaurantListItem.setPhone(jsonObj.getString("phone"));
                    restaurantListItem.setEmail(jsonObj.getString("email"));
                    restaurantListItem.setStreet(jsonObj.getString("street"));
                    restaurantListItem.setZip(jsonObj.getString("zip"));
                    restaurantListItem.setCuisine(jsonObj.getString("cuisine"));
                    if(jsonObj.has("image")&&!jsonObj.getString("image").equals("null"))
                    {
                        String img = jsonObj.getString("image");
                        byte[] imageArray= Base64.decode(img, Base64.DEFAULT);
//                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
                        restaurantListItem.setImage(imageArray);

                    }
                    contactList.add(restaurantListItem);
                }
                restaurantListAdapter = new CustomAdapter(SearchCuisine.this, contactList);
                obj.setAdapter(restaurantListAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //loading.dismiss();
            Log.v(TAG, s);
            getdata = null;
        }

        @Override
        protected String doInBackground(Void... params) {


            HashMap<String, String> data = new HashMap<>();
            data.put("cuisine",cuisine);

            Log.v(TAG, data.toString());
            String result = rh.sendPostRequest(Download_URL, data);
            return result;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            getdata=null;
            //loading.dismiss();
            Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();
        }
    }


}


