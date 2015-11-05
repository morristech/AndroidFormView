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

package jp.fatdaruma.formview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import jp.fatdaruma.formview.utils.AppCompatWidgetUtil
import jp.fatdaruma.formview.utils.WidgetUtil
import jp.fatdaruma.formview.utils.DesignWidgetUtil
import java.util.*
import android.view.ViewGroup.LayoutParams as VGParams
import android.widget.RelativeLayout.LayoutParams as RLParams

/**
 * This is html form-like view.
 * This has a scrollable view and a container (vertical linear layout).
 * Managing input values on views which this contains is completed in the inside of this.
 */
public open class FormView: RelativeLayout {

    val container: LinearLayout
    val scrollView: ScrollView

    constructor(context: Context): this(context, null, 0) // redirect
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0) // redirect
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        scrollView = ScrollView(context).apply {
            this@FormView.addView(this, VGParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }

        container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            scrollView.addView(this, VGParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        }
    }

    private val biKVMap = BidirectionalFakeMap<String, View>()

    private val valueConverterMap = HashMap<Class<out View>, (View) -> String>().apply {
        WidgetUtil.converters().forEach {
            put(it.first, it.second)
        }
        AppCompatWidgetUtil.converters().forEach {
            put(it.first, it.second)
        }
        DesignWidgetUtil.converters().forEach {
            put(it.first, it.second)
        }
    }

    private val validatorMap = HashMap<String, (String?) -> Boolean>()
    private val validateResultMap = HashMap<String, Boolean>()

    private val validateListener = object: OnParamaterChangedListener {
        override fun <T : View> onParamChanged(view: T) {
            biKVMap.getKey(view)?.let { key ->
                validatorMap[key]?.invoke(view.asValue())?.apply {
                    validateResultMap.put(key, this)
                }
            }
        }
    }

    private fun addNew(key: String, view: View, validator: ((String?) -> Boolean)?, f: LinearLayout.() -> Unit) {
        biKVMap.put(key, view)

        if (validator != null) {
            validatorMap.put(key, validator)
            mapValidatorToView(view)
        }

        with(container, f)
    }

    private fun <T: View> mapValidatorToView(view: T) {
        WidgetUtil.mapValidator(validateListener, view)
        DesignWidgetUtil.mapValidator(validateListener, view)
        AppCompatWidgetUtil.mapValidator(validateListener, view)
    }

    fun putValidator(key: String, validator: (String?) -> Boolean) {
        biKVMap.getValue(key)?.apply {
            validatorMap.put(key, validator)
            mapValidatorToView(this)
        }
    }

    fun removeValidator(key: String) {
        validatorMap.remove(key)
        validateResultMap.remove(key)
    }

    fun isValid(): Boolean =
            validatorMap.map { it.key }.map {
                validateResultMap.getOrElse(it, { false })
            }.reduce { lb, rb -> lb && rb }

    fun addConverter(classObject: Class<out View>, converter: (View) -> String) = valueConverterMap.put(classObject, converter)

    fun removeConverter(classObject: Class<out View>) = valueConverterMap.remove(classObject)

    fun hasConveter(classObject: Class<out View>) = valueConverterMap.containsKey(classObject)

    fun addAll(keys: Array<String>, parentView: ViewGroup) {
        with(0.until(parentView.childCount).map { parentView.getChildAt(it) }) {
            addAll(keys, this)
        }
    }

    fun addAll(keys: Array<String>, views: List<View>) {
        keys.zip(views).forEach { kv ->
            kv.second.parent?.apply {
                (this as ViewGroup).removeView(kv.second)
            }

            add(kv.first, kv.second)
        }
    }

    fun add(key: String, view: View, validator: ((String?) -> Boolean)? = null) =
            addNew(key, view, validator) {
                addView(view)
            }

    fun add(key: String, view: View, params: VGParams, validator: ((String?) -> Boolean)? = null) =
            addNew(key, view, validator) {
                addView(view, params)
            }

    fun add(key: String, view: View, index: Int, validator: ((String?) -> Boolean)? = null) =
            addNew(key, view, validator) {
                addView(view, index)
            }

    fun add(key: String, view: View, index: Int, params: VGParams, validator: ((String?) -> Boolean)? = null) =
            addNew(key, view, validator) {
                addView(view, index, params)
            }

    fun remove(key: String) {
        biKVMap.getValue(key)?.apply {
            removeValidator(key)

            parent?.let {
                container.removeView(this)
            }
        }
    }

    fun remove(view: View?) {
        view?.apply {
            biKVMap.getKey(view)?.apply {
                removeValidator(this)
                biKVMap.remove(this)
            }

            parent?.let {
                container.removeView(view)
            }
        }
    }

    override fun removeAllViews() {
        container.removeAllViews()
        biKVMap.clear()
        validateResultMap.clear()
        validatorMap.clear()
    }

    fun getParams() =
            0.until(container.childCount).map {
                container.getChildAt(it)
            }.map {
                biKVMap.getKey(it) to it.asValue()
            }.toMap()

    // To avoid the name conflict
    private fun View.asValue(): String =
            if (this is FormPart) {
                toValue()
            } else {
                valueConverterMap[javaClass]?.run {
                    invoke(this@asValue)
                }.orEmpty()
            }

    /**
     * Bidirectional relations between Key and Value can be used.
     * This is not TRUE MutableMap, but have some of MutableMap features.
     */
    private class BidirectionalFakeMap<K, V>{
        private val KVS: MutableMap<K, V> = HashMap()
        private val VKS: MutableMap<V, K> = HashMap()

        val size: Int
            get() = Math.min(KVS.size, VKS.size)

        fun isEmpty(): Boolean = KVS.isEmpty() || VKS.isEmpty()

        fun containsKey(key: K): Boolean = KVS.containsKey(key)

        fun containsValue(value: V): Boolean = VKS.containsKey(value)

        fun remove(key: K): V? =
                KVS.remove(key)?.apply {
                    VKS.remove(this)
                }

        fun put(key: K, value: V): V? {
            KVS.put(key, value)
            VKS.put(value, key)
            return value
        }

        fun getValue(key: K) = KVS[key]
        fun getKey(value: V) = VKS[value]

        fun clear() {
            KVS.clear()
            VKS.clear()
        }
    }

    internal interface OnParamaterChangedListener {
        fun <T: View> onParamChanged(view: T)
    }
}