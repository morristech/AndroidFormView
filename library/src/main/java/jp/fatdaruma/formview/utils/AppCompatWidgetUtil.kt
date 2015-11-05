/*
 Copyright 2015 Jumpei Matsuda

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package jp.fatdaruma.formview.utils

import android.support.v7.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import jp.fatdaruma.formview.FormView

/**
 * Utilities for widgets in AppCompat.
 */
internal class AppCompatWidgetUtil private constructor() {
    companion object {
        val ofAppCompatEditText                  = WidgetUtil.ofEditText
        val ofAppCompatAutoCompleteTextView      = WidgetUtil.ofAutoCompleteTextView
        val ofAppCompatMultiAutoCompleteTextView = WidgetUtil.ofMultiAutoCompleteTextView
        val ofAppCompatCheckBox                  = WidgetUtil.ofCheckBox
        val ofAppCompatRadioButton               = WidgetUtil.ofRadioButton
        val ofAppCompatSpinner                   = WidgetUtil.ofSpinner
        val ofAppCompatSeekBar                   = WidgetUtil.ofSeekBar
        val ofAppCompatRatingBar                 = WidgetUtil.ofRatingBar

        // followings don't exist
        //        val ofAppCompatSwitch                    = ConverterUtil.ofSwitch
        //        val ofAppCompatRadioGroup                = ConverterUtil.ofRadioGroup
        //        val ofAppCompatToggleButton              = ConverterUtil.ofToggleButton
        //        val ofAppCompatDatePicker                = ConverterUtil.ofDatePicker
        //        val ofAppCompatTimePicker                = ConverterUtil.ofTimePicker

        fun converters() = arrayListOf (
                AppCompatEditText::class.java                  to ofAppCompatEditText,
                AppCompatAutoCompleteTextView::class.java      to ofAppCompatAutoCompleteTextView,
                AppCompatCheckBox::class.java                  to ofAppCompatCheckBox,
                AppCompatMultiAutoCompleteTextView::class.java to ofAppCompatMultiAutoCompleteTextView,
                AppCompatRadioButton::class.java               to ofAppCompatRadioButton,
                AppCompatSpinner::class.java                   to ofAppCompatSpinner,
                AppCompatSeekBar::class.java                   to ofAppCompatSeekBar,
                AppCompatRatingBar::class.java                 to ofAppCompatRatingBar
        )

        fun <T: View> mapValidator(listener: FormView.OnParamaterChangedListener, reciever: T) {
            when (reciever.javaClass) {
                AppCompatEditText::class.java,
                AppCompatAutoCompleteTextView::class.java,
                AppCompatMultiAutoCompleteTextView::class.java-> {
                    (reciever as EditText).addTextChangedListener(object: TextWatcher {
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            // none
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            // none
                        }

                        override fun afterTextChanged(s: Editable?) {
                            listener.onParamChanged(reciever)
                        }
                    })
                }

                AppCompatCheckBox::class.java,
                AppCompatRadioButton::class.java -> {
                    (reciever as CompoundButton).setOnCheckedChangeListener { btn, bool ->
                        listener.onParamChanged(reciever)
                    }
                }

                AppCompatSpinner::class.java -> {
                    (reciever as Spinner).onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            listener.onParamChanged(reciever)
                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            listener.onParamChanged(reciever)
                        }
                    }
                }

                AppCompatSeekBar::class.java -> {
                    (reciever as SeekBar).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            listener.onParamChanged(seekBar)
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {
                        }

                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        }
                    })
                }

                AppCompatRatingBar::class.java -> {
                    (reciever as RatingBar).onRatingBarChangeListener = object: RatingBar.OnRatingBarChangeListener {
                        override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
                            listener.onParamChanged(ratingBar)
                        }
                    }
                }

                else -> { /* do nothing */ }
            }
        }
    }
}