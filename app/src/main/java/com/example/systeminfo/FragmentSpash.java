package com.example.systeminfo;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.systeminfo.databinding.FragmentSpashBinding;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSpash#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSpash extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1="param1";
    private static final String ARG_PARAM2="param2";

    private int ACCESS_PHONE_STATE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentSpashBinding binding;

    private static final int CAMERA_PERMISSION_CODE = 100;

    public FragmentSpash() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSpash.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSpash newInstance(String param1, String param2) {
        FragmentSpash fragment=new FragmentSpash();
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
       // return inflater.inflate(R.layout.fragment_spash, container, false);
        binding = FragmentSpashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        }, 2000);
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it
//            ActivityCompat.requestPermissions(requireActivity(),
//                    new String[]{Manifest.permission.CAMERA},
//                    CAMERA_PERMISSION_REQUEST_CODE);
//        } else {
//            Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_SHORT).show();
//            // Permission is already granted, handle the camera-related operations
//            final Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_container, new FragmentSystemInfo());
//                    transaction.commit();
//                }
//            }, 200);
//        }


        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
    }
    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            requestPermissions( new String[] { permission }, requestCode);
        }
        else {
            movetoNextFragment();
          //
            //  Toast.makeText(requireContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
            Log.e("Request Code",""+requestCode);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
                movetoNextFragment();
            }
            else {
                Toast.makeText(requireContext(), "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }

    private void movetoNextFragment(){
                    final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new FragmentSystemInfo());
                    transaction.commit();
                }
            }, 200);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }






}