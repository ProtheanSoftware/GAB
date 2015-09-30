package gab.protheansoftware.com.gab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_list,container,false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        String[] foodz = {"banan","Candy"};
        ListAdapter foodzAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, foodz);
        setListAdapter(foodzAdapter);
    }

}
