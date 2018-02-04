package org.fundacionparaguaya.advisorapp.data;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.repositories.SyncManager;

import javax.inject.Inject;

/**
 * A utility that creates jobs.
 */

public class JobCreator implements com.evernote.android.job.JobCreator {
    private AdvisorApplication mApplication;

    @Inject
    SyncManager mSyncManager;

    public JobCreator(AdvisorApplication application) {
        this.mApplication = application;

        mApplication.getApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncJob.TAG:
                return new SyncJob(mSyncManager);
            default:
                return null;
        }
    }

    public class SyncJob extends Job {
        public static final String TAG = "SyncJob";

        private SyncManager mSyncManager;

        public SyncJob(SyncManager syncManager) {
            super();
            this.mSyncManager = syncManager;
        }

        @Override
        @NonNull
        protected Result onRunJob(@NonNull Params params) {
            if (mSyncManager.sync())
                return Result.SUCCESS;
            else
                return Result.FAILURE;
        }
    }
}
