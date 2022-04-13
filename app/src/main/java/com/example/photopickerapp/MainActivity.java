

package com.example.photopickerapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    String currentImagePath = null;

    TextInputLayout textFieldVehicleRegistration, textFieldOffender, textFieldOffenses, textFieldDescription,textFieldOffences_menu,textFieldDL_Number_or_IDNumber;
    Button buttonSubmit,buttonAddOffender, buttonAddOffences, buttonWitnessReport, buttonAddPhotos, buttonTakeAlbumPhoto,button_submit_offence;

    CircularProgressIndicator circularProgressIndicator;
    ProgressBar progressBar;

    AutoCompleteTextView autoCompleteOffence, autoCompleteSelectOffender;
    ArrayAdapter arrayAdapter, arrayAdapterOffencesDetails;
    ArrayList<String> offencesArrayList;
    ArrayList<String> offenderDetailsArrayList;
    private String[] PERMISSIONS;
    private ImageView fileReportImageView;
    private String tempImageFilePath = "";
    //private Uri imageViewUri;

    private Uri takePhotoFileUri;
    private Uri uriFromGetContent;
    private Uri uri;

    Bitmap bitmap = null;
    private String takePhotoFileImagePath="";


    String selectedOffence = "";
    String selectOffenderDetail = "";



    ActivityResultLauncher<String> startForResultHere = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {

            if (result != null){

                uriFromGetContent = result;

                fileReportImageView.setImageURI(uriFromGetContent);

                Log.i("LogUriResult", String.valueOf(result));

                Log.i("InsideOnResultTrue","Inside Boolean True Here");
                Log.i("GetPath",result.getPath());
                Log.i("EncodedPath",result.getEncodedPath());
                Log.i("GetAuthority",result.getAuthority());
                Log.i("tempImageFilePath",tempImageFilePath);

                //Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(result));

                //Code for Converting the image Uri result to a bitmap
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result);
//                    fileReportImageView.setImageBitmap(bitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                //fileReportImageView.setImageBitmap(bitmap.);


            }
            else{
                //Toast.success(getApplicationContext(),"Result is Ok", Toast.LENGTH_SHORT, true).show();
                Toast.makeText(getApplicationContext(),"You Haven't taken a picture.", Toast.LENGTH_LONG).show();

            }






            //imageView.setImageURI(Uri.parse(tempImageFilePath));

        }
    });


    ActivityResultLauncher<Uri> startForResult = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {

            if (result){
                Log.i("InsideOnResultTrue","Inside Boolean True");
                Log.i("TakePhotoFileUriInBool", String.valueOf(takePhotoFileUri));
                Log.i("Here","Here");
                Log.i("TakePhotoFileImagePath",takePhotoFileImagePath);
                Log.i("Here","Here");
                //imageView.setImageURI(imageViewUri);
                fileReportImageView.setImageURI(takePhotoFileUri);

            }
            else{
                Toast.makeText(getApplicationContext(),"You Haven't taken a picture.", Toast.LENGTH_LONG).show();
                Log.i("InsideOnResultFalse","Inside Boolean False");

            }

        }
    });

//    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result) {
//            Log.i("InsideOnResult","Inside OnActivity Result");
//
//
//            if (result != null && result.getResultCode() == RESULT_OK){
//                Toasty.success(getApplicationContext(),"Result is Ok", Toast.LENGTH_SHORT, true).show();
//
//            }
//            else{
//                Toasty.success(getApplicationContext(),"Result is Not Ok", Toast.LENGTH_SHORT, true).show();
//
//            }
//
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAddPhotos = findViewById(R.id.takePhotos);
        buttonTakeAlbumPhoto = findViewById(R.id.buttonAddAlbumPhotos);
        buttonWitnessReport = findViewById(R.id.buttonWitnessReport);
        button_submit_offence = findViewById(R.id.button_submit_offence);
        progressBar = findViewById(R.id.report_progressbar);

        //getActionBar().setTitle("File Report");
        getSupportActionBar().setTitle("File Report");


        circularProgressIndicator = findViewById(R.id.fileReportProgressIndicator);

        textFieldOffences_menu = findViewById(R.id.offences_menu);
        textFieldOffender = findViewById(R.id.textFieldOffender);
        textFieldDL_Number_or_IDNumber = findViewById(R.id.textFieldDL_Number_or_IDNumber);
        textFieldVehicleRegistration = findViewById(R.id.textFieldVehicleRegistration);
        textFieldDescription = findViewById(R.id.textFieldDescription);


        fileReportImageView = findViewById(R.id.fileReportImageView);

        buttonAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toasty.success(getApplicationContext(),"Taking Photo", Toast.LENGTH_SHORT, true).show();
                if (!hasPermissions(MainActivity.this, PERMISSIONS)){
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS,1);
                }
                else{
                    try {
                        takePicture();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });


        buttonTakeAlbumPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startForResultHere.launch("image/*");

            }
        });

        buttonWitnessReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), WitnessAccount.class);
