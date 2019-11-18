package gy.android.ui.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentManagerUtil {

    public FragmentManager manager;

    public FragmentManagerUtil(FragmentManager manager) {
        this.manager = manager;
    }

    public void replaceFragment(int containerID, Fragment fragment, boolean isAddToBackStack) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(containerID, fragment);
        if (isAddToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }
}
