package se.stylianosgakis.sleeptracker.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import se.stylianosgakis.sleeptracker.database.SleepNight
import se.stylianosgakis.sleeptracker.databinding.ListItemSleepNightBinding

class SleepNightAdapter :
    ListAdapter<SleepNight, SleepNightAdapter.QualityViewHolder>(SleepNightDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QualityViewHolder.from(parent)

    override fun onBindViewHolder(holder: QualityViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
        override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight) =
            oldItem.nightId == newItem.nightId

        override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight) =
            oldItem == newItem
    }

    class QualityViewHolder private constructor(
        val binding: ListItemSleepNightBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SleepNight) {
            binding.apply {
                sleep = item
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): QualityViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding
                    .inflate(layoutInflater, parent, false)
                return QualityViewHolder(binding)
            }
        }
    }
}