package com.yourtion.draganddraw;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Yourtion on 6/1/16.
 */
public class DragAndDrawFragment extends Fragment {
    public static final String TAG = "DragAndDrawFragment";
    private static final String KET_BOXES = "Boxes";
    private BoxDrawingView mBoxesView;
    private ArrayList<Box> mBoxes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Box> boxes = (ArrayList<Box>) savedInstanceState.getSerializable(KET_BOXES);
            if (boxes != null && boxes.size() > 0) {
                mBoxes = boxes;
            } else {
                mBoxes = null;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drag_and_draw, container, false);
        mBoxesView = (BoxDrawingView) v.findViewById(R.id.boxDrawingView);
        if (mBoxes != null) {
            mBoxesView.setBoxes(mBoxes);
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ArrayList<Box> boxes = mBoxesView.getBoxes();
        savedInstanceState.putSerializable(KET_BOXES, boxes);
    }
}
