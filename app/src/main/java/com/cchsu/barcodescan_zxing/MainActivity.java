package com.cchsu.barcodescan_zxing;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import static android.Manifest.permission.*;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.R;
import com.google.zxing.client.android.Intents.Scan;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.integration.android.IntentIntegrator;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private final int CAMERA_PERMISSION_REQUEST = 0;
    private boolean mDeniedCameraAccess = false;

    private final int REQUEST_CODE = 0xa1;
    private TextView order_no;
    private TextView result_type;
    private TextView result_value;
    private Button scan_single;
    private Button scan_batch;

    private boolean isSingleSacn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 檢查允許使用相機權限
        grantCameraPermissionsThenStartScanning();
        if (mDeniedCameraAccess == true)
            return;

        order_no = (TextView)findViewById(R.id.txt_orderno);
        result_type = (TextView)findViewById(R.id.txt_result_type);
        result_value = (TextView)findViewById(R.id.txt_result_value);
        scan_single = (Button)findViewById(R.id.btn_single_scan);
        scan_batch = (Button)findViewById(R.id.btn_batch_scan);

        scan_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSingleSacn = true;
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.setAction(Scan.ACTION); //啟動掃描動作，一定要設定
                //intent.putExtra(Scan.WIDTH, 1600); //調整掃描視窗寬度(Optional)
                //intent.putExtra(Scan.HEIGHT, 850); //調整掃描視窗高度(Optional)
                intent.putExtra(Scan.RESULT_DISPLAY_DURATION_MS, 100L); //設定掃描成功地顯示時間(Optional)
                intent.putExtra(Scan.PROMPT_MESSAGE, "請將條碼置於鏡頭範圍進行掃描"); //客製化掃描視窗的提示文字(Optional)
                //intent.putExtra(Scan.MODE, Scan.ONE_D_MODE);  //限制只能掃一維條碼(預設為全部條碼都支援)
                intent.putExtra(CaptureActivity.SCAN_MODE_NAME, CaptureActivity.SCAN_SIGLE_MODE);
                BeepManager.VIBRATE_MODE = true; //掃描成功發出振動:true, 沒有震動:false
                CameraManager.FORCE_FLASH_MODE = false;
                CameraManager.FORCE_AUTO_FOCUS = true;
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        scan_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSingleSacn = false;
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.setAction(Scan.ACTION);
                //intent.putExtra(Scan.WIDTH, 1600); //調整掃描視窗寬度(Optional)
                //intent.putExtra(Scan.HEIGHT, 850); //調整掃描視窗高度(Optional)
                intent.putExtra(Scan.PROMPT_MESSAGE, "請將條碼置於鏡頭範圍進行掃描");
                intent.putExtra(Scan.RESULT_DISPLAY_DURATION_MS, 10L);
                intent.putExtra(CaptureActivity.SCAN_MODE_NAME, CaptureActivity.SCAN_BATCH_MODE);
                BeepManager.VIBRATE_MODE = true;
                CameraManager.FORCE_FLASH_MODE = false;
                CameraManager.FORCE_AUTO_FOCUS = true;
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null){
                result.setText("掃描结果:\n" + data.getStringExtra("resultCode"));
            }
        } **/

        /**
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                result_type.setText("條碼類型:\n" + data.getStringExtra(Scan.RESULT_FORMAT));
                result_value.setText("條碼數值:\n" + data.getStringExtra(Scan.RESULT));
            }
        } **/

        if (isSingleSacn == true) {
            if (data != null) {
                CaptureActivity.Number_Order = 1;
                CaptureActivity.Barcode_Type = data.getStringExtra(Scan.RESULT_FORMAT);
                CaptureActivity.Barcode_Value = data.getStringExtra(Scan.RESULT);
            }
        }

        order_no.setText("掃描筆數: " + CaptureActivity.Number_Order);
        result_type.setText("條碼類型: " + CaptureActivity.Barcode_Type);
        result_value.setText("條碼數值: " + CaptureActivity.Barcode_Value);

        // 驗證連續掃描以List儲存的功能
        if (isSingleSacn == false) {
            String strSanString = "";
            java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            for (int nn = 0; nn < CaptureActivity.SCAN_BATCH_VALUE.size(); nn++)
                strSanString += ("[" + sdf.format(CaptureActivity.SCAN_BATCH_VALUE.get(nn).brushDate) + "]" + CaptureActivity.SCAN_BATCH_VALUE.get(nn).strOrderNo + ",");
            Toast.makeText(this, strSanString, Toast.LENGTH_LONG).show();
        }
    }

    private void grantCameraPermissionsThenStartScanning()
    {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                mDeniedCameraAccess = false;
            else
                mDeniedCameraAccess = true;
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
