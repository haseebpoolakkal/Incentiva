package com.azetech.insentiva;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.ViewHolder> implements Filterable {

    List nameList = new ArrayList();
    List nameListAll = new ArrayList();
    HashMap originalMap = new HashMap();
    HashMap mobileMap = new HashMap();
    Context ctx;

    Convert convert = new Convert();

    public EmployeeListAdapter(HashMap<String, String> map, HashMap<String, String> mobile, Context ctx){
        nameList.addAll(map.keySet());
        nameListAll.addAll(map.keySet());
        originalMap.putAll(map);
        mobileMap.putAll(mobile);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public EmployeeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.user_list, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeListAdapter.ViewHolder viewHolder, int i) {
        final String name = String.valueOf(nameList.get(i));

        viewHolder.name.setText(name);
        viewHolder.salary.setText(String.valueOf(originalMap.get(name)));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, AdminInsentives.class);
                intent.putExtra("name", name);
                intent.putExtra("mobile", convert.ObjectToString(mobileMap.get(name)));
                intent.putExtra("salary", convert.ObjectToString(originalMap.get(name)));
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List filterList = new ArrayList();

            if (constraint.toString().isEmpty()){
                filterList.addAll(nameListAll);
            }
            else {
                for (Object name: nameListAll){
                    if (String.valueOf(name).toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterList.add(name);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            nameList.clear();
            nameList.addAll((Collection) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, salary;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.user_name);
            salary = (TextView) itemView.findViewById(R.id.user_salary);
        }
    }
}
