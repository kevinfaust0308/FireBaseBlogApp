package com.monsoonblessing.fireblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.imageSelect)
    ImageButton mSelectImage;
    @BindView(R.id.titleField)
    EditText mPostTitle;
    @BindView(R.id.descField)
    EditText mPostDesc;

    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private StorageReference mStorage;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
    }

    @OnClick(R.id.imageSelect)
    void onImageSelect() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @OnClick(R.id.submiButton) void onSubmit() {
        mProgress.setMessage("Posting to blog...");
        mProgress.show();
        startPosting();
    }

    private void startPosting() {

        String titleVal = mPostTitle.getText().toString();
        String descVal = mPostDesc.getText().toString();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null) {
            StorageReference filepath = mStorage.child("BlogImages").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mProgress.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                mImageUri = data.getData();
                mSelectImage.setImageURI(mImageUri);
            }
        }

    }
}
