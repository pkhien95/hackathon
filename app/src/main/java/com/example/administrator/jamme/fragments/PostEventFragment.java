package com.example.administrator.jamme.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.jamme.libs.ApiClient;
import com.example.administrator.jamme.libs.LocationHelper;
import com.example.administrator.jamme.model.ApiInterface;
import com.example.administrator.jamme.model.Event;
import com.example.administrator.jamme.utlis.Utlis;
import com.example.administrator.jamme.widget.CheckableImageButton;

import com.example.administrator.jamme.R;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostEventFragment extends DialogFragment {
    private static final int CAMERA_REQUEST = 1888;

    private CheckableImageButton mCurrentButton, btnTypeSos, btnTypeJam, btnTypeAccident, btnTypeOther;
    private Button btnPostEvent;
    private ImageButton btnTakeImg;
    private EditText editDesc;
    private ImageView imageView;

    private Event event;
    private Context mContext;
    private int tmpType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_event, container, false);
        Firebase.setAndroidContext(mContext);
        event = new Event();
        /********************Create dialog******************************/
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        btnTypeSos = (CheckableImageButton) view.findViewById(R.id.btnTypeSos);
        btnTypeJam = (CheckableImageButton) view.findViewById(R.id.btnTypeJam);
        btnTypeAccident = (CheckableImageButton) view.findViewById(R.id.btnTypeAccident);
        btnTypeOther = (CheckableImageButton) view.findViewById(R.id.btnTypeOther);
        btnPostEvent = (Button) view.findViewById(R.id.btnPostEvent);
        editDesc = (EditText) view.findViewById(R.id.editDesc);

        mCurrentButton = btnTypeSos;
        btnTypeSos.setChecked(true);
        tmpType = 0;
        CheckableImageButton.OnCheckedChangeListener onCheckedChangeListener = new CheckableImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckableImageButton button, boolean isChecked) {
                if (isChecked && mCurrentButton != button) {
                    mCurrentButton.setChecked(false);
                    mCurrentButton = button;

                    if (mCurrentButton == btnTypeSos) tmpType = 0;
                    else if (mCurrentButton == btnTypeJam) tmpType = 1;
                    else if (mCurrentButton == btnTypeAccident) tmpType = 2;
                    else if (mCurrentButton == btnTypeOther) tmpType = 3;
                }
            }
        };

        btnTypeSos.setOnCheckedChangeListener(onCheckedChangeListener);
        btnTypeJam.setOnCheckedChangeListener(onCheckedChangeListener);
        btnTypeAccident.setOnCheckedChangeListener(onCheckedChangeListener);
        btnTypeOther.setOnCheckedChangeListener(onCheckedChangeListener);

        btnTakeImg = (ImageButton) view.findViewById(R.id.btnTakeImg);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        btnTakeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityFromFragment(PostEventFragment.this, cameraIntent, CAMERA_REQUEST);
            }
        });

        btnPostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationHelper location = new LocationHelper(mContext);

                event.setLat(location.getLocation().getLatitude());
                event.setLng(location.getLocation().getLongitude());
                event.setType(tmpType);
                event.setMessage(editDesc.getText().toString());

                /*PostEvent to server using Retrofit
                ApiInterface apiService = ApiClient.retrofitExecute(Utlis.BASE_URL).create(ApiInterface.class);

                Call<Event> call = apiService.postEvent(event);
                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {}
                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {}
                });*/
                Firebase ref = new Firebase(Utlis.FIREBASE_URL);
                ref.child("event").push().setValue(event);

                Toast.makeText(mContext, "Bạn đã đăng event thành công", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int) (dm.widthPixels * .95) ;
        int height = (int) (dm.heightPixels * .95);

        getDialog().getWindow().setLayout(width, height);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            /******Convert bitmap to base64*************/
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            event.setImg(Base64.encodeToString(byteArray, Base64.DEFAULT));

            imageView.setImageBitmap(photo);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}