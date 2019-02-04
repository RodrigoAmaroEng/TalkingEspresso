package br.eng.rodrigoamaro.espressopresentation

import android.app.Activity
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class MainActivity : AppCompatActivity(), KoinComponent {

    private lateinit var editUsername: EditText
    private lateinit var buttonVerify: Button
    private lateinit var buttonVerifyRx: Button
    private lateinit var imageStatus: ImageView
    private lateinit var groupStatus: Group
    private lateinit var textStatus: TextView

    private var isProcessing: Boolean = false

    private val api: Api = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editUsername = findViewById(R.id.edit_username)
        buttonVerify = findViewById(R.id.button_verify)
        buttonVerifyRx = findViewById(R.id.button_verify_rx)
        imageStatus = findViewById(R.id.image_status)
        textStatus = findViewById(R.id.text_status)
        groupStatus = findViewById(R.id.group_status)


        RxView.keys(editUsername)
            .doOnNext { groupStatus.visibility = View.GONE }
            .subscribe()

        RxView.clicks(buttonVerify)
            .filter { !isProcessing }
            .subscribe { check(editUsername.text.toString()) }

        RxView.clicks(buttonVerifyRx)
            .filter { !isProcessing }
            .subscribe { checkRx(editUsername.text.toString()) }
    }

    private fun check(userName: String) {
        imageStatus.setImageResource(R.drawable.loading)
        textStatus.text = "Consultando..."
        groupStatus.visibility = View.VISIBLE
        isProcessing = true
        closeKeyboard()
        val context = get<CoroutineDispatcher>()
        GlobalScope.launch(context) {
            println("CLASSSS ${this.coroutineContext}")
            val isValid = api.coroutineCall(userName)
            isProcessing = false
            if (isValid) {
                imageStatus.setImageResource(R.drawable.success)
                textStatus.text = "Nome disponível"
            } else {
                imageStatus.setImageResource(R.drawable.failure)
                textStatus.text = "Nome indisponível"
            }
        }
    }

    private fun checkRx(userName: String) {
        imageStatus.setImageResource(R.drawable.loading)
        textStatus.text = "Consultando..."
        groupStatus.visibility = View.VISIBLE
        closeKeyboard()
        api.reactiveCall(userName)
            .doOnSubscribe { isProcessing = true }
            .doAfterTerminate { isProcessing = false }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    imageStatus.setImageResource(R.drawable.success)
                    textStatus.text = "Nome disponível"
                } else {
                    imageStatus.setImageResource(R.drawable.failure)
                    textStatus.text = "Nome indisponível"
                }
            }
    }

    private fun closeKeyboard() {
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(editUsername.windowToken, 0)
    }
}


