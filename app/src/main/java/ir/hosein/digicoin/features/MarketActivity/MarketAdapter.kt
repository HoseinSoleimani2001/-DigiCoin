package ir.hosein.digicoin.features.MarketActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.hosein.digicoin.R
import ir.hosein.digicoin.apiManager.BASE_URL_IMAGE
import ir.hosein.digicoin.apiManager.model.CoinsData
import ir.hosein.digicoin.databinding.ItemRecyclerMarketBinding

class MarketAdapter(
    private val data: ArrayList<CoinsData.Data>,
    private val recyclerCallback: RecyclerCallback
) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    lateinit var binding: ItemRecyclerMarketBinding

    inner class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindView(dataCoin: CoinsData.Data) {

            if (dataCoin.dISPLAY != null && dataCoin.rAW != null) {

                binding.txtCoinName.text = dataCoin.coinInfo.fullName
                binding.txtPrice.text = dataCoin.dISPLAY.uSD.pRICE


                val taghir = dataCoin.rAW.uSD.cHANGEPCT24HOUR
                if (taghir > 0) {
                    binding.txtTaghir.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorGain
                        )
                    )
                    binding.txtTaghir.text =
                        dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 4) + "%"

                } else if (taghir < 0) {
                    binding.txtTaghir.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorLoss
                        )
                    )
                    binding.txtTaghir.text =
                        dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
                } else {
                    binding.txtTaghir.text = "0.00%"
                }

                /* val marketCap = dataCoin.rAW.uSD.mKTCAP / 1000000000
                 val indexDot = marketCap.toString().indexOf('.')
                 binding.txtMarketCap.text = "$" + marketCap.toString().substring(0, indexDot + 3) + " B"*/
                binding.txtMarketCap.text = dataCoin.dISPLAY.uSD.mKTCAP

                Glide
                    .with(itemView)
                    .load(BASE_URL_IMAGE + dataCoin.coinInfo.imageUrl)
                    .into(binding.imgItem)

                itemView.setOnClickListener {
                    recyclerCallback.onCoinItemClicked(dataCoin)
                }
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRecyclerMarketBinding.inflate(inflater, parent, false)

        return MarketViewHolder(binding.root)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindView(data[position])
    }

    interface RecyclerCallback {

        fun onCoinItemClicked(dataCoin: CoinsData.Data)

    }
}