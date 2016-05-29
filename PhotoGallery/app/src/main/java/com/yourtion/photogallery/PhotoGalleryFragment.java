package com.yourtion.photogallery;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Yourtion on 5/29/16.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    ArrayList<GalleryItem> mItems;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (View) inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mGridView = (GridView) v.findViewById(R.id.gridView);

        setupAdapter();

        return v;
    }

    void setupAdapter() {
        if (getActivity() == null && mGridView == null) return;

        if (mItems != null) {
            mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
                    android.R.layout.simple_gallery_item, mItems));
        } else {
            mGridView.setAdapter(null);
        }

    }

    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>> {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }
}
