package com.a1anwang.beacondemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String TAG="MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    EditText edit_uuid,edit_major,edit_minor;
Button btn_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager != null) {
            mBluetoothAdapter = manager.getAdapter();
        }

        edit_uuid= (EditText) findViewById(R.id.edit_uuid);
        edit_major= (EditText) findViewById(R.id.edit_major);
        edit_minor= (EditText) findViewById(R.id.edit_minor);
        btn_start= (Button) findViewById(R.id.btn_start);

    }


    public void startAction(View v){
        if(!mBluetoothAdapter.isEnabled()){
            ToastUtils.showToast(this,"请开启蓝牙",2000);
            return;
        }
        String uuid =edit_uuid.getText().toString().toUpperCase();
        boolean isvaliduuid= isValidUUID(uuid);
        if(!isvaliduuid){
            ToastUtils.showToast(this,"UUID 格式不正确",2000);
            return;
        }
        int major = -1;
        if (!TextUtils.isEmpty(edit_major.getText().toString())) {
            major = Integer.parseInt(edit_major.getText().toString());
        }
        int minor = -1;
        if (!TextUtils.isEmpty(edit_minor.getText().toString())) {
            minor = Integer.parseInt(edit_minor.getText().toString());
        }
        if (major < 0 || major > 65535) {
            ToastUtils.showToast(this,"major ranges：0- 65535",2000);
            return;
        }
        if (minor < 0 || minor > 65535) {
            ToastUtils.showToast(this,"minor ranges：0- 65535",2000);
            return;
        }
        startAdvertise(uuid,major,minor);
    }

    public void stopAction(View v){
        stopAdvertise();
    }



    private void startAdvertise(String uuid, int major, int minor) {
        if (mBluetoothAdapter == null) {
            return;
        }
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }
        if (mBluetoothLeAdvertiser != null) {

            AdvertiseSettings settings=createAdvertiseSettings(0);
            AdvertiseData data=createAdvertiseData( uuid ,  major, minor, -59);
            //-59是 measuredPower,一般设备默认都是-59，这里固定了
            mBluetoothLeAdvertiser.startAdvertising(settings ,data  ,  mAdvCallback);
        }
    }


    private void stopAdvertise() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvCallback);
            mBluetoothLeAdvertiser = null;

        }
        btn_start.setText("开启广播");
        btn_start.setEnabled(true);
    }


    public AdvertiseSettings createAdvertiseSettings(  int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);

        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        return builder.build();
    }


    public AdvertiseData createAdvertiseData(String uuidString, int major,
                                             int minor, int txPower) {
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        String beaconType="0215"; //按照apple iBeacon协议
        String uuid=uuidString.replace("-","");
        String majorStr= Conversion.formatStringLenth(4,Integer.toHexString(major),'0');
        String minorStr=Conversion.formatStringLenth(4,Integer.toHexString(minor),'0');
        String measuredPower=Conversion.formatStringLenth(2,Integer.toHexString(txPower),'0');

        String dataStr=beaconType+uuid+majorStr+minorStr+measuredPower;

        byte[] data=Conversion.hexStringToBytes( dataStr);
        mDataBuilder.addManufacturerData(0x004C, data);//004c是厂商id，代表apple公司
        AdvertiseData mAdvertiseData = mDataBuilder.build();
        return mAdvertiseData;

    }




    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            if (settingsInEffect != null) {
                Log.e("debug", "onStartSuccess TxPowerLv="
                        + settingsInEffect.getTxPowerLevel()
                        + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e("debug", "onStartSuccess, settingInEffect is null");
            }
            btn_start.setText("已开启");
            btn_start.setEnabled(false);
            ToastUtils.showToast(MainActivity.this,"开启成功",1000);
        }

        public void onStartFailure(int errorCode) {
            Log.e("debug", "onStartFailure errorCode=" + errorCode);
            ToastUtils.showToast(MainActivity.this,"开启失败" + errorCode,1000);
        }
    };



    public static boolean isValidUUID(String uuid){
        String regEx = "^[a-fA-F0-9]{8}[-][a-fA-F0-9]{4}[-][a-fA-F0-9]{4}[-][a-fA-F0-9]{4}[-][a-fA-F0-9]{12}$";  //FDA50693-A4E2-4FB1-AFCF-C6EB07647825
        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(uuid);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();

        return rs;
    }
}
