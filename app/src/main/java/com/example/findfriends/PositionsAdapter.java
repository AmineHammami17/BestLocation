package com.example.findfriends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.PositionViewHolder> {

    private List<Position> positionsList;
    private OnPositionClickListener positionClickListener;

    public interface OnPositionClickListener {
        void onPositionClick(Position position);
    }

    public PositionsAdapter(List<Position> positionsList, OnPositionClickListener positionClickListener) {
        this.positionsList = positionsList;
        this.positionClickListener = positionClickListener;
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_position, parent, false);
        return new PositionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PositionViewHolder holder, int position) {
        Position positionData = positionsList.get(position);

        holder.tvName.setText(positionData.getName());
        holder.tvNumber.setText(positionData.getPhoneNumber());

        holder.tvLatitude.setText(positionData.getLatitude());
        holder.tvLongitude.setText(positionData.getLongitude());

        holder.itemView.setOnClickListener(v -> {
            if (positionClickListener != null) {
                positionClickListener.onPositionClick(positionData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return positionsList.size();
    }

    public static class PositionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber, tvLatitude, tvLongitude;

        public PositionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPositionName);
            tvNumber = itemView.findViewById(R.id.tvPositionNumber);
            tvLatitude = itemView.findViewById(R.id.tvPositionLatitude);
            tvLongitude = itemView.findViewById(R.id.tvPositionLongitude);
        }
    }
}
