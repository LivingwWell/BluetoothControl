///*
// * Copyright (C) 2009 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.example.bluetoothcontrol;
//
//import java.util.Set;
//
//
//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Toast;
//
///**
// *此活动显示为对话框。 它列出了所有配对设备和
// *发现后在该区域检测到的设备。 选择设备时
// *由用户将设备的MAC地址发送回父设备
// *结果中的活动意图。
// */
//
//public class DeviceListActivity extends Activity {
//    // Debugging
//    private static final String TAG = "DeviceListActivity";
//    private static final boolean D = true;
//
//    //返回意图额外
//    public static String EXTRA_DEVICE_ADDRESS = "device_address";
//
//    //会员字段
//    private BluetoothAdapter mBtAdapter;
//    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
//    private ArrayAdapter<String> mNewDevicesArrayAdapter;
//
//    private ListView pairedListView,newDevicesListView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //设置窗口
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        setContentView(R.layout.devicelist);
//
//        //设置结果CANCELED使用户退出
//        setResult(Activity.RESULT_CANCELED);
//
//        //初始化按钮以执行设备发现
//        Button scanButton = (Button) findViewById(R.id.button_scan);
//        scanButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                doDiscovery();
//                v.setVisibility(View.GONE);
//                pairedListView.setVisibility(View.GONE);
//            }
//        });
//
//        // Initialize array adapters. One for already paired devices and
//        // one for newly discovered devices
//        //mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.devicelist);
//        //mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.devicelist);android.R.layout.test_list_item
//        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);
//        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);
//        //查找并设置配对设备的ListView
//        pairedListView = (ListView) findViewById(R.id.paired_devices);
//        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
//        pairedListView.setOnItemClickListener(mDeviceClickListener);
//
//
//        //为新发现的设备查找并设置ListView
//        newDevicesListView = (ListView) findViewById(R.id.new_devices);
//        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
//        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
//
//        //在发现设备时注册广播
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        this.registerReceiver(mReceiver, filter);
//
//        //发现完成后注册广播
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.registerReceiver(mReceiver, filter);
//
//        //获取本地蓝牙适配器
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        //获取一组当前配对的设备
//        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//
//        //如果有配对设备，请将每个设备添加到ArrayAdapter
//        if (pairedDevices.size() > 0) {
//            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
//            for (BluetoothDevice device : pairedDevices) {
//                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//            }
//        } else {
//            String noDevices = getResources().getText(R.string.none_paired).toString();
//            mPairedDevicesArrayAdapter.add(noDevices);
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            //申请权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    1);
//        }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            //申请权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    2);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1: //如果取消请求，结果数组为空。
//                if ((grantResults.length > 0)
//                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED));
//                else Toast.makeText(this,"app没有定位权限将无法搜索蓝牙设备!",Toast.LENGTH_LONG);
//                break;
//            case 2: if ((grantResults.length > 0)
//                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED));
//            else Toast.makeText(this,"app没有定位权限将无法搜索蓝牙设备!",Toast.LENGTH_LONG);
//                break;
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        //确保我们不再进行发现了
//        if (mBtAdapter != null) {
//            mBtAdapter.cancelDiscovery();
//        }
//
//        //取消注册广播监听器
//        this.unregisterReceiver(mReceiver);
//    }
//
//    /**
//     *使用BluetoothAdapter启动设备发现
//     */
//    private void doDiscovery() {
//        if (D) Log.d(TAG, "doDiscovery()");
//
//        //表示标题中的扫描
//        setProgressBarIndeterminateVisibility(true);
//        setTitle(R.string.scanning);
//
//        //为新设备启用子标题
//        findViewById(R.id.title_paired_devices).setVisibility(View.GONE);
//
//        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
//
//        //如果我们已经发现了，请停止它
//        if (mBtAdapter.isDiscovering()) {
//            mBtAdapter.cancelDiscovery();
//        }
//        mNewDevicesArrayAdapter.clear();
//        mNewDevicesArrayAdapter.notifyDataSetChanged();
//        //从BluetoothAdapter请求发现
//        mBtAdapter.startDiscovery();
//    }
//
//    // ListView中所有设备的单击监听器
//    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
//        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
//            //取消发现，因为它很昂贵而且我们即将连接
//            mBtAdapter.cancelDiscovery();
//
//            //获取设备MAC地址，即视图中的最后17个字符
//            String info = ((TextView) v).getText().toString();
//            if(info.length()<17)return;
//
//            String address = info.substring(info.length() - 17);
//
//            //创建结果Intent并包含MAC地址
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
//
//            //设置结果并完成此活动
//            setResult(Activity.RESULT_OK, intent);
//            finish();
//        }
//    };
//
//    //侦听已发现设备的BroadcastReceiver
//    //在发现完成后更改标题
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            //当发现找到设备时
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                //从Intent获取BluetoothDevice对象
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                //如果它已经配对，请跳过它，因为它已经被列出了
//                // if（device.getBondState（）！= BluetoothDevice.BOND_BONDED）{
//                if(mNewDevicesArrayAdapter.getPosition(device.getName() + "\n" + device.getAddress()) == -1){
//                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                }
//                //完成发现后，更改活动标题
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                setProgressBarIndeterminateVisibility(false);
//                setTitle(R.string.select_device);
//                if (mNewDevicesArrayAdapter.getCount() == 0) {
//                    String noDevices = getResources().getText(R.string.none_found).toString();
//                    mNewDevicesArrayAdapter.add(noDevices);
//                }
//                findViewById(R.id.button_scan).setVisibility(View.VISIBLE);
//            }
//        }
//    };
//
//}
