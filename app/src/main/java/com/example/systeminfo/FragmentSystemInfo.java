package com.example.systeminfo;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.icu.text.DecimalFormat;
import android.opengl.GLSurfaceView;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.systeminfo.databinding.FragmentSpashBinding;
import com.example.systeminfo.databinding.FragmentSystemInfoBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSystemInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSystemInfo extends Fragment {


    private FragmentSystemInfoBinding binding;

    // Define a constant for the camera permission request
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 789;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1="param1";
    private static final String ARG_PARAM2="param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SensorManager sensorManager;

    private ActivityManager activityManager;

    TelephonyManager telephonyManager;

    BatteryManager bm;

    private static final int PHONE_STATE_PERMISSION_REQUEST_CODE=456;

    public FragmentSystemInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSystemInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSystemInfo newInstance(String param1, String param2) {
        FragmentSystemInfo fragment=new FragmentSystemInfo();
        Bundle args=new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1=getArguments().getString(ARG_PARAM1);
            mParam2=getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSystemInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorManager=(SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);

        activityManager=(ActivityManager) requireActivity().getSystemService(ACTIVITY_SERVICE);



        setRAM();
        setROM();
        setBattery();

        telephonyManager=(TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);

        binding.textviewmodel.setText(Build.MODEL);
        binding.textviewmanufacturer.setText(Build.MANUFACTURER);
        binding.textviewmodelnumber.setText(Build.ID);





        //binding.textviewimei.setText(IMEINummber());
        try {
            binding.textviewcpu.setText(""+getCPUInfo());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getSensorsList();


    }





    @Override
    public void onStart() {
        super.onStart();
        Log.e("MP",""+Math.round(getBackCameraResolutionInMp()));


        binding.textviewccamera.setText(""+Math.round(getBackCameraResolutionInMp())+" MP");

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PHONE_STATE_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, handle the phone state-related operations
            // For example, retrieve the phone state information
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                updateIMEI();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Phone state permission is granted, handle the phone state-related operations
                updateIMEI();
            } else {
                // Phone state permission is denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(requireContext(), "Phone state permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String IMEINummber() {
        TelephonyManager telephonyManager=(TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return telephonyManager.getImei();
        } else {
            return "";
        }
    }



    public static Map<String, String> getCPUInfo() throws IOException {

        BufferedReader br=new BufferedReader(new FileReader("/proc/cpuinfo"));

        String str;

        Map<String, String> output=new HashMap<>();

        while ((str=br.readLine()) != null) {

            String[] data=str.split(":");

            if (data.length > 1) {

                String key=data[0].trim().replace(" ", "_");
                if (key.equals("model_name")) key="cpu_model";

                output.put(key, data[1].trim());

            }

        }

        br.close();

        return output;

    }

    private void getSensorsList() {
        List<Sensor> sensorList=sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            binding.textviewsensor.append("" + sensor.getName() + ",");
            Log.i("Sensor name:", "" + sensor.getName());
        }
    }

    private void updateIMEI() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.textviewimei.setText(telephonyManager.getImei());
        }


    }

    private void setROM(){
        double totalSize = new File(requireActivity().getFilesDir().getAbsoluteFile().toString()).getTotalSpace();
        double totMb = totalSize / (1024 * 1024 * 1024);
        Log.e("Total ROM",""+totMb);
        binding.textviewrom.setText( String.format("%.2f",totMb)+" GB");
    }

    private void  setRAM(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(
                memoryInfo
        );
        double gigabytes = (double) memoryInfo.totalMem / (1024 * 1024 * 1024);

        binding.textviewram.setText( String.format("%.2f",gigabytes)+" GB");
    }

    private void setBattery(){
        bm = (BatteryManager) requireContext().getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        binding.textviewcurrentcharging.setText(""+batLevel+"%");
    }


    public float getBackCameraResolutionInMp()
    {
        int noOfCameras = Camera.getNumberOfCameras();
        float maxResolution = -1;
        long pixelCount = -1;
        for (int i = 0;i < noOfCameras;i++)
        {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                Camera camera = Camera.open(i);;
                Camera.Parameters cameraParams = camera.getParameters();
                for (int j = 0;j < cameraParams.getSupportedPictureSizes().size();j++)
                {
                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height; // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount)
                    {
                        pixelCount = pixelCountTemp;
                        maxResolution = ((float)pixelCountTemp) / (1024000.0f);
                    }
                }

                camera.release();
            }
        }

        return maxResolution;
    }

}