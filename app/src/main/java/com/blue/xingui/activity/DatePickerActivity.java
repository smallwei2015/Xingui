package com.blue.xingui.activity;

import android.os.Bundle;
import android.util.Log;

import com.blue.xingui.R;
import com.blue.xingui.base.BaseActivity;
import com.tubb.calendarselector.library.CalendarSelector;
import com.tubb.calendarselector.library.FullDay;
import com.tubb.calendarselector.library.MonthView;
import com.tubb.calendarselector.library.SCMonth;
import com.tubb.calendarselector.library.SegmentSelectListener;
import com.tubb.calendarselector.library.SingleMonthSelector;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class DatePickerActivity extends BaseActivity {

    @ViewInject(R.id.pick_month)
    MonthView monthView;

    @Override
    public void initView() {
        super.initView();

        initTop(R.mipmap.left_gray, "日期选择", -1);


        final List<SCMonth> data = new ArrayList<>();

        SCMonth scMonth = new SCMonth(2017, 7, SCMonth.SUNDAY_OF_WEEK);

        SCMonth scMonth1 = new SCMonth(2017, 8, SCMonth.SUNDAY_OF_WEEK);
        data.add(scMonth);
        data.add(scMonth1);
        SingleMonthSelector selector = new CalendarSelector(data, CalendarSelector.Mode.SEGMENT);

        selector.setSegmentSelectListener(new SegmentSelectListener() {
            @Override
            public void onSegmentSelect(FullDay startDay, FullDay endDay) {

            }
        });

        monthView.setSCMonth(scMonth);
        monthView.setMonthDayClickListener(new MonthView.OnMonthDayClickListener() {
            @Override
            public void onMonthDayClick(FullDay day) {

                Log.w("3333", day.toString());
                finish();
            }
        });
        selector.bind(monthView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        x.view().inject(this);
        initView();
        initData();
    }
}
