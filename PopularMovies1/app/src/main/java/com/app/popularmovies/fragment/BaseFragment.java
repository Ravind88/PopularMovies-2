package com.app.popularmovies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.popularmovies.R;
import com.app.popularmovies.activity.BaseActivity;
import com.app.popularmovies.utils.MaterialProgressDialog;
import com.app.popularmovies.utils.Utility;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

/**
 * Base class for all fragments to handle common logic
 * Created by ravind maurya on 19 Jan 2016
 */
public abstract class BaseFragment extends Fragment {
    protected View view;
    protected Context mContext;
    private MaterialProgressDialog mMaterialProgressDialog;
    protected Snackbar mSnackBar;
    private ProgressBar mMaterialProgressBar;
    protected View mParent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutById(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        mMaterialProgressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        if (mMaterialProgressBar != null)
            mMaterialProgressBar.setIndeterminateDrawable(new IndeterminateProgressDrawable(mContext));
        mMaterialProgressDialog = Utility.getProgressDialogInstance(mContext);
        mParent = findViewById(R.id.parent);
        initUi(savedInstanceState);
    }

    protected View findViewById(int resId) {
        return view.findViewById(resId);
    }

    protected abstract void initUi(Bundle savedInstanceState);

    protected abstract int getLayoutById();

    public void showProgressDialog(boolean iShow) {
        if (mMaterialProgressDialog != null) {
            if (iShow)
                mMaterialProgressDialog.show();
            else
                mMaterialProgressDialog.dismiss();
        }
    }

    public void showProgressBar(boolean iShow) {

        if (mMaterialProgressBar != null) {
            if (iShow)
                mMaterialProgressBar.setVisibility(View.VISIBLE);
            else
                mMaterialProgressBar.setVisibility(View.GONE);
        }
    }

    protected void setToolBarColor(int resColor) {
        ((BaseActivity) getActivity()).setToolBarColor(resColor);
    }

    protected void setToolBarTextColor(int resColor) {
        ((BaseActivity) getActivity()).setToolBarTextColor(resColor);
    }

}


