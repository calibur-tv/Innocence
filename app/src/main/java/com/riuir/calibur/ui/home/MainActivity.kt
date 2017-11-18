package com.riuir.calibur.ui.home

import android.os.Bundle
import com.riuir.calibur.R
import com.riuir.calibur.net.ApiClient
import com.riuir.calibur.net.ApiErrorModel
import com.riuir.calibur.net.ApiResponse
import com.riuir.calibur.net.NetworkScheduler
import com.riuir.calibur.utils.toast
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_text.setText(R.string.app_name)
        main_text.setOnClickListener { fetchRepo() }
    }

    fun fetchRepo() {
        ApiClient.instance.service.listRepos(main_text.text.toString())
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<String>(this) {
                    override fun success(data: String) {
                        toast(data)
                    }

                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        toast(apiErrorModel.message, 1)
                    }
                })
    }
}
