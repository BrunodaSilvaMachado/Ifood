package com.cursoandroid.ifood.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> implements Filterable {
    public interface Layout{
        @LayoutRes
        int getLayout(int viewType);
    }
    public interface OnItemClickListener <T>{
        void onItemClick(T t);
    }
    public interface FilterComparator<T>{
        boolean compare(T t, String query);
    }
    private List<T> itemList;
    private List<T> itemListFull;
    private final Layout layout;
    private final BaseViewHolder.OnViewBinding<T> viewBinding;
    private OnItemClickListener<T> onItemClickListener;
    private FilterComparator<T> comparator;
    final private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<T> filteredList = new ArrayList<>();
            if (comparator == null || constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (T t : itemListFull) {
                    if (comparator.compare(t,filterPattern)) {
                        filteredList.add(t);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List<T>) results.values);
            notifyDataSetChanged();
        }
    };

    public GroupAdapter(List<T> itemList, Layout layout, BaseViewHolder.OnViewBinding<T> viewBinding){
        this.itemList = itemList;
        this.itemListFull = new ArrayList<>(itemList);
        this.layout = layout;
        this.viewBinding = viewBinding;
        this.onItemClickListener = null;
        this.comparator = null;
    }

    @NonNull
    @NotNull
    @Override
    public BaseViewHolder<T> onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.getLayout(viewType),parent,false );
        return new BaseViewHolder<>(view,viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BaseViewHolder<T> holder, int position) {
        final T t = itemList.get(position);
        holder.bind(t);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(l->onItemClickListener.onItemClick(t));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addItem(T t){
        itemList.add(t);
        itemListFull.add(t);
        notifyItemInserted(getItemCount());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAllItem(List<T> itemList){
        this.itemList = itemList;
        this.itemListFull = new ArrayList<>(itemList);
        notifyDataSetChanged();
    }

    public List<T> getItemList(){
        return itemList;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setComparator(FilterComparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearFilter(){
        itemList = new ArrayList<>(itemListFull);
        notifyDataSetChanged();
    }
}
