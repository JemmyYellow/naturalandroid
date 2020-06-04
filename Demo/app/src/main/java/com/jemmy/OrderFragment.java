package com.jemmy;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jemmy.adapter.OrderVOAdapter;
import com.jemmy.utils.OrderVODiffCallback;
import com.jemmy.utils.ProductDiffCallback;
import com.jemmy.viewmodel.OrderViewModel;
import com.jemmy.vo.OrderVO;

import java.util.List;

public class OrderFragment extends Fragment {

    private OrderViewModel mViewModel;
    private SwipeRefreshLayout orderSwipeLayout;
    private RecyclerView orderRecyclerView;

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        View view = inflater.inflate(R.layout.order_fragment, container, false);
        orderSwipeLayout = view.findViewById(R.id.orderSwipeLayout);
        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);

        orderSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mViewModel.fetchData()){
                    orderSwipeLayout.setRefreshing(false);
                }
            }
        });

        final OrderVOAdapter adapter = new OrderVOAdapter(new OrderVODiffCallback(), mViewModel, requireActivity());
        orderRecyclerView.setAdapter(adapter);
        orderRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        mViewModel._orderVOListLive.observe(getViewLifecycleOwner(), new Observer<List<OrderVO>>() {
            @Override
            public void onChanged(List<OrderVO> orderVOS) {
                adapter.submitList(orderVOS);
                orderSwipeLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
        if (mViewModel._orderVOListLive.getValue() == null ||
                mViewModel._orderVOListLive.getValue().size() == 0) {
            mViewModel.fetchData();
        }
        if(mViewModel.FETCH_FAIL){
            orderSwipeLayout.setRefreshing(false);
        }
    }

}
