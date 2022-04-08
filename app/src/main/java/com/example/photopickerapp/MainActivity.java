

package com.example.photopickerapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextInputLayout textFieldVehicleRegistration, textFieldOffender, textFieldOffenses, textFieldDescription,textFieldOffences_menu,textFieldDL_Number_or_IDNumber;
    Button buttonSubmit,buttonAddOffender, buttonAddOffences, buttonWitnessReport, buttonAddPhotos, buttonTakeAlbumPhoto,button_submit_offence;

    CircularProgressIndicator circularProgressIndicator;

    AutoCompleteTextView autoCompleteOffence, autoCompleteSelectOffender;
    ArrayAdapter arrayAdapter, arrayAdapterOffencesDetails;
    ArrayList<String> offencesArrayList;
    ArrayList<String> offenderDetailsArrayList;
    private String[] PERMISSIONS;
    private ImageView fileReportImageView;
    private String tempImageFilePath = "";
    private Uri imageViewUri;


    String selectedOffence = "";
    String selectOffenderDetail = "";



    ActivityResultLauncher<String> startForResultHere = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Log.i("InsideOnResultTrue","Inside Boolean True Here");
            Log.i("GetPath",result.getPath());
            Log.i("tempImageFilePath",tempImageFilePath);

            //imageView.setImageURI(Uri.parse(tempImageFilePath));

        }
    });


    ActivityResultLauncher<Uri> startForResult = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {

            if (result){
                Log.i("InsideOnResultTrue","Inside Boolean True");
                //imageView.setImageURI(imageViewUri);

            }
            else{
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
                    takePicture();

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

        //arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.option_item, offencesArrayList);
        //arrayAdapterOffencesDetails = new ArrayAdapter(MainActivity.this, R.layout.option_item, offenderDetailsArrayList);


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
                uploadReport();

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
                takePicture();
            }

        }
    }

    public void takePicture(){
//
//        startForResult.launch();
//
//        tempImageUri = FileProvider.getUriForFile(this,"com.example.cameraapp.provider",createImageFile().also {
//            tempImageFilePath = it.absolutePath
//        })



        File fileCreateImageFile = createImageFile().getAbsoluteFile();

        String fileGetAbsolutePath = createImageFile().getAbsolutePath();

        Log.i("FileCreateImageFile", String.valueOf(fileCreateImageFile));
        Log.i("FileGetAbsolutePath", fileGetAbsolutePath);

        Uri file = FileProvider.getUriForFile(this,"com.example.photopickerapp.provider", new File(fileGetAbsolutePath));

        tempImageFilePath = file.getPath();


        Log.i("FileGetPath",file.getPath());
        tempImageFilePath = file.getPath();
        //imageView.setImageURI(Uri.parse(file.getPath()));

        startForResult.launch(file);




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
}