package com.example.datafile4.bookstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.datafile4.bookstore.Config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BooksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksFragment extends Fragment {
    // public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private String url = "https://amiraslan.azurewebsites.net/api/BookStore/GetFilteredBooks";
    private static String imgUrl = "https://amiraslan.azurewebsites.net/";
    int bookId;
    private ArrayList<Book> books;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    protected ProgressBar progressBar;
    protected RecyclerView mRecyclerView;
    protected BookAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private JSONArray responseArray;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "filtervalues";
    //private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BooksFragment() {
        // Required empty public constructor
    }


    public static BooksFragment newInstance(String message) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, message);
      //  args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        books = new ArrayList<Book>();
        mAdapter = new BookAdapter(getActivity(), books);
        //Message from MainActivity
       // String message = getArguments().getString(Constants.KEY_FILTER_VALUES);

        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences(Constants.PREF,Context.MODE_PRIVATE);
        String message = sharedPrefs.getString(Constants.KEY_FILTER_VALUES,"");
       // String message = "{'LangIDs':[], 'GenreIDs':[],  'LowPrice':0,  'HighPrice':999,  'SearchTerms':[],  'Pagination':{ 'PageNumber':0,    'PageLength':10  }}";

        Log.v("FilterValuesFragment",message);
        JSONObject queryParameter = null;
        try {
            queryParameter = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Volley request
        CustomJsonArrayRequest jsonObjectRequest = new CustomJsonArrayRequest(Request.Method.POST, url,queryParameter, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                int id;
                String bookName;
                String Url;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        id = jsonObject.getInt(Constants.KEY_ID);
                        bookName = jsonObject.getString(Constants.KEY_BOOKNAME);
                        //Url = imgUrl + jsonObject.getString(Constants.KEY_IMG_URL);
                        Url = "http://starecat.com/content/wp-content/uploads/copying-and-pasting-from-stack-overflow-essential-book-oreilly.jpg";//
                        //I receive urls in images/img.jpg format
                        books.add(new Book(id, bookName, Url));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.updateGrid(books);
               // progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorVolley:","Something is bad");
            }
        });
// {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//
//                params.put(Constants.KEY_PAGENUMBER, String.valueOf(0));
//                params.put(Constants.KEY_PAGELENGTH, String.valueOf(10));
//                return params;
//            }
//        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        mAdapter.setOnItemClickListener(new BookAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d("Click", "position" + position);
                bookId = books.get(position).getBookID();
                Intent intent = new Intent(getActivity(), BookActivity.class);
                intent.putExtra(Constants.KEY_ID, bookId);
                startActivity(intent);
            }
        });


//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        requestQueue.add(jsonObjectRequest);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_books, container, false);

        View rootView = inflater.inflate(R.layout.fragment_books, container, false);
        rootView.setTag(TAG);

        com.github.clans.fab.FloatingActionButton floatingActionButton = ( com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.books_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FilterActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvBooks);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
//        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBarBooks);
//        progressBar.setVisibility(ProgressBar.INVISIBLE);
        //progressBar.setVisibility(ProgressBar.VISIBLE);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter.updateGrid(books);

        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        books.clear();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
