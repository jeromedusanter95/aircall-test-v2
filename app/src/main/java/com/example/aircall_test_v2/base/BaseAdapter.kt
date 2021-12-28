package com.example.aircall_test_v2.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.aircall_test_v2.BR
import com.example.aircall_test_v2.utils.cast
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions

abstract class BaseAdapter<B : ViewDataBinding, O : IUiModel, VM : BaseItemViewModel<O>>(
    @LayoutRes protected open val resId: Int
) : RecyclerView.Adapter<BaseAdapter.ViewHolder<B>>() {

    val selection: MutableList<O> = mutableListOf()
    val list: MutableList<O> = mutableListOf()
    val empty = ObservableBoolean(true)
    var mode = Mode.NO_SELECTION

    private var lastSelectedItemIndex = -1

    abstract fun createViewModel(): VM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        val binding = DataBindingUtil.inflate<B>(
            LayoutInflater.from(parent.context),
            resId,
            parent,
            false
        )
        binding.setVariable(BR.viewModel, createViewModel())
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        holder.binding::class.memberFunctions.find {
            it.name == "getViewModel"
        }
            ?.cast<KFunction<VM>>()
            ?.let {
                val viewModel: VM = it.call(holder.binding)
                val item = list[position]

                if (mode == Mode.NO_SELECTION) {
                    viewModel.setNewItem(item, false)
                } else {
                    viewModel.setNewItem(item, selection.contains(item))
                }

                holder.binding.root.setOnClickListener {
                    selectItem(list[position])
                    viewModel.onItemClick?.invoke(item, position)
                }
            }
    }

    override fun getItemCount() = list.size

    fun addOrUpdateItem(item: O, predicate: (O) -> Boolean, updatePredicate: (O) -> Boolean) {
        if (list.all(predicate)) {
            addItem(item)
        } else updateItem(updatePredicate, item)

        empty.set(list.isEmpty())
    }

    fun addOrUpdateItem(item: O, predicate: (O) -> Boolean) {
        if (list.all(predicate)) {
            addItem(item)
        } else {
            val index: Int = list.indexOfFirst { !predicate(it) }

            if (index != -1)
                updateItem(item, index)
        }
        empty.set(list.isEmpty())
    }

    fun addOrUpdateItem(item: O, index: Int, predicate: (O) -> Boolean) {
        if (list.all(predicate)) {
            addItem(item, index)
        } else {
            val index: Int = list.indexOfFirst { !predicate(it) }

            if (index != -1)
                updateItem(item, index)
        }
        empty.set(list.isEmpty())
    }

    fun addItem(item: O, predicate: (O) -> Boolean) {
        if (list.all(predicate)) {
            addItem(item)
        }
        empty.set(list.isEmpty())
    }

    fun addItem(item: O, index: Int, predicate: (O) -> Boolean) {
        if (list.all(predicate)) {
            addItem(item, index)
        }
        empty.set(list.isEmpty())
    }

    open fun addItem(item: O, index: Int = list.size, clear: Boolean = false) {
        if (clear)
            list.clear()

        list.add(if (index <= list.size && index >= 0) index else 0, item)
        notifyItemInserted(index)
        empty.set(list.isEmpty())
    }

    open fun addItems(items: List<O>, clear: Boolean = false) {
        if (clear)
            list.clear()

        val previous: Int = list.size
        list.addAll(items)
        if (clear)
            notifyDataSetChanged()
        else
            notifyItemRangeInserted(previous, list.size)
        empty.set(list.isEmpty())
    }

    fun updateItem(predicate: (O) -> Boolean, item: O) {
        val index = list.indexOfFirst { predicate(it) }

        if (index != -1)
            updateItem(item, index)
        empty.set(list.isEmpty())
    }

    open fun updateItem(item: O, index: Int) {
        list[index] = item
        notifyItemChanged(index)
        empty.set(list.isEmpty())
    }

    fun swapItem(from: Int, to: Int) {
        Collections.swap(list, from, to)
        notifyItemChanged(from)
        notifyItemChanged(to)
    }

    fun removeItem(predicate: (O) -> Boolean) {
        val index = list.indexOfFirst { predicate(it) }

        if (index != -1)
            removeItem(index)
        empty.set(list.isEmpty())
    }

    fun removeItem(item: O) {
        val index = list.indexOfFirst { it == item }

        if (index != -1) {
            list.removeAt(index)
            notifyItemRemoved(index)
        }
        empty.set(list.isEmpty())
    }

    fun removeItem(index: Int) {
        if (index < list.size && index >= 0) {
            list.removeAt(index)
            notifyItemRemoved(index)
        }
        empty.set(list.isEmpty())
    }

    fun removeItems(items: List<O>) {
        items.forEach { removeItem(it) }
        empty.set(list.isEmpty())
    }

    fun has(predicate: (O) -> Boolean): Boolean {
        return list.any(predicate)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
        empty.set(list.isEmpty())
    }

    // Selection //
    private fun selectItem(item: O) {
        val index = list.indexOfFirst { it == item }
        if (mode == Mode.MULTIPLE_SELECTION) {
            if (selection.contains(item)) {
                selection.remove(item)
            } else {
                selection.add(item)
            }
            notifyItemChanged(index)
        } else if (mode == Mode.SINGLE_SELECTION) {
            if (selection.size > 0 && lastSelectedItemIndex != index) {
                selection.clear()
                notifyItemChanged(lastSelectedItemIndex)
                selection.add(item)
                notifyItemChanged(index)
            } else if (selection.size > 0 && lastSelectedItemIndex == index) {
                selection.clear()
                notifyItemChanged(lastSelectedItemIndex)
            } else {
                selection.add(item)
                notifyItemChanged(index)
            }
        }

        lastSelectedItemIndex = index
    }

    fun list(list: List<O>, clear: Boolean = true) {
        if (clear) this.list.clear()
        this.list.addAll(list)
    }

    fun into(recyclerView: RecyclerView) {
        recyclerView.adapter = this
    }

    class ViewHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)

    enum class Mode {
        NO_SELECTION, SINGLE_SELECTION, MULTIPLE_SELECTION
    }
}