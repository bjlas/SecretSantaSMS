package com.android.onehuman.smsecretsanta.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.android.onehuman.smsecretsanta.R;
import com.android.onehuman.smsecretsanta.listener.Main_ItemClickListener;
import com.android.onehuman.smsecretsanta.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{

    private List<Person> contactList;
    private Context context;
    private TypedArray icons;
    private int icon_position;

    private Main_ItemClickListener recyclerItemClickListener;

    public PersonAdapter(Context context) {
        this.context = context;
        this.contactList = new ArrayList<>();
        icons = context.getResources().obtainTypedArray(R.array.main_row_thumb_icons_array);
        icon_position=0;
    }

    public void updateList(List<Person> pl) {
        contactList = pl;
        notifyDataSetChanged();
    }

    public void remove(Person item) {
        int position = contactList.indexOf(item);
        if (position > -1) {
            contactList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Person getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_row_item, parent, false);

        final PersonViewHolder contactHolder = new PersonViewHolder(view);

        contactHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = contactHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    if (recyclerItemClickListener != null) {
                        recyclerItemClickListener.onItemClick(adapterPos, contactHolder.itemView);
                    }
                }
            }
        });

        return contactHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        final Person contact = contactList.get(position);
        Random random = new Random();

        holder.thumb.setImageResource(icons.getResourceId(updateIconPosition(),-1));

        holder.name.setText(contact.getName());
        if(contact.getForbbidenList().size()>0 ) {
            holder.forbbidenlist.setText(getForbiddenListNames(contact.getForbbidenList()));
            holder.forbbidenlist.setPaintFlags(holder.forbbidenlist.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.forbbidenlist.setText("");

        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void setOnItemClickListener(Main_ItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView name;
        TextView forbbidenlist;

        public PersonViewHolder(View itemView) {
            super(itemView);

            thumb = (ImageView) itemView.findViewById(R.id.main_row_thumb);
            name = (TextView) itemView.findViewById(R.id.main_row_name);
            forbbidenlist = (TextView) itemView.findViewById(R.id.main_row_forbbidenlist);

        }
    }

    private int updateIconPosition() {
        icon_position++;
        if (icon_position >= icons.length()) {
            icon_position=0;
        }

        return icon_position;
    }

    private String getForbiddenListNames(List<Person> forbbidenList) {
        String forbiddenNames="";
        for(Person p: forbbidenList){
            forbiddenNames +="["+p.getName()+"]";
        }
        return forbiddenNames;
    }
}
