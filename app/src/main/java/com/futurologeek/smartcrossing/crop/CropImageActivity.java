/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurologeek.smartcrossing.crop;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.futurologeek.smartcrossing.AddBookActivity;
import com.futurologeek.smartcrossing.GetCategory;
import com.futurologeek.smartcrossing.GetStringCode;
import com.futurologeek.smartcrossing.POSTHandler;
import com.futurologeek.smartcrossing.R;
import com.futurologeek.smartcrossing.MonitoredActivityReportingLifeCycle;
import com.futurologeek.smartcrossing.UserInfo;
import com.futurologeek.smartcrossing.compressor.Compressor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/*
 * Modified from original in AOSP.
 */
public class CropImageActivity extends MonitoredActivityReportingLifeCycle {

    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;
    JSONObject ob;
    private final Handler handler = new Handler();

    private int aspectX = 1;
    private int aspectY = 3;
    // Output image
    private int maxX;
    private int maxY;
    private int exifRotation;
    public String filePath;
    private Uri sourceUri;
    private Uri saveUri;
    String cat;
    private Boolean isDirectFilePath;
    private String title;
    RelativeLayout wholeRelative;
    private String description;
    private String date;
    private String hour;
    private boolean isResponse;
    private int book_year;
    private String author;
    String server_response;
    private boolean isSaving;

    private int sampleSize;
    private RotateBitmap rotateBitmap;
    private CropImageView imageView;
    private HighlightView cropView;
    String ISBN;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setupWindowFlags();
        setupViews();
        booleanHandler();
        if (ActivityCompat.checkSelfPermission(CropImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestWritePermission();
        } else {
            setAll();
        }
        }

