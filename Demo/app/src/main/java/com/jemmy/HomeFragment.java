package com.jemmy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jemmy.viewmodel.HomeViewModel;

import java.io.File;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private ImageView testImage;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.home_fragment, container, false);
        testImage = view.findViewById(R.id.testImage);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        // TODO: Use the ViewModel

        String path = "D:\\Mysql\\mysql-8.0.20-winx64\\images\\score.jpg";
        File file = new File("path");
//        testImage.setImageResource(R.drawable.jlu);
        boolean ee = file.exists();
        if(ee){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            testImage.setImageBitmap(bitmap);
        }

    }

}
