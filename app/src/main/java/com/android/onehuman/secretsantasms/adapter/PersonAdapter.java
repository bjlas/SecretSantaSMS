package com.android.onehuman.secretsantasms.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.onehuman.secretsantasms.R;
import com.android.onehuman.secretsantasms.event.Person_OnItemClickListener;
import com.android.onehuman.secretsantasms.event.Resend_OnItemClickListener;
import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.model.Person;

import java.util.ArrayList;
import java.util.List;


public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{

    private List<Person> contactList;
    private Context context;
    private TypedArray icons;
    private int icon_position;
    private Group group;

    public PersonAdapter(Context context) {
        this.context = context;
        this.contactList = new ArrayList<>();
        icons = context.getResources().obtainTypedArray(R.array.personlist_row_icons_array);
        icon_position=0;
    }

    public void updateList(List<Person> pl) {
        contactList = pl;
        notifyDataSetChanged();
    }

    public void setGroup(Group g) {
        this.group=g;
    }


    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_row_item, parent, false);

        final PersonViewHolder contactHolder = new PersonViewHolder(view);
        return contactHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        final Person person = contactList.get(position);

        holder.thumb.setImageResource(icons.getResourceId(updateIconPosition(),-1));

        holder.name.setText(person.getName());

        if(person.getForbiddenList().size()>0 ) {
            holder.forbbidenlist.setText(person.forbiddenListToString());
            holder.forbbidenlist.setPaintFlags(holder.forbbidenlist.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.forbbidenlist.setText("");

        }
        holder.itemView.setOnClickListener(new Person_OnItemClickListener(context, person, group));

        holder.resendButton.setOnClickListener(new Resend_OnItemClickListener(context, person));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private int updateIconPosition() {
        icon_position++;
        if (icon_position >= icons.length()) {
            icon_position=0;
        }

        return icon_position;
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView name;
        TextView forbbidenlist;
        ImageButton resendButton;


        public PersonViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageView) itemView.findViewById(R.id.person_row_thumb);
            name = (TextView) itemView.findViewById(R.id.person_row_name);
            forbbidenlist = (TextView) itemView.findViewById(R.id.person_row_forbbidenlist);
            resendButton = (ImageButton) itemView.findViewById(R.id.person_row_button_resend);
        }
    }



}
