package com.duimy.baidumapdome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapViewHolder> {

    private List<PoiInfo> mPoiData;

    public void setPoiData(List<PoiInfo> poiData) {
        mPoiData = poiData;
    }

    @Override
    public MapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, null);

        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapViewHolder holder, int position) {
        PoiInfo poiInfo = mPoiData.get(position);
        holder.mTvName.setText(poiInfo.name);
    }

    @Override
    public int getItemCount() {
        return mPoiData == null ? 0 : mPoiData.size();
    }



    class MapViewHolder extends RecyclerView.ViewHolder {
        TextView mTvName;

        public MapViewHolder(View itemView) {
            super(itemView);

            mTvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}
