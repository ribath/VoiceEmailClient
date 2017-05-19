package com.chtl.mainflow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ribath on 4/30/2017.
 */

public class SentItemsAdapter extends RecyclerView.Adapter<SentItemsAdapter.ViewHolder>{

    List<SentMail> sentMails;

    public SentItemsAdapter(List<SentMail> sentMails) {
        this.sentMails = sentMails;
    }

    @Override
    public SentItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SentItemsAdapter.ViewHolder holder, int position) {
        SentMail sentMail = sentMails.get(position);
        holder.subject.setText(sentMail.getSubject());
        holder.date.setText(sentMail.getDate());
    }

    @Override
    public int getItemCount() {
        if(sentMails  != null) {
            return sentMails.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView subject;
        public TextView date;

        public ViewHolder(View v) {
            super(v);
            subject = (TextView) v.findViewById(R.id.subject);
            date = (TextView) v.findViewById(R.id.date);
        }
    }
}
