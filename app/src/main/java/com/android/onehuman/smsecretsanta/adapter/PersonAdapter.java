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
import com.android.onehuman.smsecretsanta.event.Main_OnItemClickListener;
import com.android.onehuman.smsecretsanta.model.Person;

import java.util.ArrayList;
import java.util.List;


public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{

    private List<Person> contactList;
    private Context context;
    private TypedArray icons;
    private int icon_position;

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


    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_row_item, parent, false);

        final PersonViewHolder contactHolder = new PersonViewHolder(view);
        return contactHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        final Person person = contactList.get(position);

        holder.thumb.setImageResource(icons.getResourceId(updateIconPosition(),-1));

        holder.name.setText(person.getName());

        if(person.getCandidates().size()>0 ) {
            holder.forbbidenlist.setText(getNonCandidates(person, person.getCandidates()));
            holder.forbbidenlist.setPaintFlags(holder.forbbidenlist.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.forbbidenlist.setText("");

        }
        holder.itemView.setOnClickListener(new Main_OnItemClickListener(context, person));

    }

    @Override
    public int getItemCount() {
        return contactList.size();
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

    private String getNonCandidates(Person actual, List<Person> candidatesList) {
        String forbiddenNames="";
        for(Person person: contactList){
            if(person!=actual && !candidatesList.contains(person)) {
                forbiddenNames += "[" + person.getName() + "]";
            }
        }
        return forbiddenNames;
    }
}
