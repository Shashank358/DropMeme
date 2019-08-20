package com.dropout.dropmeme.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropout.dropmeme.Adapters.ThumbnailAdapter;
import com.dropout.dropmeme.CreateMemeActivity;
import com.dropout.dropmeme.Interfaces.FiltersListFragmentListener;
import com.dropout.dropmeme.R;
import com.dropout.dropmeme.Utils.BitmapUtils;
import com.dropout.dropmeme.Utils.SpacesItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersListFragment extends BottomSheetDialogFragment implements FiltersListFragmentListener{

    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem> thumbnailItems;

    FiltersListFragmentListener listener;

    static FiltersListFragment instance;
    static Bitmap bitmap;

    public static FiltersListFragment getInstance(Bitmap bitmapSave){
        bitmap = bitmapSave;

        if (instance == null)
        {
            instance = new FiltersListFragment();

        }

        return instance;
    }

    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_filters_list, container, false);


        thumbnailItems = new ArrayList<>();
        adapter = new ThumbnailAdapter(thumbnailItems, this, getActivity());

        recyclerView = (RecyclerView) itemView.findViewById(R.id.filters_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);

        displayThumbnail(bitmap);

        return itemView;
    }

    public void displayThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Bitmap thumbImg;
                if (bitmap == null)
                {
                    thumbImg = BitmapUtils.getBitmapFromAssets(getActivity(), CreateMemeActivity.pictureName, 100, 100);
                }
                else {
                    thumbImg = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }

                if (thumbImg == null)
                    return;
                    ThumbnailsManager.clearThumbs();
                    thumbnailItems.clear();

                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbImg;
                    thumbnailItem.filterName = "Normal";
                    ThumbnailsManager.addThumb(thumbnailItem);

                    List<Filter> filters = FilterPack.getFilterPack(getActivity());

                    for (Filter filter: filters){

                        ThumbnailItem tI = new ThumbnailItem();
                        tI.image = thumbImg;
                        tI.filterName = filter.getName();
                        tI.filter = filter;
                        ThumbnailsManager.addThumb(tI);
                    }

                    thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {

        if (listener!=null){
            listener.onFilterSelected(filter);
        }
    }
}
