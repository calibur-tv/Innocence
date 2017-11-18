package com.riuir.calibur

import android.os.Bundle
import android.widget.Toast
import com.riuir.calibur.net.ApiClient
import com.riuir.calibur.net.ApiErrorModel
import com.riuir.calibur.net.ApiResponse
import com.riuir.calibur.net.NetworkScheduler
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
    private fun fetchRepo() {
        ApiClient.instance.service.listRepos(main_text.text.toString())
                .compose(NetworkScheduler.compose())
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe(object : ApiResponse<String>(this) {
                    override fun success(data: String) {
                    }

                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
                        Toast.makeText(this@MainActivity, apiErrorModel.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }
}
