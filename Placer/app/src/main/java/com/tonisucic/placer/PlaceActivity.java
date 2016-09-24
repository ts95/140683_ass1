package com.tonisucic.placer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tonisucic.placer.adapter.PlaceAdapter;
import com.tonisucic.placer.db.PlaceModel;
import com.tonisucic.placer.unit.Distance;

import java.util.Formatter;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class PlaceActivity extends AppCompatActivity {

    TextView mInfoTextView;
    ListView mLogListView;

    PlaceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInfoTextView = (TextView) findViewById(R.id.tv_info);
        mLogListView = (ListView) findViewById(R.id.lv_log);

        final Realm realm = Realm.getDefaultInstance();

        RealmResults<PlaceModel> realmResults = realm
                .where(PlaceModel.class)
                .findAllSorted("mCreatedAt", Sort.DESCENDING);

        mAdapter = new PlaceAdapter(this, realmResults);

        View emptyListView = getLayoutInflater().inflate(R.layout.list_empty, null);
        addContentView(emptyListView, mLogListView.getLayoutParams());

        mLogListView.setEmptyView(emptyListView);
        mLogListView.setAdapter(mAdapter);
        mLogListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                 new AlertDialog.Builder(PlaceActivity.this)
                        .setTitle(R.string.are_you_sure)
                        .setMessage(R.string.affirm_delete_record)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                realm.beginTransaction();
                                PlaceModel model = mAdapter.getItem(position);
                                model.deleteFromRealm();
                                realm.commitTransaction();

                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
            }
        });

        mAdapter.notifyDataSetChanged();

        Bundle bundle = getIntent().getExtras();

        final Distance altitude = bundle.getParcelable("altitude");
        final String place = bundle.getString("place");
        final WeatherRestClient.Forecast forecast = bundle.getParcelable("forecast");

        if (altitude == null)
            return;

        if (forecast == null)
            return;

        String altitudeString = altitude.getLocaleString(getResources());
        String tempString = forecast.getTemp().getLocaleString();

        Formatter formatter = new Formatter();
        String info = formatter.format(getResources().getString(R.string.info_activity_place),
                place, altitudeString, tempString).toString();

        mInfoTextView.setText(info);

        FloatingActionButton addPlaceButton = (FloatingActionButton) findViewById(R.id.ab_add_place);

        if (addPlaceButton == null)
            return;

        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceModel model = new PlaceModel(altitude, forecast.getTemp(), place);

                realm.beginTransaction();
                realm.copyToRealm(model);
                realm.commitTransaction();

                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
