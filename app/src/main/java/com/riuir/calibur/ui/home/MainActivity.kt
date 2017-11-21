package com.riuir.calibur.ui.home

import android.os.Bundle
import com.riuir.calibur.R
import com.riuir.calibur.ui.widget.MainBottomBar
import com.riuir.calibur.utils.toast
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity(), MainBottomBar.OnSingleClickListener {

    private var fragmentMain: MainFragment = MainFragment()
    private var fragmentDrama: DramaFragment = DramaFragment()
    private var fragmentMessage: MessageFragment = MessageFragment()
    private var fragmentMine: MineFragment = MineFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        maintab_bottombar.init()
        maintab_bottombar.setSelectDis()
        maintab_bottombar.setOnSingleClickListener(this)
        supportFragmentManager
                .beginTransaction()
                .add(R.id.framelayout_main, fragmentMain)
                .add(R.id.framelayout_main, fragmentDrama)
                .add(R.id.framelayout_main, fragmentMessage)
                .add(R.id.framelayout_main, fragmentMine)
                .hide(fragmentMain)
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .commit()
        supportFragmentManager
                .beginTransaction()
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .show(fragmentMain)
                .commitAllowingStateLoss()
    }

//    fun fetchRepo() {
//        ApiClient.instance.service.listRepos(main_text.text.toString())
//                .compose(NetworkScheduler.compose())
//                .bindUntilEvent(this, ActivityEvent.DESTROY)
//                .subscribe(object : ApiResponse<String>(this) {
//                    override fun success(data: String) {
//                        toast(data)
//                    }
//
//                    override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {
//                        toast(apiErrorModel.message, 1)
//                    }
//                })
//    }

    override fun onClickAdd() {
        toast("新建")
    }

    override fun onClickDis() {
        supportFragmentManager
                .beginTransaction()
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .show(fragmentMain)
                .commitAllowingStateLoss()
    }

    override fun onClickCircle() {
        supportFragmentManager
                .beginTransaction()
                .hide(fragmentMain)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .show(fragmentDrama)
                .commitAllowingStateLoss()
    }

    override fun onClickMsg() {
        supportFragmentManager
                .beginTransaction()
                .hide(fragmentMain)
                .hide(fragmentDrama)
                .hide(fragmentMine)
                .show(fragmentMessage)
                .commitAllowingStateLoss()
    }

    override fun onClickMine() {
        supportFragmentManager
                .beginTransaction()
                .hide(fragmentMain)
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .show(fragmentMine)
                .commitAllowingStateLoss()
    }
}