//                startActivity(intent);
            }
        });


        PERMISSIONS = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        };


        //List<OffencesTable> offences = Select.columns("offence").from(OffencesTable.class).fetch();
        offencesArrayList = new ArrayList<String>();
        offencesArrayList.add("Select Offence");
        offencesArrayList.add("ReckLess Driving");
        offencesArrayList.add("Overspeeding");
        offencesArrayList.add("Drunk Driving");



        offenderDetailsArrayList = new ArrayList<String>();

        offenderDetailsArrayList.add("Select Offender Detail");
        offenderDetailsArrayList.add("DL Number");
        offenderDetailsArrayList.add("ID Number");


//        for (int i=0;i<offences.size();i++){
//            offencesArrayList.add(offences.get(i).getOffence());
//
//
//        }

        arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.option_item, offencesArrayList);
        arrayAdapterOffencesDetails = new ArrayAdapter(getApplicationContext(), R.layout.option_item, offenderDetailsArrayList);


        autoCompleteOffence = findViewById(R.id.autoCompleteOffence);

        //To make it the default value
        autoCompleteOffence.setText("Offence", false);

        autoCompleteSelectOffender = findViewById(R.id.autoCompleteOffenderDetail);

        //To make it the default value
        autoCompleteSelectOffender.setText("Select Offender Detail", false);




        autoCompleteOffence.setAdapter(arrayAdapter);
        autoCompleteSelectOffender.setAdapter(arrayAdapterOffencesDetails);




        autoCompleteOffence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedOffence = arrayAdapterOffencesDetails.getItem(position).toString();

                Toast.makeText(getApplicationContext(), arrayAdapterOffencesDetails.getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });



        autoCompleteSelectOffender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                selectOffenderDetail = arrayAdapter.getItem(position).toString();

                if (position == 0){
                    textFieldDL_Number_or_IDNumber.setVisibility(View.GONE);
                }

                else if (position == 1){
                    textFieldDL_Number_or_IDNumber.setVisibility(View.VISIBLE);
                    textFieldDL_Number_or_IDNumber.setHint("Enter DL Number");
                }

                else if (position == 2){
                    textFieldDL_Number_or_IDNumber.setVisibility(View.VISIBLE);
                    textFieldDL_Number_or_IDNumber.setHint("Enter ID Number");

                }

                //Toast.makeText(getApplicationContext(), arrayAdapterOffencesDetails.getItem(position).toString(), Toast.LENGTH_SHORT).show();

            }
        });


        button_submit_offence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (takePhotoFileUri != null){
                    uploadImage(Uri.parse(takePhotoFileImagePath));

                }
                else if (uriFromGetContent != null){
                    uploadImage(uriFromGetContent);

                }
                else{
                    Toast.makeText(getApplicationContext(), "None of the above launched", Toast.LENGTH_SHORT).show();

                }


            }
        });





    }

    private boolean hasPermissions(Context context, String... PERMISSIONS){
        if (context != null && PERMISSIONS != null){
            for (String permission: PERMISSIONS){
                if (ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }

        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Calling Permission is granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Calling Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Calling Permission is granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Calling Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[2] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Calling Permission is granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Calling Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                //Start Camera Activity
                try {
                    takePicture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void takePicture() throws IOException {
//
//        startForResult.launch();
//
//        tempImageUri = FileProvider.getUriForFile(this,"com.example.cameraapp.provider",createImageFile().also {
//            tempImageFilePath = it.absolutePath
//        })



        //File fileCreateImageFile = createImageFile().getAbsoluteFile();

        //String fileGetAbsolutePath = createImageFile().getAbsolutePath();

        //Log.i("FileCreateImageFile", String.valueOf(fileCreateImageFile));
        //Log.i("FileGetAbsolutePath", fileGetAbsolutePath);

        File myImageFile = getImageFile();

        takePhotoFileUri = FileProvider.getUriForFile(this,"com.example.photopickerapp.provider",myImageFile);

        takePhotoFileImagePath = myImageFile.getAbsolutePath();

        Log.i("FileIsHere", String.valueOf(takePhotoFileUri));

        //tempImageFilePath = file.getPath();

        fileReportImageView.setImageURI(takePhotoFileUri);


        //Log.i("FileGetPath",file.getPath());
        //Log.i("ActualUrlFileObject", String.valueOf(file+file.getPath()));
        //tempImageFilePath = file.getPath();
        //imageView.setImageURI(Uri.parse(file.getPath()));

        startForResult.launch(takePhotoFileUri);







//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null){
//            startActivity(intent);
//
//        }
    }


    private File createImageFile() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            return File.createTempFile("temp_image",".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return File.createTempFile("temp_image",".jpg",storageDir);

        return storageDir;
    }

    private void uploadReport(){
        circularProgressIndicator.setVisibility(View.VISIBLE);

        String vehicleRegistration = textFieldVehicleRegistration.getEditText().getText().toString().trim();
        String description = textFieldDescription.getEditText().toString().trim();


        if (vehicleRegistration.isEmpty()){
            textFieldVehicleRegistration.setError("Please Enter Vehicle Registration Number");
            textFieldVehicleRegistration.requestFocus();
            circularProgressIndicator.setVisibility(View.GONE);
            return;

        }

        if (description.isEmpty()){
            textFieldDescription.setError("Please provide a small description");
            textFieldDescription.requestFocus();
            circularProgressIndicator.setVisibility(View.GONE);
            return;

        }

        if (selectedOffence.equalsIgnoreCase("Select Offence")){
            textFieldOffences_menu.setError("Please Select the Offence");
            textFieldOffences_menu.requestFocus();
            circularProgressIndicator.setVisibility(View.GONE);
            return;
        }

        if (selectOffenderDetail.equalsIgnoreCase("Select Offender Detail")){
            textFieldOffender.setError("Please Select the Offenders Detail");
            textFieldOffender.requestFocus();
            circularProgressIndicator.setVisibility(View.GONE);
            return;

        }




    }

    private File getImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg",storageDir);
        return imageFile;
    }

//    private void doMultiPartRequest(){
//        String path = Environment.getExternalStorageDirectory().toString() + "/pictures";
//        Log.d("Files","Path:" + path);
//        File f = new File(path);
//        File file[] = f.listFiles();
//        Log.i("Files","Size" + file.length);
//        for (int i=0;i < file.length;i++){
//            if (file[i].isFile()){
//                Log.d("OkHttpV3Files","FileName: "+ file[i].getName());
//                DoActualRequest(File[i]);
//                break;
//            }
//        }
//
//    }
//
//    private void DoActualRequest(File file){
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new MultipartBody.Builder();
//    }

//    //Prabesh Upload Image
//    private void uploadImage(){
//        String Image = imageToString();
//        String title = "My Title";
//        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
//        Call<ImageClass> call = apiInterface.uploadImage(title,Image);
//        call.enqueue(new Callback<ImageClass>() {
//            @Override
//            public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {
//                ImageClass imageClass = response.body();
//                Toast.makeText(MainActivity.this, "Server Response: "+imageClass.getResponse(),Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call<ImageClass> call, Throwable t) {
//
//            }
//        });
//    }

    private String imageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);

    }

    private String uriToFilename(Uri uri) {
        String path = null;

        if ((Build.VERSION.SDK_INT < 19) && (Build.VERSION.SDK_INT > 11)) {
            path = getRealPathFromURI_API11to18(this, uri);
        } else {
            path = getFilePath(this, uri);
        }

        return path;
    }

    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;
        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }


    public String getFilePath(Context context, Uri uri) {
        //Log.e("uri", uri.getPath());
        String filePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            //Log.e("wholeID", wholeID);
            // Split at colon, use second item in the array
            String[] splits = wholeID.split(":");
            if (splits.length == 2) {
                String id = splits[1];

                String[] column = {MediaStore.Images.Media.DATA};
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }


    public void uploadImage(Uri parameterUri){

        if(parameterUri == null){
            Toast.makeText(getApplicationContext(),"Image Uri is Null", Toast.LENGTH_LONG).show();
            return;
        }

        final File imageFile = new File(uriToFilename(parameterUri));
        Uri uris = Uri.fromFile(imageFile);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        String imageName = imageFile.getName();

        OkHttpClient client = new OkHttpClient();

        //Log.e(TAG, imageFile.getName()+" "+mime+" "+uriToFilename(uri));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageName,
                        RequestBody.create(imageFile, MediaType.parse(mime)))
                .addFormDataPart("vehicle_Registration","kaw123")
                .addFormDataPart("owner","Moses")
                .addFormDataPart("offence","Drunk Driving")
                .addFormDataPart("description","This is my description")
                .build();

//        RequestBody requestBody = new FormBody.Builder()
//                .add("owner","Moses")
//                .add("offence","Drunk Driving")
//                .add("description","This is my description")
//                .build();



        final CountingRequestBody.Listener progressListener = new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesRead, long contentLength) {
                if (bytesRead >= contentLength) {
                    if (progressBar != null)
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                } else {
                    if (contentLength > 0) {
                        final int progress = (int) (((double) bytesRead / contentLength) * 100);
                        if (progressBar != null)
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(progress);
                                }
                            });

                        if(progress >= 100){
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.e("uploadProgress called", progress+" ");
                    }
                }
            }
        };

        OkHttpClient imageUploadClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        if (originalRequest.body() == null) {
                            return chain.proceed(originalRequest);
                        }
                        Request progressRequest = originalRequest.newBuilder()
                                .method(originalRequest.method(),
                                        new CountingRequestBody(originalRequest.body(), progressListener))
                                .build();

                        return chain.proceed(progressRequest);

                    }
                })
                .build();



        Request request = new Request.Builder()
                .url("http://172.16.40.161:8000/api/admin/incident/createIncident")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();









        Call call2 = imageUploadClient.newCall(request);


        call2.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String mMessage = e.getMessage().toString();
                        Toast.makeText(MainActivity.this, "Error uploading file", Toast.LENGTH_LONG).show();
                        Log.e("failure Response", mMessage);
                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Success Uploading Image", Toast.LENGTH_LONG).show();

                    }
                });


                try {
                    Log.i("BodyString",response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



//        Call call = client.newCall(request);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        String mMessage = e.getMessage().toString();
//                        Toast.makeText(MainActivity.this, "Error uploading file", Toast.LENGTH_LONG).show();
//                        Log.e("failure Response", mMessage);
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Success Uploading Image", Toast.LENGTH_LONG).show();
//
//                    }
//                });
//
//
//                try {
//                    Log.i("BodyString",response.body().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });



//
//        imageUploadClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                String mMessage = e.getMessage().toString();
//                //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
//                Log.e("failure Response", mMessage);
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                final String mMessage = response.body().toString();
//
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Log.e(TAG, mMessage);
//                        progressBar.setVisibility(View.GONE);
//                        //upload.setVisibility(View.GONE);
//                    }
//                });
//
//            }
//
//
//
//
//
//
//
//
//
////            @Override
////            public void onFailure(retrofit2.Call call, IOException e) {
////                String mMessage = e.getMessage().toString();
////                //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
////                Log.e("failure Response", mMessage);
////            }
//
////            @Override
////            public void onResponse(retrofit2.Call call, retrofit2.Response response) {
////                final String mMessage = response.body().toString();
////
////                MainActivity.this.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        //Log.e(TAG, mMessage);
////                        progressBar.setVisibility(View.GONE);
////                        //upload.setVisibility(View.GONE);
////                    }
////                });
////            }
////
////            /**
////             * Invoked when a network exception occurred talking to the server or when an unexpected
////             * exception occurred creating the request or processing the response.
////             *
////             * @param call
////             * @param t
////             */
////            @Override
////            public void onFailure(retrofit2.Call call, Throwable t) {
//////                String mMessage = t.getMessage().toString();
//////                //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
//////                Log.e("failure Response", mMessage);
////
////            }
//        });






    }









}