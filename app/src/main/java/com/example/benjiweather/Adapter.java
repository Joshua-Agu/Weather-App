package com.example.benjiweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {

    private Context context;
    private ArrayList<WeatherBuildUp> weatherBuildUpArrayList;

    public Adapter(Context context, ArrayList<WeatherBuildUp> weatherBuildUpArrayList) {
        this.context = context;
        this.weatherBuildUpArrayList = weatherBuildUpArrayList;
    }

    @NonNull
    @Override
    public Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_process,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        WeatherBuildUp model = weatherBuildUpArrayList.get(position);
        holder.temp.setText((model.getTempterature()) + "°C");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.Image);
        holder.wind.setText(model.getGaleSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm");
        try{
            Date T = input.parse(model.getTime());
            holder.time.setText(output.format(T));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherBuildUpArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView wind, temp, time;
        private ImageView Image;
        public viewHolder(@NonNull View view) {
            super(view);
            wind = itemView.findViewById(R.id.wind);
            temp = itemView.findViewById(R.id.Temp);
            time = itemView.findViewById(R.id.Time);
            Image = itemView.findViewById(R.id.Image1);
        }
    }
}
