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

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView.OnItemSelectedListener as forSpinnerListener
import android.widget.*
import jp.fatdaruma.formview.FormView
import java.util.*

/**
 * Utilities for common widgets.
 */
internal class WidgetUtil private constructor() {
    companion object {
        val ofEditText: (View) -> String = {
            (it as EditText).run {
                editableText.toString()
            }
        }

        val ofAutoCompleteTextView = ofEditText
        val ofMultiAutoCompleteTextView = ofEditText

        val ofCompoundButton: (View) -> String = {
            (it as CompoundButton).run {
                isChecked.toString()
            }
        }

        val ofCheckBox = ofCompoundButton
        val ofToggleButton = ofCompoundButton
        val ofSwitch = ofCompoundButton
        val ofRadioButton = ofCompoundButton

        val ofRadioGroup: (View) -> String = {
            (it as RadioGroup).run {
                checkedRadioButtonId.toString()
            }
        }

        val ofSpinner: (View) -> String = {
            (it as Spinner).run {
                selectedItemPosition.toString()
            }
        }

        val ofDatePicker: (View) -> String = {
            (it as DatePicker).run {
                "$year/$month/$dayOfMonth"
            }
        }

        val ofTimePicker: (View) -> String = {
            (it as TimePicker).run {
                "$hour:$minute"
            }
        }

        val ofNumberPicker: (View) -> String = {
            (it as NumberPicker).run {
                value.toString()
            }
        }

        val ofSeekBar: (View) -> String = {
            (it as SeekBar).run {
                progress.toString()
            }
        }

        val ofRatingBar: (View) -> String = {
            (it as RatingBar).run {
                rating.toString()
            }
        }

        fun converters() = arrayListOf (
                EditText::class.java                  to ofEditText,
                AutoCompleteTextView::class.java      to ofAutoCompleteTextView,
                MultiAutoCompleteTextView::class.java to ofMultiAutoCompleteTextView,
                CheckBox::class.java                  to ofCheckBox,
                ToggleButton::class.java              to ofToggleButton,
                Switch::class.java                    to ofSwitch,
                RadioButton::class.java               to ofRadioButton,
                RadioGroup::class.java                to ofRadioGroup,
                Spinner::class.java                   to ofSpinner,
                DatePicker::class.java                to ofDatePicker,
                TimePicker::class.java                to ofTimePicker,
                NumberPicker::class.java              to ofNumberPicker,
                SeekBar::class.java                   to ofSeekBar,
                RatingBar::class.java                 to ofRatingBar
        )

        fun <T: View> mapValidator(listener: FormView.OnParamaterChangedListener, reciever: T) {
            when (reciever.javaClass) {
                EditText::class.java,
                AutoCompleteTextView::class.java,
                MultiAutoCompleteTextView::class.java-> {
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

                CheckBox::class.java,
                ToggleButton::class.java,
                Switch::class.java,
                RadioButton::class.java -> {
                    (reciever as CompoundButton).setOnCheckedChangeListener { btn, bool ->
                        listener.onParamChanged(reciever)
                    }
                }

                RadioGroup::class.java -> {
                    (reciever as RadioGroup).setOnCheckedChangeListener { radioGroup, checkedId ->
                        listener.onParamChanged(radioGroup)
                    }
                }

                Spinner::class.java -> {
                    (reciever as Spinner).onItemSelectedListener = object: forSpinnerListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            listener.onParamChanged(reciever)
                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            listener.onParamChanged(reciever)
                        }
                    }
                }

                DatePicker::class.java -> {
                    val year = GregorianCalendar().get(GregorianCalendar.YEAR)
                    val month = GregorianCalendar().get(GregorianCalendar.MONTH)
                    val dayOfMonth = GregorianCalendar().get(GregorianCalendar.DAY_OF_MONTH)
                    (reciever as DatePicker).init(year, month, dayOfMonth, { picker, y, m, d ->
                        listener.onParamChanged(picker)
                    })
                }

                TimePicker::class.java -> {
                    (reciever as TimePicker).setOnTimeChangedListener { picker, h, m ->
                        listener.onParamChanged(picker)
                    }
                }

                NumberPicker::class.java -> {
                    (reciever as NumberPicker).setOnValueChangedListener { picker, oldValue, newValue ->
                        listener.onParamChanged(picker)
                    }
                }

                SeekBar::class.java -> {
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

                RatingBar::class.java -> {
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