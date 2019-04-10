package com.example.loginvalidation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
  var disposables: CompositeDisposable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    disposables = CompositeDisposable()

    disposables?.add(
      Observables
        .combineLatest(
          email.textChanges().skip(1)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t ->
              val isValid: Boolean = !t.isBlank() && Patterns.
                EMAIL_ADDRESS.matcher(t).matches()
              isValid
            },
          password.textChanges().skip(1)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t ->
              val isValid: Boolean = !t.isBlank() && t.length > 6
              isValid

            }
        ) { t1, t2 -> t1 && t2 }
        .observeOn(AndroidSchedulers.mainThread())
        .startWith(false)
        .subscribe { signBtn.isEnabled = it })

  }

  override fun onDestroy() {
    super.onDestroy()
    disposables?.dispose()
  }
}
