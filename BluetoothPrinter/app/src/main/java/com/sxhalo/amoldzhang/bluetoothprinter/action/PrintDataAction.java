package com.sxhalo.amoldzhang.bluetoothprinter.action;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.amoldzhang.bluetoothprinter.R;
import com.sxhalo.amoldzhang.bluetoothprinter.service.PrintDataService;

/**
 * Created by amoldZhang on 2017/6/12.
 */
public class PrintDataAction implements OnClickListener {
    private Context context = null;
    private TextView deviceName = null;
    private TextView connectState = null;
    private EditText printData = null;
    private String deviceAddress = null;
    private PrintDataService printDataService = null;

    public PrintDataAction(Context context, String deviceAddress,
                           TextView deviceName, TextView connectState) {
        super();
        this.context = context;
        this.deviceAddress = deviceAddress;
        this.deviceName = deviceName;
        this.connectState = connectState;
        this.printDataService = new PrintDataService(this.context,
                this.deviceAddress);
        this.initView();

    }

    private void initView() {
        // 锟斤拷锟矫碉拷前锟借备锟斤拷锟斤拷
        this.deviceName.setText(this.printDataService.getDeviceName());
        // 一锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟借备
        boolean flag = this.printDataService.connect();
        if (flag == false) {
            // 锟斤拷锟斤拷失锟斤拷
            this.connectState.setText("锟斤拷锟斤拷失锟杰ｏ拷");
        } else {
            // 锟斤拷锟接成癸拷
            this.connectState.setText("锟斤拷锟接成癸拷锟斤拷");

        }
    }

    public void setPrintData(EditText printData) {
        this.printData = printData;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            String sendData = this.printData.getText().toString();
            this.printDataService.send(sendData + "\n");
        } else if (v.getId() == R.id.command) {
            this.printDataService.selectCommand();

        }
    }
}