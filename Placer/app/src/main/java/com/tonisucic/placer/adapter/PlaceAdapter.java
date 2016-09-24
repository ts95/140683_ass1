package com.tonisucic.placer.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tonisucic.placer.R;
import com.tonisucic.placer.db.PlaceModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by tonisucic on 16.09.2016.
 */
public class PlaceAdapter extends RealmBaseAdapter<PlaceModel> implements ListAdapter {

    public PlaceAdapter(Context context, RealmResults<PlaceModel> realmResults) {
        super(context, realmResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlaceModel model = getItem(position);

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_info, parent, false);

            holder = new ViewHolder();
            holder.place = (TextView) convertView.findViewById(R.id.tv_place);
            holder.temp = (TextView) convertView.findViewById(R.id.tv_temp);
            holder.altitude = (TextView) convertView.findViewById(R.id.tv_altitude);
            holder.dateTime = (TextView) convertView.findViewById(R.id.tv_date_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int dateStyle = DateFormat.MEDIUM;
        int timeStyle = DateFormat.SHORT;
        DateFormat df = DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.getDefault());

        Resources res = parent.getContext().getResources();

        String altitudeString = model.getAltitude().getLocaleString(res);

        holder.place.setText(model.getPlace());
        holder.temp.setText(model.getTemp().getLocaleString());
        holder.altitude.setText(res.getString(R.string.info_row_altitude, altitudeString));
        holder.dateTime.setText(df.format(model.getCreatedAt()));

        return convertView;
    }

    public static class ViewHolder {
        TextView place;
        TextView temp;
        TextView altitude;
        TextView dateTime;
    }
}
