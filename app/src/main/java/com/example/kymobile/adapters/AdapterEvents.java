package com.example.kymobile.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kymobile.R;
import com.example.kymobile.models.ModelEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterEvents extends RecyclerView.Adapter<AdapterEvents.MyHolder> {

    Context context;
    List<ModelEvent> postList;
    Dialog myDialog;


    public AdapterEvents(Context context, List<ModelEvent> postList) {
        this.context = context;
        this.postList = postList;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.event_row,parent,false);
        final MyHolder vHolder = new MyHolder(view);

        //dialog init
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.more_info);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        vHolder.row_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dialog_event_name = myDialog.findViewById(R.id.event_pop_name);
                TextView dialog_event_fullDesc = myDialog.findViewById(R.id.event_pop_full_desc);
                TextView dialog_event_startTime = myDialog.findViewById(R.id.event_pop_start_time);
                TextView dialog_event_endTime = myDialog.findViewById(R.id.event_pop_end_time);
                TextView dialog_event_startDate = myDialog.findViewById(R.id.event_pop_start_date);
                TextView dialog_event_endDate = myDialog.findViewById(R.id.event_pop_end_date);
                TextView dialog_event_venue = myDialog.findViewById(R.id.event_pop_venue);
                ImageView dialog_event_image = myDialog.findViewById(R.id.event_pop_cover);
                //setting data
                dialog_event_name.setText(postList.get(vHolder.getAdapterPosition()).getTitle());
                dialog_event_fullDesc.setText(postList.get(vHolder.getAdapterPosition()).getFullDesc());
                String string = postList.get(vHolder.getAdapterPosition()).getCover();

                if(postList.get(vHolder.getAdapterPosition()).getStartTime().equals(" ")){
                    //start time
                    dialog_event_startTime.setVisibility(View.GONE);
                }else{
                    dialog_event_startTime.setText(postList.get(vHolder.getAdapterPosition()).getStartTime());
                }
                if(postList.get(vHolder.getAdapterPosition()).getEndTime().equals(" ")){
                    //end time
                    dialog_event_endTime.setVisibility(View.GONE);
                }else{
                    dialog_event_endTime.setText(postList.get(vHolder.getAdapterPosition()).getEndTime());
                }
                if(postList.get(vHolder.getAdapterPosition()).getStartDate().equals(" ")){
                    dialog_event_startDate.setVisibility(View.GONE);
                }else{
                    dialog_event_startDate.setText(postList.get(vHolder.getAdapterPosition()).getStartDate());
                }
                if(postList.get(vHolder.getAdapterPosition()).getEndDate().equals(" ")){
                    dialog_event_endDate.setVisibility(View.GONE);
                }else{
                    dialog_event_endDate.setText(postList.get(vHolder.getAdapterPosition()).getEndDate());
                }
                if(postList.get(vHolder.getAdapterPosition()).getVenue().equals(" ")){
                    dialog_event_venue.setVisibility(View.GONE);
                }else{
                    dialog_event_venue.setText(postList.get(vHolder.getAdapterPosition()).getVenue());
                }







                try{
                    Picasso.get().load(string).into(dialog_event_image);

                }
                catch (Exception e){

                }
                myDialog.show();
            }
        });

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        //get data
        final String EventName = postList.get(position).getTitle();
        String ShortDesc = postList.get(position).getShortDesc();
        final String Cover = postList.get(position).getCover();


        holder.EventTitle.setText(EventName);
        holder.EventShortDesc.setText(ShortDesc);



        if(Cover.equals("NoCover")){
            //hide image view
            holder.EventImage.setVisibility(View.GONE);
        }
        else{
            try{
                Picasso.get().load(Cover).into(holder.EventImage);

            }
            catch (Exception e){

            }
        }


    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView EventImage;
        TextView EventTitle,EventShortDesc;
        CardView row_event;



        public MyHolder(@NonNull View itemView) {
            super(itemView);
            EventImage = itemView.findViewById(R.id.event_cover);
            EventTitle = itemView.findViewById(R.id.event_name);
            EventShortDesc = itemView.findViewById(R.id.event_short_desc);
            row_event = itemView.findViewById(R.id.row_event);



        }
    }
}
