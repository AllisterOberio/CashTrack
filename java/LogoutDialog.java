package com.example.budgetify1;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LogoutDialog extends DialogFragment {
    private final Runnable onConfirm;

    public LogoutDialog(Runnable onConfirm) {
        this.onConfirm = onConfirm;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d("LogoutDialog", "Dialog created");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.logout, null);

        Button confirmLogout = view.findViewById(R.id.confirmLogout);
        Button cancelLogout = view.findViewById(R.id.cancelLogout);

        confirmLogout.setOnClickListener(v -> {
            Log.d("LogoutDialog", "Confirm logout clicked");
            onConfirm.run();
            dismiss();
        });

        cancelLogout.setOnClickListener(v -> {
            Log.d("LogoutDialog", "Cancel logout clicked");
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}
