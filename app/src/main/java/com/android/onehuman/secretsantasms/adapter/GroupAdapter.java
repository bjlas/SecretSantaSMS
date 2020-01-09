package com.android.onehuman.secretsantasms.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.onehuman.secretsantasms.R;
import com.android.onehuman.secretsantasms.event.Group_OnItemClickListener;
import com.android.onehuman.secretsantasms.model.Group;

import java.util.ArrayList;
import java.util.List;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    private List<Group> groupList;
    private Context context;
    private TypedArray icons;

    public GroupAdapter(Context context) {
        this.context = context;
        this.groupList = new ArrayList<>();
        icons = context.getResources().obtainTypedArray(R.array.grouplist_row_icons_array);
    }

    public void updateList(List<Group> pl) {
        groupList = pl;
        notifyDataSetChanged();
    }


    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        Group group = groupList.get(position);

        holder.thumb.setImageResource(icons.getResourceId(Math.abs(group.getGroupID()%10) ,-1));

        holder.name.setText(group.getGroupName());

        if(group.getMaxPrice() != null ) {
            holder.maxprice.setText(group.getMaxPrice());
        }
        holder.itemView.setOnClickListener(new Group_OnItemClickListener(context, group));

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView name;
        TextView maxprice;

        public GroupViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageView) itemView.findViewById(R.id.group_row_thumb);
            name = (TextView) itemView.findViewById(R.id.group_row_name);
            maxprice = (TextView) itemView.findViewById(R.id.group_row_maxprice);
        }
    }



}
