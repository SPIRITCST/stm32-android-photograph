package com.example.cst.bluetooth_1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class scan_blueooth extends Activity {
    private final static int REQUEST_ENABLE_BT = 1;


    private BluetoothAdapter mBluetoothAdapter;
    private Button mScanBtn;
    private ListView mDeviceListView;


    private ArrayAdapter<String> adapter;
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_blueooth);

        initView();//初始化界面
        openBlueTooth();
        findPirBlueTooth();

        IntentFilter inflater=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        inflater.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver,inflater);


    }
    //界面初始化
    private void initView(){
        mScanBtn=(Button)findViewById(R.id.scan_device);
        mScanBtn.setOnClickListener(onClick);
        mDeviceListView=(ListView)findViewById(R.id.device_list);
        mDeviceListView.setOnItemClickListener(onClickItem);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mArrayList);
        mDeviceListView.setAdapter(adapter);
        System.out.println("1111111111111111111111111111111111111111111111111111111");
    }
    //扫描蓝牙点击事件
    View.OnClickListener onClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openBlueTooth();
            if (!mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.startDiscovery();
            }
        }
    };
    //蓝牙选择事件
    AdapterView.OnItemClickListener onClickItem=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            BluetoothDevice selectDevice=mDeviceList.get(position);
            Toast.makeText(scan_blueooth.this,selectDevice.getName()+selectDevice.getAddress()
                    ,Toast.LENGTH_SHORT).show();
            System.out.println("2222222222222222222222222222222222222222222222222");
            ConnectThread connectThread=new ConnectThread(selectDevice);
            connectThread.start();
            System.out.println("3333333333333333333333333333333333333333333333333333");
        }
    };
    //打开蓝牙
    private void openBlueTooth(){
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null)
            return;
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
    }
    //已配对的蓝牙
    private void findPirBlueTooth(){
        Set<BluetoothDevice> pirDevices=mBluetoothAdapter.getBondedDevices();
        if (pirDevices.size()>0){
            for (BluetoothDevice device:pirDevices){
                mArrayList.add(device.getName()+"\n"+device.getAddress());
                mDeviceList.add(device);
            }
            adapter.notifyDataSetChanged();
        }        System.out.println("33333333333333333333333333333333333333");
    }
    //动态广播接收器
    private final BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            //如果找到新的蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)){   //会进IF，只是蓝牙搜索效果不好
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDeviceList.contains(device)) {
                    return;
                }
                mArrayList.add(device.getName()+"\n"+device.getAddress());
                mDeviceList.add(device);
                adapter.notifyDataSetChanged();
            }
            //如果扫描结束
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            }
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    private class ConnectThread extends Thread {
        private  final BluetoothSocket mmSocket;
       // private  final BluetoothDevice mmDevice;
        public final String s = "00001101-0000-1000-8000-00805F9B34FB";
        private Boolean isConnect  = false;
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            //mmDevice = device;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(s));
            } catch (IOException e) { }
            mmSocket = tmp;
            if (mmSocket==null)
                System.out.println("00000000000000000000000000000000000000");
            System.out.println("9999999999999999999999999999999999999");
            System.out.println(mmSocket);
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            try {
                System.out.println("8888888888888888888888888888888888888888888888");
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                System.out.println("连接成功");
                System.out.println("666666666666666666666666666666666666666666666");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                    System.out.println("7777777777777777777777777777777777777777777");
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
