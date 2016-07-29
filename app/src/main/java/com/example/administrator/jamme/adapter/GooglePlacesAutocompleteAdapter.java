package com.example.administrator.jamme.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.administrator.jamme.libs.ApiClient;
import com.example.administrator.jamme.model.ApiInterface;
import com.example.administrator.jamme.model.Prediction;
import com.example.administrator.jamme.model.PredictionReponse;
import com.example.administrator.jamme.utlis.Utlis;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<Prediction> resultList;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index).getDescription();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    try {
                        resultList = autocomplete(constraint.toString()).take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private BlockingQueue<ArrayList<Prediction>> autocomplete(String input){
        ApiInterface apiService = ApiClient.retrofitExecute(Utlis.BASE_URL_AUTOCOMPLETE).create(ApiInterface.class);
        final BlockingQueue<ArrayList<Prediction>> blockingQueue = new ArrayBlockingQueue<>(1);

        /** Handler Asynchronous without AsyncTask **/
        Call<PredictionReponse> call = apiService.getPrediction(Utlis.API_BROWSER, "country:vn", input);
        call.enqueue(new Callback<PredictionReponse>() {
            @Override
            public void onResponse(Call<PredictionReponse> call, Response<PredictionReponse> response) {
                ArrayList<Prediction> tmp = response.body().getPredictions();
                blockingQueue.add(tmp);
            }

            @Override
            public void onFailure(Call<PredictionReponse> call, Throwable t) {

            }
        });
        return blockingQueue;
    }
}


