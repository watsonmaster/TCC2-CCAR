package br.edu.uffs.cc.arcc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_model.view.*

val SELECTED_MODEL_COLOR = Color.parseColor("#08710C") //DarkGreenUFFS
val UNSELECTED_MODEL_COLOR = Color.parseColor("#32B137") //GreenUFFS

class ModelAdapter(
    val models: List<Model>): RecyclerView.Adapter<ModelAdapter.ModelViewHolder>()
{
    var selectedModel = MutableLiveData<Model>()
    private var selectedModelIndex = 0

    inner class ModelViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view)
    }

    override fun getItemCount() = models.size

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        if (selectedModelIndex == holder.layoutPosition)
        {
            holder.itemView.setBackgroundColor(SELECTED_MODEL_COLOR)
            selectedModel.value = models[holder.layoutPosition]
        } else {
            holder.itemView.setBackgroundColor(UNSELECTED_MODEL_COLOR)
        }
        holder.itemView.apply {
            ivThumbnail.setImageResource(models[position].ImageResourceID)
            tvTitle.text = models[position].title

            setOnClickListener {
                selectModel(holder)
            }
        }
    }

    private fun selectModel(holder: ModelViewHolder){
        if(selectedModelIndex != holder.layoutPosition){
            holder.itemView.setBackgroundColor(SELECTED_MODEL_COLOR)
            notifyItemChanged(selectedModelIndex)
            selectedModelIndex = holder.layoutPosition
            selectedModel.value = models[holder.layoutPosition]
        }

    }

}