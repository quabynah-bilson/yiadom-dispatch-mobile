package com.yiadom.dispatch.customer.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yiadom.dispatch.customer.BR;
import com.yiadom.dispatch.customer.databinding.ItemOrderPackageBinding;
import com.yiadom.dispatch.customer.view.listener.OnPackageSelectedListener;
import com.yiadom.dispatch.model.OrderPackageType;

public class OrderPackageListAdapter extends ListAdapter<OrderPackageType, OrderPackageListAdapter.ItemViewHolder> {
    private static final DiffUtil.ItemCallback<OrderPackageType> diffUtil = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull OrderPackageType oldItem, @NonNull OrderPackageType newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull OrderPackageType oldItem, @NonNull OrderPackageType newItem) {
            return oldItem.name().equals(newItem.name());
        }
    };
    private final OnPackageSelectedListener listener;
    private OrderPackageType activeItem;

    public OrderPackageListAdapter(OnPackageSelectedListener listener) {
        super(diffUtil);
        this.listener = listener;
        this.activeItem = OrderPackageType.FOOD;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemOrderPackageBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        var currentItem = getItem(position);
        holder.bind(currentItem, listener, activeItem == currentItem);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void toggleItem(OrderPackageType packageType) {
        this.activeItem = packageType;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemOrderPackageBinding binding;

        public ItemViewHolder(@NonNull ItemOrderPackageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OrderPackageType packageType, OnPackageSelectedListener listener, boolean selected) {
            binding.setVariable(BR.packageType, packageType);
//            binding.container.setChecked(selected);
            binding.getRoot().setOnClickListener(v -> listener.onPackageSelected(packageType));
        }
    }
}
