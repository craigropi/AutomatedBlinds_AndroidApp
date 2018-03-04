package com.craigropi.automatedblinds;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    //private BluetoothGatt mBluetoothGatt;
    private BluetoothGatt lightSwitchBluetoothGatt;
    private BluetoothGatt blindsBluetoothGatt;


    byte[] zeroByte = new byte[] {0};
    byte[] oneByte = new byte[] {1};
    byte[] twoByte = new byte[] {2};
    byte[] threeByte = new byte[] {3};

    //public int mConnectionState = STATE_DISCONNECTED;
    public int lightSwitchConnectionState = STATE_DISCONNECTED;
    public int blindsConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;


    //private int mServicesState = NOT_DISCOVERED;
    private int lightSwitchServicesState = NOT_DISCOVERED;
    private int blindsServicesState = NOT_DISCOVERED;
    private static final int NOT_DISCOVERED = 0;
    private static final int DISCOVERED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Automatically attempt to connect to the lightswitch when the app opens.
        connectToBluetoothDevice("Remote Lightswitch");
    }

    // Called when the user taps the Connect to Lightswitch button
    public void connectToLightswitch(View view) {
        connectToBluetoothDevice("Remote Lightswitch");
    }

    // Called when the user taps the Connect to Blinds button
    public void connectToBlinds(View view) {
        connectToBluetoothDevice("Automated Blinds");
    }

    /** Called when the user taps the Dark button */
    public void makeItDark(View view) {
        flipSwitch(oneByte);
    }

    /** Called when the user taps the Light button */
    public void makeItLight(View view) {
        flipSwitch(zeroByte);
    }

    /** Called when the user taps the Light button */
    public void nudgeUp(View view) {
        rollBlinds(zeroByte);
    }

    /** Called when the user taps the Light button */
    public void nudgeDown(View view) {
        rollBlinds(oneByte);
    }

    /** Called when the user taps the Light button */
    public void rollUp(View view) {
        rollBlinds(twoByte);
    }

    /** Called when the user taps the Light button */
    public void rollDown(View view) {rollBlinds(threeByte); }

    //Actually flip the lightswitch either up (inputByte = {0}) or down (inputByte = {1})
    private void flipSwitch(byte[] inputByte) {
        String flipAttemptResult = "Failed to flip switch";
        String mServiceUUIDstring = "19b10000-e8f2-537e-4f6c-d104768a8a8a";
        String mCharacteristicUUIDstring = "19b10001-e8f2-537e-4f6c-d104768a8a8a";

        //Check to make sure the Remote Lightswitch is connected.
        if (lightSwitchConnectionState != STATE_CONNECTED) {
            flipAttemptResult = "Remote Lightswitch is not connected";
        } else if(lightSwitchServicesState != DISCOVERED) {
            //Check to make sure the BLE devices services finished being discovered before attempting to modify them.
            flipAttemptResult = "Could not retrieve Remote Lightswitch Bluetooth LE services";
        } else {
            //List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
            List<BluetoothGattService> gattServices = lightSwitchBluetoothGatt.getServices();
            if (gattServices != null) {
                //Loops through the BLE device's available services
                for (BluetoothGattService gattService : gattServices) {
                    String gattServiceUuid = gattService.getUuid().toString();
                    if (gattServiceUuid.equals(mServiceUUIDstring)) {
                        List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        // Loops through the BLE device's available characteristics.
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                            String gattCharacteristicUuid = gattCharacteristic.getUuid().toString();
                            if (gattCharacteristicUuid.equals(mCharacteristicUUIDstring)) {
                                //Setting this characteristic tells the Remote Lightswitch to Flip
                                gattCharacteristic.setValue(inputByte);
                                if (inputByte == zeroByte) {
                                    flipAttemptResult = "Let there be Light!";
                                } else if (inputByte == oneByte) {
                                    flipAttemptResult = "Is this darkness in you too?";
                                }
                            }
                            //mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                            lightSwitchBluetoothGatt.writeCharacteristic(gattCharacteristic);
                        }
                    }
                }
            }
        }
        displayShortMessage(flipAttemptResult);
    }

    //Actually roll the blinds either up (inputByte = {0}) or down (inputByte = {1})
    private void rollBlinds(byte[] inputByte) {
        String rollAttemptResult = "Failed to roll Blinds";
        String mServiceUUIDstring = "19b10000-e8f2-537e-4f6c-d104768a1214";
        String mCharacteristicUUIDstring = "19b10001-e8f2-537e-4f6c-d104768a1214";

        //Check to make sure the Automated Blinds are connected.
        if (blindsConnectionState != STATE_CONNECTED) {
            rollAttemptResult = "Automated Blinds are not connected";
        } else if(blindsServicesState != DISCOVERED) {
            //Check to make sure the BLE devices services finished being discovered before attempting to modify them.
            rollAttemptResult = "Could not retrieve Automated Blinds Bluetooth LE services";
        } else {
            //List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
            List<BluetoothGattService> gattServices = blindsBluetoothGatt.getServices();
            if (gattServices != null) {
                //Loops through the BLE device's available services
                for (BluetoothGattService gattService : gattServices) {
                    String gattServiceUuid = gattService.getUuid().toString();
                    if (gattServiceUuid.equals(mServiceUUIDstring)) {
                        List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        // Loops through the BLE device's available characteristics.
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                            String gattCharacteristicUuid = gattCharacteristic.getUuid().toString();
                            if (gattCharacteristicUuid.equals(mCharacteristicUUIDstring)) {
                                //Setting this characteristic tells the Automated Blinds to roll
                                gattCharacteristic.setValue(inputByte);
                                if (inputByte == zeroByte) {
                                    rollAttemptResult = "Let there be Light!";
                                } else if (inputByte == oneByte) {
                                    rollAttemptResult = "Is this darkness in you too?";
                                }
                            }
                            //mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                            blindsBluetoothGatt.writeCharacteristic(gattCharacteristic);
                        }
                    }
                }
            }
        }
        displayShortMessage(rollAttemptResult);
    }

    private void connectToBluetoothDevice (String blueDeviceName) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String connectionResult = "Connecting to " + blueDeviceName + " failed for an unknown reason";
        if (mBluetoothAdapter == null) {
            connectionResult = "Android device does not support bluetooth";
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 7);
            connectionResult = "Bluetooth is not enabled";
        } else {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    if(device.getName().equals(blueDeviceName)) {
                        //connectionResult = "Remote Lightswitch is paired, but will not respond";
                        if(blueDeviceName.equals("Remote Lightswitch")) {
                            lightSwitchBluetoothGatt = device.connectGatt(this, true, lightSwitchGattCallback);
                            return;
                        } else if (blueDeviceName.equals("Automated Blinds")) {
                            blindsBluetoothGatt = device.connectGatt(this, true, blindsGattCallback);
                            return;
                        }
                        //mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
                        //return;
                    }
                }
            } else {
                connectionResult = blueDeviceName + " is not paired";
            }
        }
        displayShortMessage(connectionResult);
    }

    public void displayShortMessage(String message) {
        Context context = getApplicationContext();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private final BluetoothGattCallback lightSwitchGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                lightSwitchConnectionState = STATE_CONNECTED;
                gatt.discoverServices();
            } else {
                lightSwitchConnectionState = STATE_DISCONNECTED;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                lightSwitchServicesState = DISCOVERED;
            } else {
                lightSwitchServicesState = NOT_DISCOVERED;
            }
        }
    };

    private final BluetoothGattCallback blindsGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                blindsConnectionState = STATE_CONNECTED;
                gatt.discoverServices();
            } else {
                blindsConnectionState = STATE_DISCONNECTED;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                blindsServicesState = DISCOVERED;
            } else {
                blindsServicesState = NOT_DISCOVERED;
            }
        }
    };
}
