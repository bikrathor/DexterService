package com.ccec.dexterservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.ccec.dexterservice.profiles.ProfileOne;
import com.ccec.dexterservice.profiles.ProfileThree;
import com.ccec.dexterservice.profiles.ProfileTwo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CircularImageView circularImageView, circularImageView2;
    private String picturePath;
    private static int REQUEST_CAMERA = 0;
    private static int SELECT_FILE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    UserSessionManager session;
    private String profilePic, email, uid;
    private TabLayout tabLayout;
    private String status = "hii";
    private String encodedImage, source = "";
    private AlertDialog.Builder builder;
    private Bitmap thumbnail;
    private TextView text3;
    private ProgressBar progressBar;
    private AlertDialog dialog;
    private String picUrl;
    private byte[] imageData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        session = new UserSessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        uid = user.get(UserSessionManager.TAG_id);
        profilePic = user.get(UserSessionManager.TAG_profilepic);
        email = user.get(UserSessionManager.TAG_email);

        TextView textView = (TextView) view.findViewById(R.id.headerTextProfileTab);
        textView.setTypeface(FontsManager.getRegularTypeface(getContext()));
        textView.setText(email);

        mViewPager = (ViewPager) view.findViewById(R.id.containerProfileTabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        changeTabsFont();

        circularImageView = (CircularImageView) view.findViewById(R.id.circularImageProfileTab);
        getPic();
        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        return view;
    }

    private void getPic() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
        storageRef.child("profilePics/" + uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.with(getActivity()).load(uri).noPlaceholder().into(circularImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                storageRef.child("profilePics/" + uid + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getActivity()).load(uri).noPlaceholder().into(circularImageView);
                    }
                });
            }
        });
    }

    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(FontsManager.getRegularTypeface(getContext()));
                }
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Update Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");

                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContext().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    try {
                        Bitmap bm = BitmapFactory.decodeFile(picturePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        imageData = baos.toByteArray();

                        source = "gallery";
                        confirmUpload();
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(getActivity(), "Out Of Memory", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Some Error", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            imageData = bytes.toByteArray();
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(b);
//            fo.close();
//
//            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            source = "camera";
            confirmUpload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmUpload() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.dialog_pic, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setView(dialoglayout);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        circularImageView2 = (CircularImageView) dialoglayout.findViewById(R.id.circularImage);
        if (source.equals("camera"))
            circularImageView2.setImageBitmap(thumbnail);
        else if (source.equals("gallery"))
            circularImageView2.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        progressBar = (ProgressBar) dialoglayout.findViewById(R.id.progressBar);

        text3 = (TextView) dialoglayout.findViewById(R.id.headerText3);
        text3.setTypeface(FontsManager.getRegularTypeface(getContext()));

        final LinearLayout linearLayout = (LinearLayout) dialoglayout.findViewById(R.id.lin);

        final Button ok = (Button) dialoglayout.findViewById(R.id.yes);
        ok.setTypeface(FontsManager.getBoldTypeface(getContext()));
        final Button cancel = (Button) dialoglayout.findViewById(R.id.no);
        cancel.setTypeface(FontsManager.getBoldTypeface(getContext()));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text3.setText("Updating..");

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                StorageReference imageRef = storageRef.child("profilePics/" + uid + ".jpg");
                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Image updated", Toast.LENGTH_SHORT).show();

                        ((HomePage) getActivity()).getPic();

                        if (source.equals("camera"))
                            circularImageView.setImageBitmap(thumbnail);
                        else if (source.equals("gallery"))
                            circularImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });

                ok.setEnabled(false);
                cancel.setEnabled(false);

                ok.setAlpha((float) 0.7);
                cancel.setAlpha((float) 0.7);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ProfileOne fragment = new ProfileOne();
                    return fragment;
                case 1:
                    ProfileTwo fragment2 = new ProfileTwo();
                    return fragment2;
                case 2:
                    ProfileThree fragment3 = new ProfileThree();
                    return fragment3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Profile";
                case 1:
                    return "Edit Profile";
                case 2:
                    return "Change Pass";
            }
            return null;
        }
    }
}