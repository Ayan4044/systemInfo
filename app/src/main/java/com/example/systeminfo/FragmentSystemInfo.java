package com.example.systeminfo;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.systeminfo.databinding.FragmentSpashBinding;
import com.example.systeminfo.databinding.FragmentSystemInfoBinding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSystemInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSystemInfo extends Fragment {


    private FragmentSystemInfoBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1="param1";
    private static final String ARG_PARAM2="param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        binding = FragmentSystemInfoBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textviewmodel.setText(android.os.Build.MODEL);
        binding.textviewmanufacturer.setText(Build.MANUFACTURER);
        binding.textviewmodelnumber.setText(Build.PRODUCT);
        binding.textviewram.setText(""+getTotalRAM());
        //binding.textviewimei.setText(IMEINummber());
        try {
            binding.textviewcpu.setText(" "+getCPUInfo());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private  String IMEINummber(){
        TelephonyManager telephonyManager = (TelephonyManager)requireActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return telephonyManager.getImei();
        }
        else{
            return "";
        }
    }

    public long getTotalRAM() {

//        ActivityManager actManager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
//        actManager.getMemoryInfo(memInfo);
//
//        return  memInfo.totalMem;

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)requireActivity().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
       return mi.availMem / 1048576L;
    }

    public static Map<String, String> getCPUInfo () throws IOException {

        BufferedReader br = new BufferedReader (new FileReader("/proc/cpuinfo"));

        String str;

        Map<String, String> output = new HashMap<>();

        while ((str = br.readLine ()) != null) {

            String[] data = str.split (":");

            if (data.length > 1) {

                String key = data[0].trim ().replace (" ", "_");
                if (key.equals ("model_name")) key = "cpu_model";

                output.put (key, data[1].trim ());

            }

        }

        br.close ();

        return output;

    }
}