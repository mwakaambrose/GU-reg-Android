package biz.kanamo.ambrose.gu_registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegStatus extends AppCompatActivity {

    Button request_reg_status;
    EditText reg_num;
    String registration;
    LinearLayout result_holder;
    TextView busar, dos, dof, ar;

    public static final String RECEIPT_UPLOAD_URL = "http://gu-reg.herokuapp.com/api/v1/check";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_status);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Requesting status");

        request_reg_status = (Button) findViewById(R.id.request);
        reg_num = (EditText) findViewById(R.id.reg_num);
        result_holder = (LinearLayout) findViewById(R.id.results);
        busar = (TextView) findViewById(R.id.busar);
        dos = (TextView) findViewById(R.id.dos);
        dof = (TextView) findViewById(R.id.dof);
        ar = (TextView) findViewById(R.id.ar);
        request_reg_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration = reg_num.getText().toString();
                if (registration.isEmpty()){
                    Toast.makeText(RegStatus.this, "Please Provide a registration number", Toast.LENGTH_SHORT).show();
                }
                new OkHttpHandler().execute();
            }
        });
    }

    public class OkHttpHandler extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }
        OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(String... params) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("reg_num", registration)
                    .build();
            Request request = new Request.Builder()
                    .url(RECEIPT_UPLOAD_URL)
                    .post(requestBody)
                    .build();
            Response response ;
            try {
                response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                showResults(object.getString("Busar"), object.getString("Dean of Students"), object.getString("Faculty Dean"), object.getString("Accademic Registrar"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("XYT", s);
        }
    }

    private void showResults(String string, String string1, String string2, String string3) {
        result_holder.setVisibility(View.VISIBLE);
        busar.setText(string);
        dos.setText(string1);
        dof.setText(string2);
        ar.setText(string3);
    }
}
