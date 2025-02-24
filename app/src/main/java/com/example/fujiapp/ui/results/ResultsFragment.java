package com.example.fujiapp.ui.results;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.fujiapp.MainActivity;
import com.example.fujiapp.R;
import com.example.fujiapp.data.DbDataSource;
import com.example.fujiapp.data.TrainingSession;
import com.example.fujiapp.databinding.FragmentResultsBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

public class ResultsFragment extends Fragment {

    private FragmentResultsBinding binding;

    private View root;
    TableLayout table;

    Button btnExport;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentResultsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        table = root.findViewById(R.id.table);

        DbDataSource db = new DbDataSource(getContext());
        db.openDB();
        LinkedList<TrainingSession> list = db.getAllTrainingSessionFromUser(MainActivity.activUser);
        TableRow.LayoutParams p = (TableRow.LayoutParams) root.findViewById(R.id.tvDate).getLayoutParams();

        for (TrainingSession ts : list) {
            TableRow tr = new TableRow(getContext());
            TextView tVDate = new TextView(getContext());
            tVDate.setText(ts.getDate());
            tr.addView(tVDate, p);
            TextView tVTime = new TextView(getContext());
            tVTime.setText(ts.getTime());
            tr.addView(tVTime, p);
            TextView tVPulseAv = new TextView(getContext());
            tVPulseAv.setText(ts.getAveragePulse());
            tr.addView(tVPulseAv, p);
            TextView tVPulseMax = new TextView(getContext());
            tVPulseMax.setText(ts.getMaxPulse());
            tr.addView(tVPulseMax, p);
            TextView tVPulseMin = new TextView(getContext());
            tVPulseMin.setText(ts.getMinPulse());
            tr.addView(tVPulseMin, p);
            TextView tVRespirationAv = new TextView(getContext());
            tVRespirationAv.setText(ts.getAverageRespiration());
            tr.addView(tVRespirationAv, p);
            TextView tVRespirationMax = new TextView(getContext());
            tVRespirationMax.setText(ts.getMaxRespiration());
            tr.addView(tVRespirationMax, p);
            TextView tVRespirationMin = new TextView(getContext());
            tVRespirationMin.setText(ts.getMinRespiration());
            tr.addView(tVRespirationMin, p);
            TextView tVResultBackposition = new TextView(getContext());
            tVResultBackposition.setText(ts.getResultBackposition());
            tr.addView(tVResultBackposition, p);
            TextView tVResultTime = new TextView(getContext());
            tVResultTime.setText(ts.getResultTime());
            tr.addView(tVResultTime, p);
            TextView tVSuccess = new TextView(getContext());
            if(ts.isSuccess()) {
                tVSuccess.setText(getString(R.string.yes));
            } else {
                tVSuccess.setText(getString(R.string.no));
            }
            tr.addView(tVSuccess, p);
            table.addView(tr);
        }
        btnExport = root.findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportResults(list);
            }
        });

        return root;
    }

    public void exportResults(LinkedList<TrainingSession> list){
        StringBuilder data = new StringBuilder();
        //data.append("Datum,Uhrzeit,Durcschnittlicher Puls,Maximaler Puls,Minimaler Puls,Durchschnittlich Atmung,Maximale Atmung,Minimale Atmung,Bewertung Rückenposition,Bewertung Zeit,Erfolgreich?,Puls Verlauf, Atmung Verlauf");
        for (TrainingSession ts : list) {
            data.append("\n" + ts.getDate() +"," + ts.getTime()+"," + ts.getAveragePulse()+"," + ts.getMaxPulse()+"," + ts.getMinPulse()+"," + ts.getAverageRespiration()+"," + ts.getMaxRespiration()+"," + ts.getMinRespiration()+"," + ts.getResultBackposition()+"," + ts.getResultTime()+"," + ts.isSuccess()+"," + ts.getPulse()+"," + ts.getRespiration());
        }

        try {
            FileOutputStream outputStream = getActivity().openFileOutput("results.csv", Context.MODE_PRIVATE);
            outputStream.write((data.toString()).getBytes());
            outputStream.close();

            File file = new File(getActivity().getFilesDir(), "results.csv");
            Uri path = FileProvider.getUriForFile(root.getContext(), "com.example.fujiapp.fileprovider", file);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "results");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "results"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

        @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}