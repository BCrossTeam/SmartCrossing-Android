package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.api.services.books.Books;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;


public class AddBookActivity extends AppCompatActivity {
    String url = "";
    String creators = "";
    EditText addTitle, addAuthor;
    NumberPicker year;
    ImageView arrow;
    RelativeLayout add_photo_relative;
    LinearLayout mainLinearLayout;

    final String fname = "img_" + System.currentTimeMillis() + ".jpg";
    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_CAMERA = 1;
    File sdImageMainDirectory;
    private Uri outputFileUri;
    private Uri file;
    private Uri imageToUploadUri;

    int cyear = Calendar.getInstance().get(Calendar.YEAR);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        findViews();
        setListeners();
        if(getIntent().getExtras()!=null){
            Bundle przekazanedane = getIntent().getExtras();
            url  = przekazanedane.getString("jurl");
            new GetContacts().execute();
        }

    }

    public void findViews(){
        add_photo_relative = (RelativeLayout) findViewById(R.id.add_photo_relative);
        addTitle =   (EditText) findViewById(R.id.add_title);
        addAuthor = (EditText) findViewById(R.id.add_author);
        arrow = (ImageView) findViewById(R.id.arrow);
        year =(NumberPicker) findViewById(R.id.numberPicker);
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainlinearlayout);
    }

    public void setListeners(){
        year.setMinValue(1800);
        year.setMaxValue(cyear);
        year.setWrapSelectorWheel(false);
        year.setValue(cyear);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent i = new Intent(AddBookActivity.this,AddCoverActivity.class);
              //  startActivity(i);
            }
        });

        add_photo_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddBookActivity.this);
                //tell the Dialog to use the dialog.xml as it's layout description
                dialog.setContentView(R.layout.dialog_choose_photo_source);
                //dialog.setTitle(getResources().getString(R.string.select_src));
                RelativeLayout takePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.take_photo_relative);
                RelativeLayout choosePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.choose_photo_relative);
                takePhotoRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(AddBookActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestCameraPermission();
                            dialog.dismiss();
                        } else {
                            openImageIntent(true);
                            dialog.dismiss();
                        }
                    }
                });

                choosePhotoRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ActivityCompat.checkSelfPermission(AddBookActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestCameraPermission();
                            dialog.dismiss();
                        } else {
                            openImageIntent(false);
                            dialog.dismiss();
                        }


                    }
                });
                dialog.show();
            }
        });
    }

    class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("items");
                    // for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(0);
                    JSONObject titleobject = c.getJSONObject("volumeInfo");
                    final String title = titleobject.getString("title");
                    final String year = titleobject.getString("publishedDate");
                    JSONArray arr = titleobject.getJSONArray("authors");
                    for (int j = 0; j < arr.length(); j++) {
                        creators = creators + arr.getString(j);
                        if (j != arr.length() - 1) {
                            creators = creators + ", ";
                        }

                        Book ksiazka = new Book(title, creators, year);
                        AddBookActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                addTitle.setText(title);
                                addAuthor.setText(creators);
                            }
                        });


                        // }
                    }
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("TAG", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }

    public void openImageIntent(boolean isCamera) {
        final File root = new File(Constants.fileRoot);
        root.mkdirs();
        sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        if (!isCamera) {
            if (Build.VERSION.SDK_INT <= 19) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(i, 69);
            } else if (Build.VERSION.SDK_INT > 19) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 69);
            }
        } else {
            Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(sdImageMainDirectory));
            imageToUploadUri = Uri.fromFile(sdImageMainDirectory);
            startActivityForResult(chooserIntent, 96);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 69) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    file = selectedImageUri;
                    AppController.showToast(R.string.photo_added, Toast.LENGTH_SHORT);;
                }
            } else if (requestCode == 96) {
                if (imageToUploadUri != null) {
                    file = imageToUploadUri;
                    AppController.showToast(R.string.photo_added, Toast.LENGTH_SHORT);
                }
            }
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(mainLinearLayout, getString(R.string.app_doesnt_have_gallery_permission), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.grant_permission), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(AddBookActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SELECT_PICTURE);
        }

    }
}
