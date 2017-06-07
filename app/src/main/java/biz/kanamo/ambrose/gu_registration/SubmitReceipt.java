package biz.kanamo.ambrose.gu_registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.w3c.dom.Text;

import java.io.File;
import java.util.UUID;
import java.util.jar.Manifest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SubmitReceipt extends AppCompatActivity {
    private static final int STORAGE_SERVICE_PERMISSION_REQUEST_CODE = 1200;
    File file;
    Uri file_uri;
    String file_path;
    TextView tuition_path, general_path, guild_path;
    Button tuition, general, guild, uploadReceipt;
    EditText reg_number;

    public static final String RECEIPT_UPLOAD_URL = "http://gu-reg.herokuapp.com/api/v1/receipt";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_receipt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting receipts");

        initEntries();
        requestStoragePermission();
        tuition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Tuition receipt picture"), 1);
            }
        });

        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose General receipt picture"), 12);
            }
        });

        guild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Guild receipt picture"), 13);
            }
        });

        uploadReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reg_number.getText().toString().length() > 0){
                    new OkHttpHandler().execute();
                }else {
                    Toast.makeText(SubmitReceipt.this, "Registration Number Required with all the receipts.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reciept_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_show_register){
            startActivity(new Intent(this, RegStatus.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_SERVICE_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else {
                finish();
                Toast.makeText(this, "Permission Required: To be able to choose receipts from storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            file_uri = data.getData();
            file_path = getRealPathFromUri(file_uri);
            if (file_path != null){
                tuition_path.setText(file_path);
            }else if (file_path == null){
                file_path = file_uri.getPath();
                tuition_path.setText(file_path);
            }else{
                Toast.makeText(getApplicationContext(), "You need to take a pickture", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == 12){
            file_uri = data.getData();
            file_path = getRealPathFromUri(file_uri);
            if (file_path != null){
                general_path.setText(file_path);
            }else if (file_path == null){
                file_path = file_uri.getPath();
                general_path.setText(file_path);
            }else{
                Toast.makeText(getApplicationContext(), "You need to take a pickture", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == 13){
            file_uri = data.getData();
            file_path = getRealPathFromUri(file_uri);
            if (file_path != null){
                guild_path.setText(file_path);
            }else if (file_path == null){
                file_path = file_uri.getPath();
                guild_path.setText(file_path);
            }else{
                Toast.makeText(getApplicationContext(), "You need to take a pickture", Toast.LENGTH_LONG).show();
            }
        }
        else if ( resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Please take a picture, its required for registration.", Toast.LENGTH_SHORT).show();
        }

    }

    public void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, STORAGE_SERVICE_PERMISSION_REQUEST_CODE);
    }

    public void initEntries(){
        tuition = (Button) findViewById(R.id.tuition);
        general = (Button) findViewById(R.id.general);
        guild = (Button) findViewById(R.id.guild);
        uploadReceipt = (Button) findViewById(R.id.uploadReceipts);

        tuition_path = (TextView) findViewById(R.id.tuition_path);
        general_path = (TextView) findViewById(R.id.general_path);
        guild_path = (TextView) findViewById(R.id.guild_path);

        reg_number = (EditText) findViewById(R.id.reg_number);
    }

    public void resetEntries() {
        tuition_path.setText("");
        general_path.setText("");
        guild_path.setText("");
    }

    private String getRealPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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

    public String uploadReceiptsToServer(){
        try{
            String uuid = UUID.randomUUID().toString();
            return new MultipartUploadRequest(this, uuid, RECEIPT_UPLOAD_URL)
                    .addParameter("reg_num", reg_number.getText().toString())
                    .addParameter("is_approved", "0")
                    .addFileToUpload(tuition_path.getText().toString(), "tuition")
                    .addFileToUpload(general_path.getText().toString(), "general")
                    .addFileToUpload(guild_path.getText().toString(), "guild")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public class OkHttpHandler extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadReceiptsToServer();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Toast.makeText(SubmitReceipt.this, "Check progress in notification menu, if failed, try again next time with faster internet", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SubmitReceipt.this, MainActivity.class));
            Log.d("XYT", s);
        }
    }
}
