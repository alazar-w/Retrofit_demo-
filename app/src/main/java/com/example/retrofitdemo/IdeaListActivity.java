package com.example.retrofitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retrofitdemo.helpers.SampleContent;
import com.example.retrofitdemo.models.Idea;
import com.example.retrofitdemo.services.IdeaService;
import com.example.retrofitdemo.services.ServiceBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IdeaListActivity extends AppCompatActivity {
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_list);

        final Context context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, IdeaCreateActivity.class);
                context.startActivity(intent);
            }
        });

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.idea_list);
        assert recyclerView != null;

        if (findViewById(R.id.idea_detail_container) != null) {
            mTwoPane = true;
        }





//        HashMap<String,String> filters = new HashMap<>();
//        filters.put("owner","jim");
//        filters.put("count","1");

//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(SampleContent.IDEAS));
        IdeaService ideaService = ServiceBuilder.buildService(IdeaService.class);
//        Call<List<Idea>> ideaRequest = ideaService.getIdeas(filters);
        Call<List<Idea>> ideaRequest = ideaService.getIdeas();

        Button btnCancel = findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Failed cancel.",Toast.LENGTH_LONG).show();
                //to cancel on going request
                ideaRequest.cancel();

            }
        });

        ideaRequest.enqueue(new Callback<List<Idea>>() {
            //our GSON converter will do some work for us behind the scenes to map our response data to a clean list of ideas

            //onResponse() technically called whenever a valid Response is returned from the server.This means that regardless of the status code,
            //whether that's a 200 or a 401 or a 500, this method will still be called.so this could cause errors to occur since a 500 error probably
            //won't include data to bind. so we must check some things by our self.
            @Override
            public void onResponse(Call<List<Idea>> call, Response<List<Idea>> response) {
                //the isSuccessful() method checks for 200 range status code
                if (response.isSuccessful()){
                    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(response.body()));
                //401 - means the user is an authorized
                }else if (response.code() == 401){
                    Toast.makeText(context,"Your session has expired.",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(context,"Failed to retrieve items.",Toast.LENGTH_LONG).show();
                }
                findViewById(R.id.layout_cancel).setVisibility(View.GONE);
            }

            //onFailure() is actually only called when the request completely fails due to a problem like a lost connection or timeOut
            @Override
            public void onFailure(Call<List<Idea>> call, Throwable t) {
                //we can try to cast the throwable parameter to different types to get more information,here we are casting to an I/O error,this means there was a
                //connection problem.
                if (t instanceof IOException){
                    Toast.makeText(context,"A connection error occurred.",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(context,"Failed to retrieve items.",Toast.LENGTH_LONG).show();
                }
                findViewById(R.id.layout_cancel).setVisibility(View.GONE);
            }
        });
    }



    //region Adapter Region
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Idea> mValues;

        public SimpleItemRecyclerViewAdapter(List<Idea> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.idea_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(Integer.toString(mValues.get(position).getId()));
            holder.mContentView.setText(mValues.get(position).getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(IdeaDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        IdeaDetailFragment fragment = new IdeaDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.idea_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, IdeaDetailActivity.class);
                        intent.putExtra(IdeaDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Idea mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
//endregion
}
