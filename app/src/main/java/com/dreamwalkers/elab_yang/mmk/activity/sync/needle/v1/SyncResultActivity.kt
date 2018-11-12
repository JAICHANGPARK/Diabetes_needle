package com.dreamwalkers.elab_yang.mmk.activity.sync.needle.v1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dreamwalkers.elab_yang.mmk.R
import kotlinx.android.synthetic.main.activity_sync_result.*
import org.jetbrains.anko.toast

class SyncResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_result)

        val values = intent.getStringExtra("SyncValue")
        toast(values)

        result_text.text = values
    }
}
