package com.sxhalo.amoldzhang.bluetoothprinter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.amoldzhang.bluetoothprinter.R;
import com.sxhalo.amoldzhang.bluetoothprinter.action.PrintDataAction;
import com.sxhalo.amoldzhang.bluetoothprinter.service.PrintDataService;

/**
 * Created by amoldZhang on 2017/6/12.
 */

public class PrintDataActivity extends AppCompatActivity {

    private Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("蓝牙打印");
        this.setContentView(R.layout.printdata_layout);
        this.context = this;
        this.initListener();
    }

    /**
     * 获得从上一个Activity传来的蓝牙地址
     * @return String
     */
    private String getDeviceAddress() {
        // 直接通过Context类的getIntent()即可获取Intent
        Intent intent = this.getIntent();
        // 判断
        if (intent != null) {
            return intent.getStringExtra("deviceAddress");
        } else {
            return null;
        }
    }

    private void initListener() {
        TextView deviceName = (TextView) this.findViewById(R.id.device_name);
        TextView connectState = (TextView) this
                .findViewById(R.id.connect_state);

        PrintDataAction printDataAction = new PrintDataAction(this.context,
                this.getDeviceAddress(), deviceName, connectState);

        EditText printData = (EditText) this.findViewById(R.id.print_data);
        Button send = (Button) this.findViewById(R.id.send);
        Button command = (Button) this.findViewById(R.id.command);
        printDataAction.setPrintData(printData);

        send.setOnClickListener(printDataAction);
        command.setOnClickListener(printDataAction);
    }


    @Override
    protected void onDestroy() {
        PrintDataService.disconnect();
        super.onDestroy();
    }

}
