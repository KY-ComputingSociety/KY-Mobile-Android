package com.example.kymobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kymobile.R;
import com.example.kymobile.models.ModelNotice;

import java.util.List;

public class AdapterNotices extends RecyclerView.Adapter{

    Context context;
    List<ModelNotice> postList;

    public AdapterNotices(Context context, List<ModelNotice> postList) {
        this.context = context;
        this.postList = postList;
    }


    @Override
    public int getItemViewType(int position) {
        if(postList.get(position).getExco().equals("General")){
            return 0;
        }
        if(postList.get(position).getExco().equals("Academic")){
            return 1;
        }
        if(postList.get(position).getExco().contains("Welfare")){
            return 2;
        }
        if(postList.get(position).getExco().equals("Sports")){
            return 3;
        }
        if(postList.get(position).getExco().equals("Religious")){
            return 4;
        }
        if(postList.get(position).getExco().equals("Food")){
            return 5;
        }
        return 6;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == 0){
            view = layoutInflater.inflate(R.layout.notice_general_row,parent,false);
            return new HolderGeneral(view);
        }
        if(viewType == 1){
            view = layoutInflater.inflate(R.layout.notice_acad_row,parent,false);
            return new HolderAcad(view);
        }
        if(viewType == 2){
            view = layoutInflater.inflate(R.layout.notice_welfare_row,parent,false);
            return new HolderWelfare(view);
        }
        if(viewType == 3){
            view = layoutInflater.inflate(R.layout.notice_sports_row,parent,false);
            return new HolderSports(view);
        }
        if(viewType == 4){
            view = layoutInflater.inflate(R.layout.notice_religious_row,parent,false);
            return new HolderReligious(view);
        }
        if(viewType == 5){
            view = layoutInflater.inflate(R.layout.notice_food_row,parent,false);
            return new HolderFood(view);
        }
        view = layoutInflater.inflate(R.layout.notice_special_row,parent,false);
        return new HolderTask(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String body,title;
        body = postList.get(position).getBody();
        title = postList.get(position).getTitle();
        if(postList.get(position).getExco().contains("General")){
            HolderGeneral holderGeneral = (HolderGeneral) holder;
            holderGeneral.notice_text.setText(body);
            holderGeneral.notice_name.setText(title);
        }
        if(postList.get(position).getExco().contains("Academic")){
            HolderAcad holderAcad = (HolderAcad) holder;
            holderAcad.notice_text.setText(body);
            holderAcad.notice_name.setText(title);
        }
        if(postList.get(position).getExco().contains("Welfare")){
            HolderWelfare holderWelfare= (HolderWelfare) holder;
            holderWelfare.notice_text.setText(body);
            holderWelfare.notice_name.setText(title);
        }
        if(postList.get(position).getExco().contains("Sports")){
            HolderSports holderSports = (HolderSports) holder;
            holderSports.notice_text.setText(body);
            holderSports.notice_name.setText(title);
        }
        if(postList.get(position).getExco().contains("Religious")){
            HolderReligious holderReligious = (HolderReligious) holder;
            holderReligious.notice_text.setText(body);
            holderReligious.notice_name.setText(title);
        }
        if(postList.get(position).getExco().contains("Food")){
            HolderFood holderFood = (HolderFood) holder;
            holderFood.notice_text.setText(body);
            holderFood.notice_name.setText(title);
        }
        if(postList.get(position).getExco().contains("Special")){
            HolderTask holderTask = (HolderTask) holder;
            holderTask.notice_text.setText(postList.get(position).getBody());
            holderTask.notice_name.setText(postList.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class HolderGeneral extends RecyclerView.ViewHolder{

        TextView notice_name,notice_text;

        public HolderGeneral(@NonNull View itemView) {
            super(itemView);
            notice_text = itemView.findViewById(R.id.notice_text);
            notice_name = itemView.findViewById(R.id.notice_name);
        }
    }
    class HolderAcad extends RecyclerView.ViewHolder{

        TextView notice_name,notice_text;

        public HolderAcad(@NonNull View itemView) {
            super(itemView);
            notice_text = itemView.findViewById(R.id.notice_text);
            notice_name = itemView.findViewById(R.id.notice_name);
        }
    }
    class HolderWelfare extends RecyclerView.ViewHolder{
        TextView notice_name,notice_text;

        public HolderWelfare(@NonNull View itemView) {
            super(itemView);
            notice_text = itemView.findViewById(R.id.notice_text);
            notice_name = itemView.findViewById(R.id.notice_name);
        }
    }
    class HolderSports extends RecyclerView.ViewHolder{
        TextView notice_name,notice_text;

        public HolderSports(@NonNull View itemView) {
            super(itemView);
            notice_text = itemView.findViewById(R.id.notice_text);
            notice_name = itemView.findViewById(R.id.notice_name);
        }
    }
    class HolderReligious extends RecyclerView.ViewHolder{
        TextView notice_name,notice_text;

        public HolderReligious(@NonNull View itemView) {
            super(itemView);
            notice_text = itemView.findViewById(R.id.notice_text);
            notice_name = itemView.findViewById(R.id.notice_name);
        }
    }
    class HolderFood extends RecyclerView.ViewHolder{
        TextView notice_name,notice_text;

        public HolderFood(@NonNull View itemView) {
            super(itemView);
            notice_text = itemView.findViewById(R.id.notice_text);
            notice_name = itemView.findViewById(R.id.notice_name);
        }
    }
    class HolderTask extends RecyclerView.ViewHolder{
        TextView notice_name,notice_text;

        public HolderTask(@NonNull View itemView) {
            super(itemView);
            notice_text = itemView.findViewById(R.id.notice_text);
            notice_name = itemView.findViewById(R.id.notice_name);
        }
    }
}
