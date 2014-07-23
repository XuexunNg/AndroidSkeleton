package sg.com.utrix.skeleton.app.sample.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import sg.com.utrix.skeleton.R;
import sg.com.utrix.skeleton.app.sample.provider.SkeletonContract;
import sg.com.utrix.skeleton.app.sample.provider.SkeletonProvider;
import sg.com.utrix.skeleton.app.sample.ui.listener.EndlessScrollListener;
import sg.com.utrix.skeleton.app.sample.util.ImageLoader;

import static sg.com.utrix.skeleton.app.sample.util.LogUtils.LOGI;
import static sg.com.utrix.skeleton.app.sample.util.LogUtils.makeLogTag;

/**
 * Created by HUANG on 19/7/2014.
 */
public class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = makeLogTag(PlaceholderFragment.class);

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private CursorAdapter mAdapter;
    private ImageLoader mImageLoader;
    private int mPage=1;
    public static SwipeRefreshLayout mSwipeView;

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof ImageLoader.ImageLoaderProvider) {
            mImageLoader = ((ImageLoader.ImageLoaderProvider) getActivity()).getImageLoaderInstance();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final LoaderManager.LoaderCallbacks<Cursor> callbacks = this;
        final GridView listView = (GridView) rootView.findViewById(R.id.bone_list);

        //Swipe to refresh layout
        mSwipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeView.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        mSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeView.setRefreshing(true);
                mPage = 1; //reset to 1
                getLoaderManager().restartLoader(0, null, callbacks);

            }
        });

        //Setup endless scrolling
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mPage++;
                getLoaderManager().restartLoader(0, null, callbacks);
            }
        });

        //Set up Adapter
        mAdapter = new BoneAdapter(getActivity());
        SwingBottomInAnimationAdapter alphaInAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter);
        alphaInAnimationAdapter.setAbsListView(listView);
        listView.setAdapter(alphaInAnimationAdapter);

        getLoaderManager().restartLoader(0, null, this);

        return rootView;
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

        return new CursorLoader(getActivity(),uri, null, null, null, SkeletonContract.Bones.BONE_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private class BoneAdapter extends CursorAdapter {
        public BoneAdapter(Context context) {
            super(context, null, 0);
        }

        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(R.layout.fragment_home_item, parent,
                    false);
        }

        /** {@inheritDoc} */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final ImageView imageView = (ImageView) view.findViewById(R.id.image_view);

            if (mImageLoader != null) {
                mImageLoader.get(cursor.getString(BoneQuery.BONE_URL), imageView);
            }

            ((TextView) view.findViewById(R.id.title_text)).setText(cursor
                    .getString(BoneQuery.BONE_TITLE));

        }
    }

    private interface BoneQuery {


        int BONE_ID = 1;
        int BONE_TITLE = 2;
        int BONE_START = 3;
        int BONE_URL = 4;
    }
}