package lasting.travelassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

public class NewsPage extends ListFragment {
    private static final int INITIAL_DELAY_MILLIS = 300;

    private NewsCardsAdapter nca = null;

    private ListView newsList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news, container, false);

        newsList = (ListView) view.findViewById(R.id.newsList);
        newsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SceneryPage.class);
                startActivity(intent);
                return false;
            }
        });

        nca = new NewsCardsAdapter(getActivity());

        SwingBottomInAnimationAdapter sbiaa = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(nca, new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull ViewGroup viewGroup, @NonNull int[] ints) {
                for (int i : ints) {
                    nca.remove(i);
                }
            }
        }));
        sbiaa.setAbsListView(newsList);

        assert sbiaa.getViewAnimator() != null;
        sbiaa.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

        newsList.setAdapter(sbiaa);

        for (int i = 0; i < 20; i++) {
            nca.add(i);
        }

        return view;
    }
}
