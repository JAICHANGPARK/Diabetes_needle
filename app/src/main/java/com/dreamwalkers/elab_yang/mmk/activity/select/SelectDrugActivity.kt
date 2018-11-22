package com.dreamwalkers.elab_yang.mmk.activity.select

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.dreamwalker.multiselection.MultiSelect
import com.dreamwalker.multiselection.MultiSelectBuilder
import com.dreamwalkers.elab_yang.mmk.R
import com.dreamwalkers.elab_yang.mmk.adapter.drugselect.LeftAdapter
import com.dreamwalkers.elab_yang.mmk.adapter.drugselect.RightAdapter
import com.dreamwalkers.elab_yang.mmk.model.drugselect.Track
import com.dreamwalkers.elab_yang.mmk.model.drugselect.TrackList
import org.json.JSONArray
import org.json.JSONObject
import java.util.logging.Logger

class SelectDrugActivity : AppCompatActivity() {

    private var mMultiSelect: MultiSelect<Track>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_drug)

        setUpToolbar(findViewById<View>(R.id.toolbar) as Toolbar)

        val builder = MultiSelectBuilder(Track::class.java)
                .withContext(this)
                .mountOn(findViewById<View>(R.id.mount_point) as ViewGroup)
                .withSidebarWidth((46 + 8 * 2).toFloat()) // ImageView width with paddings

        setUpAdapters(builder)
        mMultiSelect = builder.build()

        setUpDecoration()
    }

    private fun setUpDecoration() {
        val itemDecorator = TracksItemDecorator(resources.getDimensionPixelSize(R.dimen.decoration_size))
        mMultiSelect!!.recyclerLeft.addItemDecoration(itemDecorator)
        mMultiSelect!!.recyclerRight.addItemDecoration(itemDecorator)
    }

    private fun setUpAdapters(builder: MultiSelectBuilder<Track>) {
        val leftAdapter = LeftAdapter { position -> mMultiSelect!!.select(position) }
        val rightAdapter = RightAdapter { position -> mMultiSelect!!.deselect(position) }

        leftAdapter.addAll(TrackList.TRACKS)

        builder.withLeftAdapter(leftAdapter)
                .withRightAdapter(rightAdapter)
    }

    private fun setUpToolbar(toolbar: Toolbar) {
        toolbar.inflateMenu(R.menu.menu_drug_select)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.select) {
                val items = mMultiSelect!!.selectedItems
                val selectedCount = items!!.size
                val msg: String
                if (selectedCount == 0) {
                    msg = getString(R.string.nothing_selected)
                    mMultiSelect!!.showNotSelectedPage()
                } else {
                    msg = resources.getQuantityString(R.plurals.you_selected_x_songs, selectedCount, selectedCount)
                    mMultiSelect!!.showSelectedPage()
                    for (x in 0 until items.size) {
                        Logger.getLogger(SelectDrugActivity::class.java.name).warning("${items[x].trackName} --> ${items[x].artist}")
                    }
                    //todo 받아들여진 데이터 처리 --> 액티비티간 넘기기

                    val jsonObject = JSONObject()
                    val jsonArray = JSONArray()

                    for (x in 0 until items.size) {
                        var job = JSONObject()
                        job.put("name", items[x].trackName)
                        job.put("kind", items[x].artist)
                        jsonArray.put(job)
                    }

                    jsonObject.put("count", items.size)
                    jsonObject.put("items", jsonArray)

                    Logger.getLogger(SelectDrugActivity::class.java.name).warning(jsonObject.toString())

                    val resultIntent = Intent(this, SelectDrugActivity::class.java)

                    val receiveDate = JSONObject(jsonObject.toString())
                    Logger.getLogger(SelectDrugActivity::class.java.name).warning(receiveDate.getString("count"))

                    val receivedJsonArray = receiveDate.getJSONArray("items")

                    for (x in 0 until receivedJsonArray.length()){
                        Logger.getLogger(SelectDrugActivity::class.java.name).warning( receivedJsonArray.getJSONObject(x).getString("name")   )
                    }


                }
                Snackbar.make(toolbar, msg, Snackbar.LENGTH_LONG).show()
                true
            } else {
                false
            }
        }
    }
}
