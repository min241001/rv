package com.android.launcher3.moudle.meet.view;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.dialog.Loading;
import com.android.launcher3.common.network.resp.FriendResp;
import com.android.launcher3.common.utils.AppUtils;
import com.android.launcher3.common.utils.GsonUtil;
import com.android.launcher3.common.utils.ToastUtils;
import com.android.launcher3.moudle.meet.presenter.SearchFriendsPresenter;

import java.util.List;

public class SearchFriendsActivity extends BaseMvpActivity<SearchFriendsView, SearchFriendsPresenter> implements SearchFriendsView {

    private TextView titleTxt;
    private TextView searchNumber;
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private Button btnFive;
    private Button btnSix;
    private Button btnSeven;
    private Button btnEight;
    private Button btnNine;
    private Button btnZero;
    private Button btnOk;
    private RelativeLayout btnDelete;
    private final Loading mLoading = Loading.Companion.init(this);

    @Override
    protected int getResourceId() {
        return R.layout.activity_search_friends;
    }

    @Override
    protected SearchFriendsPresenter createPresenter() {
        return new SearchFriendsPresenter();
    }

    @Override
    protected void initView() {
        titleTxt = findViewById(R.id.title_txt);
        titleTxt.setText(getString(R.string.enter_phone_number));
        searchNumber = findViewById(R.id.search_number);
        btnOne = findViewById(R.id.btn_one);
        btnTwo = findViewById(R.id.btn_two);
        btnThree = findViewById(R.id.btn_three);
        btnFour = findViewById(R.id.btn_four);
        btnFive = findViewById(R.id.btn_five);
        btnSix = findViewById(R.id.btn_six);
        btnSeven = findViewById(R.id.btn_seven);
        btnEight = findViewById(R.id.btn_eight);
        btnNine = findViewById(R.id.btn_nine);
        btnOk = findViewById(R.id.btn_ok);
        btnZero = findViewById(R.id.btn_zero);
        btnDelete = findViewById(R.id.btn_delete);
    }

    @Override
    protected void initEvent() {
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.one));
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.two));
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.three));
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.four));
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.five));
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.six));
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.seven));
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.eight));
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.nine));
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppUtils.isNetworkAvailable(v.getContext()) && !AppUtils.isWifiConnected(v.getContext())) {
                    ToastUtils.show(R.string.search_fail);
                    return;
                }
                if (AppLocalData.getInstance().getWatchId().isEmpty()) {
                    ToastUtils.show(R.string.no_waAcctId);
                    return;
                }
                if (searchNumber.getText().toString().trim().isEmpty()) {
                    ToastUtils.show(R.string.check_tel);
                    return;
                }
                mLoading.startPregress(com.android.launcher3.common.R.layout.loading_view, 30000L);
                mLoading.setCancelable(false);
                mPresenter.searchFri(v.getContext(), searchNumber.getText().toString(), AppLocalData.getInstance().getWatchId());
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchNumber(getString(com.android.launcher3.common.R.string.zero));
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNumber();
            }
        });

        btnDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                searchNumber.setText("");
                return false;
            }
        });

    }

    private void setSearchNumber(String number) {
        searchNumber.setText(searchNumber.getText().toString() + number);
    }

    private void deleteNumber() {
        String text = searchNumber.getText().toString();
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
            searchNumber.setText(text);
        }
    }

    @Override
    public void searchSuccess(List<FriendResp.FriendBean> list, int totalNum) {
        mLoading.stopPregress();
        if (list == null || list.isEmpty()) {
            ToastUtils.show(R.string.telephone_no_register);
            return;
        }
        Intent intent = new Intent(SearchFriendsActivity.this, SearchedFriendsActivity.class);
        intent.putExtra("type", "1");
        intent.putExtra("totalNum", totalNum);
        intent.putExtra("tel", searchNumber.getText().toString());
        // 将List作为额外数据放入Intent中
        String json = GsonUtil.INSTANCE.toJson(list);
        intent.putExtra("friendList", json);
        startActivity(intent);
    }

    @Override
    public void searchFail(int code, String msg) {
        mLoading.stopPregress();
        ToastUtils.show(getString(R.string.search_fail) + msg);
    }
}
