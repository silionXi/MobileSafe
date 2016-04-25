package com.silion.mobilesafe.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.silion.mobilesafe.R;

/**
 * Created by silion on 2016/3/31.
 */
public class LostFindContactFragment extends LostFindBaseFragment {
    private final static int SELECT_CONTACT = 0;

    private EditText etSecContacts;

    private View.OnClickListener mSelectContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, SELECT_CONTACT);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lostfind_contact, container, false);
        etSecContacts = (EditText) view.findViewById(R.id.etSecContacts);
        etSecContacts.setText(mPref.getString("security_contact", ""));

        TextView tvSelectContact = (TextView) view.findViewById(R.id.tvSelectContact);
        tvSelectContact.setOnClickListener(mSelectContactListener);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            if (SELECT_CONTACT == requestCode) {
                Uri uri = data.getData();
                long rawContactId = ContentUris.parseId(uri);
                Cursor cursor = mLostFindActivity.getContentResolver().query(Uri.parse("content://com.android.contacts/data"),
                        new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{rawContactId + ""}, null);
                while (cursor.moveToNext()) {
                    String data1 = cursor.getString(0);
                    String type = cursor.getString(1);

                    if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                        etSecContacts.setText(data1);
                    }
                }
            }
        }
    }

    @Override
    public void preStep() {
        mLostFindActivity.pushFragment(new LostFindSimFragment(), 1);
    }

    @Override
    public void nextStep() {
        String phone = null;
        if (etSecContacts != null) {
            phone = etSecContacts.getText().toString();
        }
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(mLostFindActivity, "请输入安全号码", Toast.LENGTH_SHORT).show();
        } else {
            mPref.edit().putString("security_contact", phone).commit();
            mLostFindActivity.pushFragment(new LostFindOpenFragment(), 3);
        }
    }
}
