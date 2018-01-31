package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.advisorapp.models.*;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SnapshotRepository;

import java.util.*;

import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Green;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Red;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Yellow;


public class FamilyInformationViewModel extends ViewModel {

    private FamilyRepository mFamilyRepository;
    private SnapshotRepository mSnapshotRespository;

    private LiveData<Family> currentFamily;

    final private MutableLiveData<Snapshot> mSelectedSnapshot = new MutableLiveData<>();

    private LiveData<List<Snapshot>> mSnapshots;

    public  FamilyInformationViewModel(FamilyRepository familyRepository, SnapshotRepository snapshotRespository){
        mFamilyRepository = familyRepository;
        mSnapshotRespository = snapshotRespository;
    }

    //Maps the selected snapshot to a list of indicators. This livedata object will notify it's observers when
    //the selected snapshot changes
    final private LiveData<SortedMap<IndicatorQuestion, IndicatorOption>> mIndicatorsForSelected = Transformations.map(mSelectedSnapshot, selected ->
    {
        if(selected==null)
        {
            return null;
        }
        else
        {
            SortedMap<IndicatorQuestion, IndicatorOption> sortedMap = new TreeMap<>();
            sortedMap.putAll(selected.getIndicatorResponses());
            return sortedMap;
        }
    });

    /**
     * Sets the current family for this view model and returns the LiveData representation
     * @param id family id for this view model
     * @return current family selected
     */
    public LiveData<Family> setFamily(int id){
        currentFamily = mFamilyRepository.getFamily(id);

        mSnapshots = Transformations.switchMap(currentFamily, currentFamily -> {
            if(currentFamily==null)
            {
                return null;
            }
            else return mSnapshotRespository.getSnapshots(currentFamily);
        });

        return currentFamily;
    }

    /**
     * Returns the indicators for the selected snapshot. Will update when the selected snapshot is changed
     * @return
     */
    public LiveData<SortedMap<IndicatorQuestion, IndicatorOption>> getSnapshotIndicators()
    {
       return mIndicatorsForSelected;
    }

    /**Gets the current family that's been set by setFamily**/
    public LiveData<Family> getCurrentFamily()
    {
        if(currentFamily == null)
        {
            throw new IllegalStateException("setFamily must be called in ViewModel before getCurrentFamily");
        }
        else return currentFamily;
    }

    public LiveData<List<Snapshot>> getSnapshots()
    {
        return mSnapshots;
    }

    public void setSelectedSnapshot(Snapshot s)
    {
        mSelectedSnapshot.setValue(s);
    }
}
