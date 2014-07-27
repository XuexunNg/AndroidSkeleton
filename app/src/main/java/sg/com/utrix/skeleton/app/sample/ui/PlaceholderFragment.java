package sg.com.utrix.skeleton.app.sample.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardCursorAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import sg.com.utrix.skeleton.R;
import sg.com.utrix.skeleton.app.sample.provider.SkeletonContract;
import sg.com.utrix.skeleton.app.sample.ui.listener.EndlessScrollListener;
import sg.com.utrix.skeleton.app.sample.util.ImageLoader;

import static sg.com.utrix.skeleton.app.sample.util.LogUtils.LOGI;
import static sg.com.utrix.skeleton.app.sample.util.LogUtils.makeLogTag;


/**
 * Created by HUANG on 19/7/2014.
 */
public class PlaceholderFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = makeLogTag(PlaceholderFragment.class);

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private CardListView mListView;
    private BoneAdapter mAdapter;
    private ImageLoader mImageLoader;
    private LoaderManager.LoaderCallbacks mCallbacks;
    public static SwipeRefreshLayout mSwipeView;
    private int mPage = 1;


    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mCallbacks = this;

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = (CardListView) getActivity().findViewById(R.id.bone_list);

        //Setup ImageLoader
        if (getActivity() instanceof ImageLoader.ImageLoaderProvider) {
            mImageLoader = ((ImageLoader.ImageLoaderProvider) getActivity()).getImageLoaderInstance();
        }

        //Set up Adapter
        mAdapter = new BoneAdapter(getActivity());
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
        }

        getLoaderManager().restartLoader(0, null, this);
        infiniteScroll();
        swipeToRefresh();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = SkeletonContract.Bones.CONTENT_URI.buildUpon()
                .appendPath("page")
                .appendPath(String.valueOf(mPage))
                .build();

        return new CursorLoader(getActivity(), uri, null, null, null, SkeletonContract.Bones.BONE_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    /*Private Functions */

    private void swipeToRefresh(){
        mSwipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeView.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        mSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeView.setRefreshing(true);
                mPage = 1; //reset to 1
                getLoaderManager().restartLoader(0, null, mCallbacks);

            }
        });
    }

    private void infiniteScroll() {
        //Setup endless scrolling
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mPage++;
                getLoaderManager().restartLoader(0, null, mCallbacks);
            }
        });
    }

    private class BoneAdapter extends CardCursorAdapter {

        public BoneAdapter(Context context) {
            super(context);
        }

        @Override
        protected Card getCardFromCursor(Cursor cursor) {
            MyCursorCard card = new MyCursorCard(super.getContext());
            setCardFromCursor(card, cursor);

            return card;

        }

        private void setCardFromCursor(MyCursorCard card, Cursor cursor) {

            card.title = cursor.getString(BoneQuery.BONE_TITLE);
            card.url = cursor.getString(BoneQuery.BONE_URL);

        }

        private class MyCursorCard extends Card {

            String title;
            String url;

            public MyCursorCard(Context context) {
                super(context, R.layout.card_inner_content);
            }

            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {

                //Simple clickListener
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Card title=" + title, Toast.LENGTH_SHORT).show();
                    }
                });


                ViewGroup.LayoutParams lp = view.getLayoutParams();
                lp.height = 800;
                view.setLayoutParams(lp);

                final ImageView imageView = (ImageView) view.findViewById(R.id.image_view);

                if (mImageLoader != null) {
                    mImageLoader.get(url, imageView);
                }

                ((TextView) view.findViewById(R.id.title_text)).setText(title);


            }
        }
    }

    private interface BoneQuery {

        int BONE_ID = 1;
        int BONE_TITLE = 2;
        int BONE_START = 3;
        int BONE_URL = 4;
    }

}