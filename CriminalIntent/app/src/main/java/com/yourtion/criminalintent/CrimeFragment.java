package com.yourtion.criminalintent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Yourtion on 5/5/16.
 */
public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "com.yourtion.criminalintent.crime_id";
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final String DIALOG_CHOOSE = "choose";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CHOOSE = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_CONTACT = 4;
    private static final int REQUEST_CALL = 5;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mSuspectButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void updateDate() {
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        String dateString = dtf.format(mCrime.getDate());
        mDateButton.setText(dateString);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        ActionBar ab = getActivity().getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                DialogFragment dialog = new ChoosePickerFragment();
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CHOOSE);
                dialog.show(fm, DIALOG_CHOOSE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null) return;

                FragmentManager fm = getActivity().getFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path, p.getRotate()).show(fm, DIALOG_IMAGE);
            }
        });
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getActivity().openContextMenu(v);
                return true;
            }
        });
        registerForContextMenu(mPhotoView);

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
        final Intent iSuspect = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(iSuspect, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
        final Intent iReport = new Intent(Intent.ACTION_SEND);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iReport.setType("text/plain");
                iReport.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                iReport.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                Intent i = Intent.createChooser(iReport, getString(R.string.send_report));
                startActivity(i);
            }
        });

        Button callButton = (Button) v.findViewById(R.id.crime_callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(iSuspect, REQUEST_CALL);
            }
        });

        // If camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }
        if (pm.queryIntentActivities(iSuspect, 0).size() < 0) {
            mSuspectButton.setEnabled(false);
        }
        if (pm.queryIntentActivities(iReport, 0).size() < 0) {
            reportButton.setEnabled(false);
            callButton.setEnabled(false);
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_pager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_crime:
                if (mCrime.getPhoto() != null) {
                    getActivity().deleteFile(mCrime.getPhoto().getFilename());
                }
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mCrime.getPhoto() != null) {
            getActivity().getMenuInflater().inflate(R.menu.crime_delete_photo, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        getActivity().deleteFile(mCrime.getPhoto().getFilename());
        mCrime.setPhoto(null);
        PictureUtils.cleanImageView(mPhotoView);
        return super.onContextItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CHOOSE) {
            String choose = data.getStringExtra(ChoosePickerFragment.EXTRA_CHOISE);
            FragmentManager fm = getActivity().getFragmentManager();
            if (choose.equals(ChoosePickerFragment.EXTRA_CHOISE_DATE)) {
                DialogFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
            if (choose.equals(ChoosePickerFragment.EXTRA_CHOISE_TIME)) {
                DialogFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        } else if (requestCode == REQUEST_PHOTO) {
            // Create a new Photo object and attach it to the crime
            if (mCrime.getPhoto() != null) {
                getActivity().deleteFile(mCrime.getPhoto().getFilename());
            }
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                int rotate = data.getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ROTATE, 0);
                Photo p = new Photo(filename, rotate);
                mCrime.setPhoto(p);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();


            String[] queryField = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryField, null, null, null);

            if (c != null && c.moveToFirst()) {
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }
            if (c != null) c.close();
        } else if (requestCode == REQUEST_CALL) {
            Uri contactUri = data.getData();
            
            Cursor c = getActivity().getContentResolver().query(contactUri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                int contactId = c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor phones = getActivity().getContentResolver()
                        .query(phoneUri, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

                if (phones != null && phones.moveToFirst()) {
                    String numPhone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Uri number = Uri.parse("tel:" + numPhone);
                    Intent i = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(i);
                }
                if (phones != null) phones.close();
            }
            if (c != null) c.close();
        }
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    private void showPhoto() {
        // (Re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path, p.getRotate());
        }
        mPhotoView.setImageDrawable(b);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
}
