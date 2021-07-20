package com.jason.colorwheel.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jason.colorwheel.R
import com.jason.colorwheel.databinding.ViewTemplateColorBinding
import com.jason.colorwheel.model.ColorTemplate
import com.jason.colorwheel.utils.ColorSelectState

class TemplateColorAdapter(private val onColorTemplateSelected: (ColorTemplate) -> Unit) :
    RecyclerView.Adapter<TemplateColorAdapter.TemplateColorViewHolder>() {

    private val colors: MutableList<ColorTemplate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateColorViewHolder {
        val binding = DataBindingUtil.inflate<ViewTemplateColorBinding>(
            LayoutInflater.from(parent.context),
            R.layout.view_template_color,
            parent,
            false
        )

        return TemplateColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateColorViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = colors.size

    fun updateData(data: MutableList<ColorTemplate>) {
        this.colors.clear()
        this.colors.addAll(data)
        notifyDataSetChanged()
    }


    inner class TemplateColorViewHolder(private val dataBinding: ViewTemplateColorBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {
        open fun bind(position: Int) {
            val item = colors[position]
            dataBinding.apply {
                data = item
                setOnColorClicked {
                    if (item.state != ColorSelectState.SELECTED) {
                        onColorTemplateSelected.invoke(item)
                    }
                }
            }
        }
    }
}
