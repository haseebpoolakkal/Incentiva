package com.azetech.insentiva;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//Created by Aseeb-P(+91 8606459264).

public class AboutBottomSheet extends BottomSheetDialogFragment {

    private TextView version_code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_bottom_sheet, container, false);

        validate(v);
        getCurrentVersion();

        return v;
    }

    private void getCurrentVersion(){
        String current_version = null;
        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            current_version = pInfo.versionName;
            version_code.setText(current_version);
        } catch (PackageManager.NameNotFoundException e) {

        }

    }
    private void validate(View v) {
        version_code = (TextView) v.findViewById(R.id.version_code);
    }
}
