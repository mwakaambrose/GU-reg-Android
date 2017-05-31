package biz.kanamo.ambrose.gu_registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String REGISTRATION_UPLOAD_URL = "";
    ImageView passport_pic;
    String encoded_string, image_name;
    Bitmap bitmap;
    File file;
    Uri file_uri;
    String file_path;
    Button register;
    EditText academic_year, semester, reg_no, surnname, other_names,
    tel_num, other_tel_num, gender, marital_status, dob, disability, home_country,
    citizenship, home_district, sub_county, home_address, other_address,
    parent_gurdians_name, parents_tel_no, parents_sub_county, parents_village,
    parents_next_of_kin, parents_next_of_kin_tel_no, programme, faculty, sponsor,
    year_of_study, mode_of_attendance;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting details...");

        initEntries();

        passport_pic = (ImageView) findViewById(R.id.passport_pic);
        passport_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Passport picture"), 1993);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show progress dialog
                progressDialog.show();
                // submit form,
                String results = submitRegistrationData();
                // dismiss dialog
                progressDialog.dismiss();
                // check response.
                // if success,
                resetEntries();
                //if not sucess dont clear error. show it in a dialog.

                Toast.makeText(MainActivity.this, results, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == 1993 && data.getData() != null){
            file_uri = data.getData();
            file_path = file_uri.getPath();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_uri);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else if ( resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Please take a picture, its required for registration.", Toast.LENGTH_SHORT).show();
        }

        passport_pic.setImageBitmap(bitmap);
    }

    private String getRealPathFromUri(Uri file_uri) {
        Cursor cursor = getContentResolver().query(file_uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);

        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID + "=?",
                new String[]{document_id},
                null
        );

        cursor.moveToFirst();
        String absolutePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return absolutePath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.show_reg_status){
            startActivity(new Intent(this, RegStatus.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void initEntries(){

        register = (Button) findViewById(R.id.register);

        academic_year = (EditText) findViewById(R.id.academic_year);
        semester = (EditText) findViewById(R.id.semester);
        reg_no = (EditText) findViewById(R.id.reg_num);
        surnname = (EditText) findViewById(R.id.surname);
        other_names = (EditText) findViewById(R.id.othername);
        tel_num = (EditText) findViewById(R.id.tel_num);
        other_tel_num = (EditText) findViewById(R.id.other_tel_num);
        gender = (EditText) findViewById(R.id.gender);
        marital_status = (EditText) findViewById(R.id.marital_status);
        dob = (EditText) findViewById(R.id.dob);
        disability = (EditText) findViewById(R.id.disability);
        home_country = (EditText) findViewById(R.id.home_country);
        citizenship = (EditText) findViewById(R.id.citizenship);
        home_district = (EditText) findViewById(R.id.home_district);
        sub_county = (EditText) findViewById(R.id.sub_county);
        home_address = (EditText) findViewById(R.id.home_address);
        other_address = (EditText) findViewById(R.id.other_address);
        parent_gurdians_name = (EditText) findViewById(R.id.parents_gurdians_name);
        parents_tel_no = (EditText) findViewById(R.id.parents_tel_num);
        parents_sub_county = (EditText) findViewById(R.id.parents_subcounty);
        parents_village = (EditText) findViewById(R.id.parents_village);
        parents_next_of_kin = (EditText) findViewById(R.id.parents_next_of_kin);
        parents_next_of_kin_tel_no = (EditText) findViewById(R.id.parents_next_of_kin_tel_num);
        programme = (EditText) findViewById(R.id.programme);
        faculty = (EditText) findViewById(R.id.faculty);
        sponsor = (EditText) findViewById(R.id.sponsor);
        year_of_study = (EditText) findViewById(R.id.year_of_study);
        mode_of_attendance = (EditText) findViewById(R.id.mode_of_attendance);
    }

    public void getEntries(){

    }

    public void resetEntries(){
        academic_year.setText("");
        semester.setText("");
        reg_no.setText("");
        surnname.setText("");
        other_names.setText("");
        tel_num.setText("");
        other_tel_num.setText("");
        gender.setText("");
        marital_status.setText("");
        dob.setText("");
        disability.setText("");
        home_country.setText("");
        citizenship.setText("");
        home_district.setText("");
        sub_county.setText("");
        home_address.setText("");
        other_address.setText("");
        parent_gurdians_name.setText("");
        parents_tel_no.setText("");
        parents_sub_county.setText("");
        parents_village.setText("");
        parents_next_of_kin.setText("");
        parents_next_of_kin_tel_no.setText("");
        programme.setText("");
        faculty.setText("");
        sponsor.setText("");
        year_of_study.setText("");
        mode_of_attendance.setText("");
    }

    public String submitRegistrationData(){
        String uuid = UUID.randomUUID().toString();
        try{
           new MultipartUploadRequest(this, uuid, REGISTRATION_UPLOAD_URL)
                    .addFileToUpload(getRealPathFromUri(file_uri), "passport")
                    .addParameter(academic_year.getText().toString(), "academic_year") //add more fields here.
                    .addParameter(surnname.getText().toString(), "surname") //add more fields here.
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload();

        }catch (Exception e){
            e.printStackTrace();
        }
        return "Response is a success";
    }
}
