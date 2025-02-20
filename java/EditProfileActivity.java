package com.example.budgetify1;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private ImageView profileImage;
    private String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        ImageView backArrow = findViewById(R.id.backButton);
        Button updateButton = findViewById(R.id.btn_update_profile);
        EditText editTextName = findViewById(R.id.editTextName);
        ImageView cameraButton = findViewById(R.id.cameraButton);
        profileImage = findViewById(R.id.profileImage);

        // Get current name and image path from intent
        String currentName = getIntent().getStringExtra("current_name");
        String currentImagePath = getIntent().getStringExtra("current_image_path");

        if (currentName != null) {
            editTextName.setText(currentName);
        }

        // Load existing profile image if available
        if (currentImagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
            profileImage.setImageBitmap(bitmap);
            imageFilePath = currentImagePath;
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editTextName.getText().toString();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("updated_name", newName);
                resultIntent.putExtra("updated_image_path", imageFilePath);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void showImagePickerDialog() {
        final String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        profileImage.setImageBitmap(bitmap);
                        imageFilePath = saveImageToInternalStorage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    profileImage.setImageBitmap(photo);
                    imageFilePath = saveImageToInternalStorage(photo);
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        String filename = "profile_image_" + UUID.randomUUID().toString() + ".png";
        File file = new File(getFilesDir(), filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
