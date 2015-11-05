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

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import jp.fatdaruma.formview.FormView

/**
 * Utilities for widgets in Design Support Library.
 */
internal class DesignWidgetUtil private constructor() {
    companion object {
        val ofTextInputLayout: (View) -> String = {
            (it as TextInputLayout).run {
                editText.editableText.toString()
            }
        }

        fun converters() = arrayListOf (
                TextInputLayout::class.java to ofTextInputLayout
        )

        fun <T: View> mapValidator(listener: FormView.OnParamaterChangedListener, reciever: T) {
            when (reciever.javaClass) {
                TextInputLayout::class.java-> {
                    (reciever as TextInputLayout).editText.addTextChangedListener(object: TextWatcher {
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

                else -> { /* do nothing */ }
            }
        }
    }
}