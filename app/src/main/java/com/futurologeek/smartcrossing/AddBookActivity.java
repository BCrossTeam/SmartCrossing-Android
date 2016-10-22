package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.Toast;

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
    ImageView choosePhotoIco;
    private Spinner catSelector;
    final String fname = "img_" + System.currentTimeMillis() + ".jpg";
    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_CAMERA = 1;
    File sdImageMainDirectory;
    private Uri outputFileUri;
    private Uri file;
    private Uri imageToUploadUri;
    int yearvalue;
    JSONObject ob;

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
            if(NetworkStatus.checkNetworkStatus(this)){
                new GetContacts().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }

        }

    }

    public void findViews(){
        add_photo_relative = (RelativeLayout) findViewById(R.id.add_photo_relative);
        addTitle =   (EditText) findViewById(R.id.add_title);
        addAuthor = (EditText) findViewById(R.id.add_author);
        arrow = (ImageView) findViewById(R.id.arrow);
        year =(NumberPicker) findViewById(R.id.numberPicker);
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainlinearlayout);
        choosePhotoIco = (ImageView) findViewById(R.id.choose_photo_ic);
        catSelector = (Spinner) findViewById(R.id.cat_selector);
    }

    public void setListeners(){
        year.setMinValue(1800);
        year.setMaxValue(cyear);
        year.setWrapSelectorWheel(false);
        year.setValue(cyear);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              goToCropActivity(false, false);
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
                        if (ActivityCompat.checkSelfPermission(AddBookActivity.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestCameraPermission(true);
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
                            requestCameraPermission(false);
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
                    final String years = titleobject.getString("publishedDate");
                    String[] tokens = years.split("-");
                    boolean firstString = true;
                    for (String t : tokens){
                        if(firstString){
                            yearvalue = Integer.parseInt(t);
                            firstString=false;
                        }
                    }



                    JSONArray arr = titleobject.getJSONArray("authors");
                    for (int j = 0; j < arr.length(); j++) {
                        creators = creators + arr.getString(j);
                        if (j != arr.length() - 1) {
                            creators = creators + ", ";
                        }

                        Book ksiazka = new Book(title, creators, years);
                        AddBookActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                addTitle.setText(title);
                                addAuthor.setText(creators);
                                year.setValue(yearvalue);
                                Snackbar.make(mainLinearLayout, getString(R.string.not_this_book), Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.clear), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addTitle.setText("");
                                                addAuthor.setText("");
                                            }
                                        })
                                        .show();
                            }
                        });


                        // }
                    }
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle(getResources().getString(R.string.confirm));
                            builder.setMessage(getResources().getString(R.string.no_book));

                            builder.setPositiveButton(getResources().getString(R.string.add_m), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    return;
                                }
                            });

                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.setCancelable(false);
                            alert.show();
                            return;
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
                    choosePhotoIco.setColorFilter(AddBookActivity.this.getResources().getColor(R.color.greenlight));
                    AppController.showToast(R.string.photo_added, Toast.LENGTH_SHORT);;
                }
            } else if (requestCode == 96) {
                if (imageToUploadUri != null) {
                    choosePhotoIco.setColorFilter(AddBookActivity.this.getResources().getColor(R.color.greenlight));
                    file = imageToUploadUri;
                    AppController.showToast(R.string.photo_added, Toast.LENGTH_SHORT);
                }
            }
        }
    }

    private void requestCameraPermission(boolean isCamera) {
        if(!isCamera){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(mainLinearLayout, getString(R.string.app_doesnt_have_gallery_permission), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.grant_permission), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(AddBookActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        SELECT_PICTURE);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        SELECT_PICTURE);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Snackbar.make(mainLinearLayout, getString(R.string.app_doesnt_have_gallery_permission), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.grant_permission), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(AddBookActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        REQUEST_CAMERA);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   openImageIntent(true);

                } else {

                    requestCameraPermission(true);
                }
                return;
            }

            case SELECT_PICTURE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openImageIntent(false);
                } else {

                    requestCameraPermission(false);
                }
                return;
            }
            }

        }


    public void goToCropActivity(boolean itsOkayToBeNull, boolean calendarIsOkay) {
        if(!NetworkStatus.checkNetworkStatus(AddBookActivity.this)) {
            Toast.makeText(AddBookActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            return;
        }
        Bundle b = new Bundle();

        final String title = addTitle.getText().toString();
        final String author = addAuthor.getText().toString();


        if(title.equals("")){
            Snackbar.make(mainLinearLayout, getString(R.string.title_null), Snackbar.LENGTH_LONG).show();
            return;
        }
        if(author.equals("")){
            Snackbar.make(mainLinearLayout, getString(R.string.desc_null), Snackbar.LENGTH_LONG).show();
            return;
        }

       if(catSelector.getSelectedItemPosition()==0){
           Snackbar.make(mainLinearLayout, getString(R.string.cat_null), Snackbar.LENGTH_LONG).show();
           return;
       }

        if(!calendarIsOkay){
            if(year.getValue() == cyear){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle(getResources().getString(R.string.confirm));
                builder.setMessage(getResources().getString(R.string.wrong_date)+" "+cyear+"?");

                builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        goToCropActivity(false, true);
                        dialog.dismiss();
                        return;
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();
                return;
            }
        }


        if (file == null && !itsOkayToBeNull) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getResources().getString(R.string.confirm));
            builder.setMessage(getResources().getString(R.string.book_without_content));

            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    goToCropActivity(true, true);
                    dialog.dismiss();
                    return;
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.show();

        } else if (file == null && itsOkayToBeNull) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        POSTHandler han = new POSTHandler();
                        JSONObject par = new JSONObject();
                        try {
                            par.put("user_auth_token", UserInfo.token);
                            par.put("book_title", title);
                            par.put("book_author", author);
                            par.put("book_isbn", "0000000000000");
                            par.put("book_publication_date", String.valueOf(yearvalue));
                            par.put("book_category", "fic");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ob = han.handlePOSTmethod("/book/", par, true);
                        AddBookActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ob.has("error")) {
                                    if(ob.has("sub_error")) {
                                        int sub_error = 0;
                                        try {
                                            sub_error = ob.getInt("sub_error");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        sub_error = sub_error*-1;
                                        try {
                                            Toast.makeText(AddBookActivity.this, getResources().getString(R.string.JUST_ERROR)+" "+ GetStringCode.getErrorResource(ob.getInt("error"), AddBookActivity.this) + getResources().getString(R.string.ADDITIONAL_ERROR_INFO)+" "+ GetStringCode.getErrorResource(sub_error, AddBookActivity.this), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            Toast.makeText(AddBookActivity.this, getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), AddBookActivity.this), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    //Toast.makeText(SignInActivity.this, signInPassword.getText().toString() + "   "  +signInLogin.getText().toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        Toast.makeText(AddBookActivity.this, GetStringCode.getSuccessCode(ob.getInt("success"), AddBookActivity.this), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        } else {
            Intent cel = new Intent(this, com.futurologeek.smartcrossing.crop.CropImageActivity.class);
            cel.setData(file);
            b.putInt("year", year.getValue());
            b.putString("cat", catSelector.getSelectedItem().toString());
            cel.putExtra("aspect_x", 14);
            cel.putExtra("aspect_y", 22);
            b.putString("title", title);
            b.putString("author", author);
            cel.putExtras(b);
            startActivity(cel);
            finish();
        }
    }
}
