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
 * Use the {@link UnreadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnreadFragment extends Fragment {

    static ArrayList<String> unread = new ArrayList<>();
    static ArrayList<String> objectId = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    static ListView listView;
    static LinearLayout linearLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UnreadFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TravelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmptyFragment newInstance(String param1, String param2) {
        EmptyFragment fragment = new EmptyFragment();
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
        final View v = inflater.inflate(R.layout.fragment_unread, container, false);

        linearLayout = v.findViewById(R.id.linearLayout);
        listView = v.findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_list_item_1, unread);

        unread.clear();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
        query.whereDoesNotExist("status");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {

                            listView.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);

                            unread.add(object.get("student").toString());
                            objectId.add(object.getObjectId());
                            listView.setClickable(true);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(v.getContext(), RequestReaderActivity.class);
                                    intent.putExtra("unread", unread.get(i));
                                    intent.putExtra("objectId", objectId.get(i));
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        listView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    MYToast.makeToast(v.getContext(), e.getMessage(), MYToast.CUSTOM_GRAVITY_BOTTOM, MYToast.CUSTOM_TYPE_ERROR, MYToast.LENGTH_LONG).show();
                }
                listView.setAdapter(arrayAdapter);
            }
        });
        return v;
    }
}