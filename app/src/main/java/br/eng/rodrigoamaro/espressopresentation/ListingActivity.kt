package br.eng.rodrigoamaro.espressopresentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView

class ListingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)
        val list = findViewById<RecyclerView>(R.id.listing)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = SimpleAdapter(
            Item("Alemanha", false),
            Item("Argentina", true),
            Item("Bolívia", true),
            Item("Brasil", true),
            Item("Canadá", false),
            Item("Chile", true),
            Item("Dinamarca", false),
            Item("Dominica", false),
            Item("Egito", false),
            Item("Espanha", false),
            Item("Finlândia", false),
            Item("França", false),
            Item("Gana", false),
            Item("Guatemala", false),
            Item("Haiti", false)
        )
    }
}

data class Item(var country: String, var selected: Boolean)

class SimpleHolder(val view: CheckedTextView) : RecyclerView.ViewHolder(view) {

    fun bind(item: Item) {
        view.text = item.country
        view.isChecked = item.selected
    }
}

class SimpleAdapter(vararg list: Item) :
    RecyclerView.Adapter<SimpleHolder>() {
    private val entries = list
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SimpleHolder {
        return SimpleHolder(
            LayoutInflater.from(p0.context).inflate(
                android.R.layout.simple_list_item_checked,
                null
            ) as CheckedTextView
        )
    }

    override fun getItemCount(): Int = entries.size

    override fun onBindViewHolder(p0: SimpleHolder, p1: Int) {
        val item = entries[p1]
        p0.view.setOnClickListener {
            item.selected = !item.selected
            notifyItemChanged(p1)
        }
        p0.bind(item)
    }
}