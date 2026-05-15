package com.roadwatch.mobile.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roadwatch.mobile.data.AppDatabase;
import com.roadwatch.mobile.data.ComplaintDao;
import com.roadwatch.mobile.data.ComplaintEntity;
import com.roadwatch.mobile.network.ApiClient;
import com.roadwatch.mobile.network.ApiService;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        ComplaintDao dao = AppDatabase.getDatabase(context).complaintDao();

        List<ComplaintEntity> unsynced = dao.getUnsyncedComplaints();
        if (unsynced.isEmpty()) {
            return Result.success();
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        boolean needsRetry = false;

        for (ComplaintEntity complaint : unsynced) {
            try {
                File file = new File(complaint.imagePath);
                if (!file.exists()) {
                    // Image missing, can't upload, mark synced to ignore it or delete it.
                    dao.markSynced(complaint.id);
                    continue;
                }

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                
                String rType = complaint.roadType != null ? complaint.roadType : "UNKNOWN";
                RequestBody roadTypePart = RequestBody.create(MediaType.parse("text/plain"), rType);

                String locationStr = complaint.location;
                RequestBody locationBody = RequestBody.create(MediaType.parse("text/plain"), locationStr != null ? locationStr : "");

                // Synchronous Retrofit call inside Worker
                Response<ResponseBody> response = apiService.createComplaint(roadTypePart, locationBody, imagePart).execute();

                if (response.isSuccessful()) {
                    dao.markSynced(complaint.id);
                } else {
                    needsRetry = true;
                }

            } catch (Exception e) {
                Log.e("SyncWorker", "Error uploading complaint: " + e.getMessage());
                needsRetry = true;
            }
        }

        if (needsRetry) {
            return Result.retry();
        }

        return Result.success();
    }
}
