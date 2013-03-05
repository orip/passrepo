package com.example.passrepo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import com.example.passrepo.events.SearchQueryUpdatedEvent;
import com.squareup.otto.Bus;

/**
 * Created with IntelliJ IDEA.
 * User: dekelna
 * Date: 3/5/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

    private final Bus bus;

    public SearchFragment() {
        this.bus = BusWrapper.globalBus;
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container);
    }

    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SearchView sv = ((SearchView) getActivity().findViewById(R.id.searchView));
        sv.setOnQueryTextListener(this);
        sv.setIconifiedByDefault(false);

        // HACK to change text color
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv.findViewById(id);
        textView.setTextColor(Color.WHITE);
    }

    public boolean onQueryTextChange(String newText) {
        bus.post(new SearchQueryUpdatedEvent(newText));
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        // do nothing, the correct results should be available from the realtime text updates
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

}
