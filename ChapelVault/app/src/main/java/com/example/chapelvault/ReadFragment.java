package com.example.chapelvault;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment {

    static ArrayList<String> read = new ArrayList<>();
    static ArrayList<String> objectId = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    static LinearLayout linearLayout;
    static ListView listView;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReadFragment newInstance(String param1, String param2) {
        ReadFragment fragment = new ReadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_read, container, false);

        listView = v.findViewById(R.id.listView);
        linearLayout = v.findViewById(R.id.linearLayout);
        arrayAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_list_item_1, read);

        read.clear();
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Request");
        query1.whereExists("status");
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {

                            linearLayout.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);

                            read.add(object.getString("student"));
                            objectId.add(object.getObjectId());
                        }
                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                } else {
                    MYToast.makeToast(v.getContext(), e.getMessage(), MYToast.CUSTOM_GRAVITY_BOTTOM, MYToast.CUSTOM_TYPE_ERROR, MYToast.LENGTH_LONG).show();
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(v.getContext(), RequestReaderActivity.class);
                intent.putExtra("read", read.get(i));
                intent.putExtra("objectId", objectId.get(i));
                startActivity(intent);
            }
        });
        return v;
    }
}