    public void setAll(){
        loadInput();
        if (rotateBitmap == null) {
            finish();
            return;
        }
        startCrop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 69: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   setAll();

                } else {

                   requestWritePermission();
                }
                return;
            }

        }
    }



    public void requestWritePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(wholeRelative, getString(R.string.app_doesnt_have_gallery_permission), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.grant_permission), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(CropImageActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    69);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    69);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setupWindowFlags() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void booleanHandler() {
        if(getIntent().getExtras()!=null) {
            title = getIntent().getExtras().getString("title");
            cat = getIntent().getExtras().getString("cat");
            author = getIntent().getExtras().getString("author");
            ISBN = getIntent().getExtras().getString("ISBN");
            book_year = getIntent().getExtras().getInt("year");
            isDirectFilePath = getIntent().getExtras().getBoolean("isDirectFilePath");
        }

    }

    private void setupViews() {
        setContentView(R.layout.crop__activity_crop);
        wholeRelative = (RelativeLayout) findViewById(R.id.whole_relative);

        imageView = (CropImageView) findViewById(R.id.crop_image);
        imageView.context = this;
        imageView.setRecycler(new ImageViewTouchBase.Recycler() {
            @Override
            public void recycle(Bitmap b) {
                b.recycle();
                System.gc();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSaveClicked();
            }
        });
    }

    private void loadInput() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            aspectX = extras.getInt(Crop.Extra.ASPECT_X);
            aspectY = extras.getInt(Crop.Extra.ASPECT_Y);
            maxX = extras.getInt(Crop.Extra.MAX_X);
            maxY = extras.getInt(Crop.Extra.MAX_Y);
            saveUri = extras.getParcelable(MediaStore.EXTRA_OUTPUT);
        }

        sourceUri = intent.getData();
        if (sourceUri != null) {
            exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(this, getContentResolver(), sourceUri));

            InputStream is = null;
            try {
                sampleSize = calculateBitmapSampleSize(sourceUri);
                is = getContentResolver().openInputStream(sourceUri);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = sampleSize;
                rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, null, option), exifRotation);
            } catch (IOException e) {
                Log.e("Error reading image: " + e.getMessage(), e);
                setResultException(e);
            } catch (OutOfMemoryError e) {
                Log.e("OOM reading image: " + e.getMessage(), e);
                setResultException(e);
            } finally {
                CropUtil.closeSilently(is);
            }
        }
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } finally {
            CropUtil.closeSilently(is);
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    private int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return SIZE_DEFAULT;
        } else {
            return Math.min(textureLimit, SIZE_LIMIT);
        }
    }

    private int getMaxTextureSize() {
        // The OpenGL texture size is the maximum size that can be drawn in an ImageView
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private void startCrop() {
        if (isFinishing()) {
            return;
        }
        imageView.setImageRotateBitmapResetBase(rotateBitmap, true);
        CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait),
                new Runnable() {
                    public void run() {
                        final CountDownLatch latch = new CountDownLatch(1);
                        handler.post(new Runnable() {
                            public void run() {
                                if (imageView.getScale() == 1F) {
                                    imageView.center();
                                }
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        new Cropper().crop();
                    }
                }, handler
        );
    }

    private class Cropper {

        private void makeDefault() {
            if (rotateBitmap == null) {
                return;
            }

            HighlightView hv = new HighlightView(imageView);
            final int width = rotateBitmap.getWidth();
            final int height = rotateBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // Make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            @SuppressWarnings("SuspiciousNameCombination")
            int cropHeight = cropWidth;

            if (aspectX != 0 && aspectY != 0) {
                if (aspectX > aspectY) {
                    cropHeight = cropWidth * aspectY / aspectX;
                } else {
                    cropWidth = cropHeight * aspectX / aspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(imageView.getUnrotatedMatrix(), imageRect, cropRect, aspectX != 0 && aspectY != 0);
            imageView.add(hv);
        }

        public void crop() {
            handler.post(new Runnable() {
                public void run() {
                    makeDefault();
                    imageView.invalidate();
                    if (imageView.highlightViews.size() == 1) {
                        cropView = imageView.highlightViews.get(0);
                        cropView.setFocus(true);
                    }
                }
            });
        }
    }

    private void onSaveClicked() {
        if (cropView == null || isSaving) {
            return;
        }
        isSaving = true;

        Bitmap croppedImage;
        Rect r = cropView.getScaledCropRect(sampleSize);
        int width = r.width();
        int height = r.height();

        int outWidth = width;
        int outHeight = height;
        if (maxX > 0 && maxY > 0 && (width > maxX || height > maxY)) {
            float ratio = (float) width / (float) height;
            if ((float) maxX / (float) maxY > ratio) {
                outHeight = maxY;
                outWidth = (int) ((float) maxY * ratio + .5f);
            } else {
                outWidth = maxX;
                outHeight = (int) ((float) maxX / ratio + .5f);
            }
        }

        try {
            croppedImage = decodeRegionCrop(r, outWidth, outHeight);
        } catch (IllegalArgumentException e) {
            setResultException(e);
            finish();
            return;
        }

        if (croppedImage != null) {
            imageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, exifRotation), true);
            imageView.center();
            imageView.highlightViews.clear();
        }
        saveImage(croppedImage);
    }

    private void saveImage(Bitmap croppedImage) {
        if (croppedImage != null) {
            final Bitmap b = croppedImage;
            CropUtil.startBackgroundJob(this, null, CropImageActivity.this.getResources().getString(R.string.crop__saving),
                    new Runnable() {
                        public void run() {
                            try {
                                saveOutput(b);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, handler
            );
        } else {
            finish();
        }
    }

    private Bitmap decodeRegionCrop(Rect rect, int outWidth, int outHeight) {
        // Release memory now
        clearImageView();

        InputStream is = null;
        Bitmap croppedImage = null;
        try {
            is = getContentResolver().openInputStream(sourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            final int width = decoder.getWidth();
            final int height = decoder.getHeight();

            if (exifRotation != 0) {
                // Adjust crop area to account for image rotation
                Matrix matrix = new Matrix();
                matrix.setRotate(-exifRotation);

                RectF adjusted = new RectF();
                matrix.mapRect(adjusted, new RectF(rect));

                //if the cutting box are rectangle( outWidth != outHeight ),and the exifRotation is 90 or 270,
                //the outWidth and outHeight showld be interchanged
                if (exifRotation==90||exifRotation==270){
                    int temp=outWidth;
                    outWidth=outHeight;
                    outHeight=temp;
                }

                // Adjust to account for origin at 0,0
                adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
                rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
            }

            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
                if (rect.width() > outWidth || rect.height() > outHeight) {
                    Matrix matrix = new Matrix();
                    matrix.postScale((float) outWidth / rect.width(), (float) outHeight / rect.height());

                    //if the picture's exifRotation !=0 ,they should be rotate to 0 degrees
                    matrix.postRotate(exifRotation);
                    croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
                }else{
                    //if the picture need not to be scale, they also neet to be rotate to 0 degrees
                    Matrix matrix = new Matrix();
                    matrix.postRotate(exifRotation);
                    croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
                }
            } catch (IllegalArgumentException e) {
                // Rethrow with some extra information
                throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image ("
                        + width + "," + height + "," + exifRotation + ")", e);
            }

        } catch (IOException e) {
            Log.e("Error cropping image: " + e.getMessage(), e);
            finish();
        } catch (OutOfMemoryError e) {
            Log.e("OOM cropping image: " + e.getMessage(), e);
            setResultException(e);
        } finally {
            CropUtil.closeSilently(is);
        }
        return croppedImage;
    }

    private void clearImageView() {
        imageView.clear();
        if (rotateBitmap != null) {
            rotateBitmap.recycle();
        }
        System.gc();
    }

    private void saveOutput(Bitmap croppedImage) throws IOException {
        saveUri = saveFileFromBitmap(croppedImage);
        filePath = getPath(CropImageActivity.this, saveUri);

        final Bitmap b = croppedImage;
        handler.post(new Runnable() {
            public void run() {
                imageView.clear();
                b.recycle();
            }
        });

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
                        par.put("book_isbn", ISBN);
                        par.put("book_publication_date", book_year);
                        par.put("book_category", GetCategory.returnCatCode(CropImageActivity.this, cat));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ob = han.handlePOSTmethodAddBook(filePath, par);
                    CropImageActivity.this.runOnUiThread(new Runnable() {
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
                                        Toast.makeText(CropImageActivity.this, getResources().getString(R.string.JUST_ERROR)+" "+ GetStringCode.getErrorResource(ob.getInt("error"), CropImageActivity.this) + getResources().getString(R.string.ADDITIONAL_ERROR_INFO)+" "+ GetStringCode.getErrorResource(sub_error, CropImageActivity.this), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Toast.makeText(CropImageActivity.this, getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), CropImageActivity.this), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                finish();
                            } else {
                                try {
                                    Toast.makeText(CropImageActivity.this, GetStringCode.getSuccessCode(ob.getInt("success"), CropImageActivity.this), Toast.LENGTH_SHORT).show();
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

    }

    private Uri saveFileFromBitmap(Bitmap bitmap) throws IOException {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        tempDir.mkdir();
        File tempFile = File.createTempFile("tempfile", ".jpg", tempDir);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] bitmapData = bytes.toByteArray();

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        tempFile = Compressor.getDefault(this).compressToFile(tempFile);
        return Uri.fromFile(tempFile);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }



    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rotateBitmap != null) {
            rotateBitmap.recycle();
        }
    }

    @Override
    public boolean onSearchRequested() {
        return false;
    }

    public boolean isSaving() {
        return isSaving;
    }

    private void setResultUri(Uri uri) {
        setResult(RESULT_OK, new Intent().putExtra(MediaStore.EXTRA_OUTPUT, uri));

    }

    private void setResultException(Throwable throwable) {
        setResult(Crop.RESULT_ERROR, new Intent().putExtra(Crop.Extra.ERROR, throwable));
    }



}

