package com.lp.flashremote.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.FrameLayout

import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

import com.lp.flashremote.R
import com.lp.flashremote.beans.DiskInfo
import com.lp.flashremote.utils.Command2JsonUtil
import com.lp.flashremote.utils.DiskinfoUtil

import com.lp.flashremote.utils.SocketUtil
import com.lp.flashremote.utils.StringUtil
import kotlinx.android.synthetic.main.disk_fagment.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.ArrayList


/**
 * Created by PUJW on 2017/9/12.
 * 磁盘分区
 */
class DiskFragment(val mdiskSocket: SocketUtil) : Fragment() {
    lateinit var rootView: View
    var result: String? = null

    /*val handle: Handler = object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what){
                1->rootView.textView.text=msg.obj.toString()

                0->rootView.textView.text="网络不好，请重新加载。。。"
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rootView = inflater!!.inflate(R.layout.disk_fagment, container, false)

        mdiskSocket.addMessage(StringUtil.operateCmd(Command2JsonUtil
                .getJson("4", null, true)))
        doAsync {
            result = mdiskSocket.readLine()
            uiThread {
                if (result != null) {
                    val diskinfos = DiskinfoUtil.getDisklist(result!!.split("_")[0])
                    diskinfos.forEach {
                        val piechart = PieChart(activity)
                        val layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                800)

                        piechart.layoutParams = layoutParams
                        initpiechart(piechart, it)
                        rootView.line1.addView(piechart)
                    }

                }

            }

        }
        return rootView


    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

    }


    private fun initpiechart(mChart: PieChart, diskInfo: DiskInfo) {

        mChart.setUsePercentValues(false)
        mChart.description.isEnabled = false
        mChart.setBackgroundColor(Color.WHITE)
        mChart.setExtraOffsets(5f, 10f, 60f, 10f)
        mChart.dragDecelerationFrictionCoef = 0.95f
        mChart.rotationAngle = 0f
        mChart.isRotationEnabled = true
        mChart.isHighlightPerTapEnabled = true
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad)


        mChart.setDrawEntryLabels(true)
        mChart.setEntryLabelColor(Color.WHITE)
        mChart.setEntryLabelTextSize(10f)


        mChart.isDrawHoleEnabled = true
        mChart.holeRadius = 28f
        mChart.transparentCircleRadius = 31f
        mChart.setTransparentCircleColor(Color.BLACK)
        mChart.setTransparentCircleAlpha(50)
        mChart.setHoleColor(Color.WHITE)
        mChart.setDrawCenterText(true)

        mChart.centerText = diskInfo.drive+"盘"
        mChart.setCenterTextSize(10f)
        mChart.setCenterTextColor(Color.RED)
        setData(mChart, diskInfo)
    }

    private fun setData(mChart: PieChart, diskInfo: DiskInfo) {
        val nouse: Float = diskInfo.useInfo.toFloat()
        val yesuse = (100 - diskInfo.useInfo.toInt()).toFloat()
        val pieEntryList = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#00c192"))
        colors.add(Color.parseColor("#fe7158"))

        val CashBalance = PieEntry(yesuse, "未使用")
        val ConsumptionBalance = PieEntry(nouse, "已使用")
        pieEntryList.add(CashBalance)
        pieEntryList.add(ConsumptionBalance)

        val pieDataSet = PieDataSet(pieEntryList, "使用图解")
        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 10f
        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        pieData.setValueTextColor(Color.BLUE)
        pieData.setValueTextSize(12f)

        pieData.setValueFormatter(PercentFormatter())
        mChart.setData(pieData)
        mChart.highlightValues(null)
        mChart.invalidate()
    }
}