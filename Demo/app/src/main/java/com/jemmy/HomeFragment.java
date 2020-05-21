package com.jemmy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jemmy.adapter.HomeAdapter;
import com.jemmy.utils.DiffCallback;
import com.jemmy.viewmodel.HomeViewModel;
import com.jemmy.vo.PhotoItem;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private SwipeRefreshLayout layout;
    private RecyclerView recyclerView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        View view =  inflater.inflate(R.layout.home_fragment, container, false);
        layout = view.findViewById(R.id.swipeLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.swipemenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.swiperefresh:
                layout.setRefreshing(true);
                mViewModel.fetchData();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // TODO: Use the ViewModel
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.fetchData();
            }
        });
        final HomeAdapter adapter = new HomeAdapter(new DiffCallback());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        mViewModel._photoListLive.observe(getViewLifecycleOwner(), new Observer<List<PhotoItem>>() {
            @Override
            public void onChanged(List<PhotoItem> photoItems) {
                adapter.submitList(photoItems);
                layout.setRefreshing(false);
            }
        });
        mViewModel.fetchData();

    }

}
