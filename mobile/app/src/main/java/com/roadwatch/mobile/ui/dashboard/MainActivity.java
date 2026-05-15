package com.roadwatch.mobile.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.roadwatch.mobile.R;
import com.roadwatch.mobile.ui.complaint.ComplaintActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExtendedFloatingActionButton reportDefectButton = findViewById(R.id.reportDefectButton);
        reportDefectButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ComplaintActivity.class);
            startActivity(intent);
        });
        
        android.widget.TextView syncStatusText = findViewById(R.id.syncStatusText);
        android.view.animation.Animation pulse = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pulse);
        syncStatusText.startAnimation(pulse);

        com.roadwatch.mobile.data.ComplaintDao dao = com.roadwatch.mobile.data.AppDatabase.getDatabase(this).complaintDao();
        
        dao.getTotalComplaintsLiveData().observe(this, count -> {
            android.widget.TextView heroNumber = findViewById(R.id.heroNumber);
            if (heroNumber != null) {
                heroNumber.setText(String.valueOf(count != null ? count : 0));
            }
        });

        dao.getUnsyncedCountLiveData().observe(this, count -> {
            if (syncStatusText != null) {
                int unsynced = count != null ? count : 0;
                syncStatusText.setText(unsynced == 0 ? "All Synced" : unsynced + " Pending Sync");
            }
        });

        androidx.recyclerview.widget.RecyclerView rvRecentCaptures = findViewById(R.id.rvRecentCaptures);
        if (rvRecentCaptures != null) {
            rvRecentCaptures.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            RecentCaptureAdapter adapter = new RecentCaptureAdapter();
            rvRecentCaptures.setAdapter(adapter);
            dao.getRecentCapturesLiveData().observe(this, adapter::setComplaints);
        }
    }
}
