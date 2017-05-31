package biz.kanamo.ambrose.gu_registration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class RegStatus extends AppCompatActivity {

    Button request_reg_status;
    LinearLayout result_holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_status);

        request_reg_status = (Button) findViewById(R.id.request);
        result_holder = (LinearLayout) findViewById(R.id.results);
        request_reg_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_holder.setVisibility(View.VISIBLE);
            }
        });
    }
}
