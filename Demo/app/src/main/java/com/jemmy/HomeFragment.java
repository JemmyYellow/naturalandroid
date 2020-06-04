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
import com.jemmy.utils.ProductDiffCallback;
import com.jemmy.viewmodel.HomeViewModel;
import com.jemmy.vo.Product;

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
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        layout = view.findViewById(R.id.orderSwipeLayout);
        recyclerView = view.findViewById(R.id.orderRecyclerView);

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.fetchData();
            }
        });

        final HomeAdapter adapter = new HomeAdapter(new ProductDiffCallback());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        mViewModel._productListLive.observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                adapter.submitList(products);
                layout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.swipemenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
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

        if (mViewModel._productListLive.getValue() == null ||
                mViewModel._productListLive.getValue().size() == 0) {
            mViewModel.fetchData();
        }
    }

}
