package com.yourtion.criminalintent;

import android.app.DialogFragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Yourtion on 5/20/16.
 */
public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH = "com.yourion.criminalintent.image_path";
    public static final String EXTRA_IMAGE_ROTATE = "com.yourion.criminalintent.image_rotate";
    private ImageView mImageView;

    public static ImageFragment newInstance(String imagePath, int rorate) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        args.putInt(EXTRA_IMAGE_ROTATE, rorate);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        int rorate = getArguments().getInt(EXTRA_IMAGE_ROTATE, 0);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path, rorate);
        mImageView.setImageDrawable(image);
        return mImageView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PictureUtils.cleanImageView(mImageView);
    }
}
