package com.anorakstech.sanjeev.restauranttrace;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class DispalyaDetailofRestaurant extends AppCompatActivity{

    int from_Where_I_Am_Coming = 0;

    private static final String SAVE_URL = "http://hungerhunterz.esy.es/saveRestaurant.php";

    TextView name ;
    TextView phone;
    TextView email;
    TextView street;
    TextView place;
    TextView cuisine;
    int id_To_Update = 0;

    ImageView imageView;
    Bitmap byteImage = null;
    Button saveButton;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private String TAG = "ReturnedResult";


    private Uri uri;
    private Uri mediaUri=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispalya_detailof_restaurant);


        name = (TextView) findViewById(R.id.editTextName);
        phone = (TextView) findViewById(R.id.editTextPhone);
        email = (TextView) findViewById(R.id.editTextStreet);
        street = (TextView) findViewById(R.id.editTextEmail);
        place = (TextView) findViewById(R.id.editTextCity);
        cuisine = (TextView) findViewById(R.id.editTextCuisine);
        imageView= (ImageView) findViewById(R.id.image_view);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        //When image is clicked present user with dialog to choose image or not from camera or gallery
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //When save button is clicked, then data should be saved in db
        saveButton = (Button)findViewById(R.id.savedata_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
//                    saveIntoDB();
                    new UploadData(name.getText().toString(),phone.getText().toString(),email.getText().toString(),street.getText().toString(),place.getText().toString(),cuisine.getText().toString()).execute(SAVE_URL);
                }
                else{
                }
            }
        });
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            int Value = extras.getInt("id");

            if(Value>0){
                //means this is the view part not the add contact part.

                Restaurant restaurant = (Restaurant) extras.getSerializable("Restaurant");
                String nam = restaurant.getName();
                String phon = restaurant.getPhone();
                String emai = restaurant.getEmail();
                String stree =restaurant.getStreet() ;
                String plac = restaurant.getZip();
                String cui = restaurant.getCuisine();
                byte[] imag = restaurant.getImage();
                if(imag!=null)
                {
                    Log.v("Image", String.valueOf(imag.length));
                    if(imag.length!=0)
                    {
                        Bitmap b1=BitmapFactory.decodeByteArray(imag, 0, imag.length);
                        imageView.setImageBitmap(b1);
                    }
                }


                //------------------------------------------------//
                Log.d("DataGet",nam.toString()+phon.toString()+emai.toString()+stree.toString()+plac.toString());


                Button savedata = (Button)findViewById(R.id.savedata_btn);
                savedata.setVisibility(View.INVISIBLE);

                name.setText((CharSequence) nam);
                name.setFocusable(false);
                name.setClickable(false);

                phone.setText((CharSequence) phon);
                phone.setFocusable(false);
                phone.setClickable(false);

                email.setText((CharSequence) emai);
                email.setFocusable(false);
                email.setClickable(false);

                street.setText((CharSequence) stree);
                street.setFocusable(false);
                street.setClickable(false);

                place.setText((CharSequence) plac);
                place.setFocusable(false);
                place.setClickable(false);

                cuisine.setText((CharSequence) cui);
                cuisine.setFocusable(false);
                cuisine.setClickable(false);


            }
        }

    }

    private boolean validate() {
        if(name.getText().toString().isEmpty())
        {
            name.setError("Required");
            return false;
        }
        if(phone.getText().toString().isEmpty())
        {
            phone.setError("Required");
            return false;
        }
        if(email.getText().toString().isEmpty())
        {
            email.setError("Required");
            return false;
        }
        if(street.getText().toString().isEmpty())
        {
            street.setError("Required");
            return false;
        }
        if(place.getText().toString().isEmpty())
        {
            place.setError("Required");
            return false;
        }
        if(cuisine.getText().toString().isEmpty())
        {
            place.setError("Required");
            return false;
        }
        if(!hasImage(imageView))
        {
            Toast.makeText(DispalyaDetailofRestaurant.this, "Select Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(DispalyaDetailofRestaurant.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                storeCameraImage();
            } else if (requestCode == 2) {
                getGalleryImage(data);
            }
        }
    }

    private void storeCameraImage() {
        File f = new File(Environment.getExternalStorageDirectory().toString());
        for (File temp : f.listFiles()) {
            if (temp.getName().equals("temp.jpg")) {
                f = temp;
                break;
            }
        }
        try {
            Bitmap bitmap;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = 5;
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                    bitmapOptions);

            imageView.setImageBitmap(bitmap);

            byteImage = bitmap;
            String path = Environment
                    .getExternalStorageDirectory()
                    + File.separator
                    + "RestaurantTrace" + File.separator + "default";
            f.delete();
            OutputStream outFile = null;
            File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
            try {
                outFile = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                outFile.flush();
                outFile.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getGalleryImage(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath,options));
        Log.w("path of image from gallery.", picturePath + "");

        imageView.setImageBitmap(thumbnail);
        byteImage = thumbnail;

    }
//
//    public void saveIntoDB()
//    {
//        mydb.insertContact(name.getText().toString(), phone.getText().toString(),
//                email.getText().toString(), street.getText().toString(),
//                place.getText().toString(),byteImage);
//        Intent intent= new Intent(DispalyaDetailofRestaurant.this,AndroidTabLayoutActivity.class);
//        startActivity(intent);
//        finish();
//    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }



    private class UploadData extends AsyncTask<String, Void, String> {

        ProgressDialog loading;
        RequestHandler rh = new RequestHandler();
        String name, phone, email, street,  zip, cuisine;
        public UploadData(String name,String phone,String email,String street, String zip,String cuisine) {
            this.name=name;
            this.phone=phone;
            this.email=email;
            this.street=street;
            this.zip=zip;
            this.cuisine=cuisine;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(DispalyaDetailofRestaurant.this, "Uploading Data", "Please wait...", true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            Log.v(TAG,s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            Bitmap bitmap1;
            String uploadImage1 = "";


            HashMap<String, String> data = new HashMap<>();
            data.put("name",name);
            data.put("phone",phone);
            data.put("zip",zip);
            data.put("email",email);
            data.put("street",street);
            data.put("cuisine",cuisine);

            bitmap1 = byteImage;
            uploadImage1 = getStringImage(bitmap1);
            data.put("image", uploadImage1);

            Log.v(TAG,data.toString());
            String result = rh.sendPostRequest(params[0], data);
            return result;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loading.dismiss();
            Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();
        }
    }


}
