package ir.hosein.digicoin.features.CoinActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import ir.hosein.digicoin.R
import ir.hosein.digicoin.apiManager.ALL
import ir.hosein.digicoin.apiManager.ApiManager
import ir.hosein.digicoin.apiManager.BASE_URL_TWITTER
import ir.hosein.digicoin.apiManager.HOUR
import ir.hosein.digicoin.apiManager.HOURS24
import ir.hosein.digicoin.apiManager.MONTH
import ir.hosein.digicoin.apiManager.MONTH3
import ir.hosein.digicoin.apiManager.WEEK
import ir.hosein.digicoin.apiManager.YEAR
import ir.hosein.digicoin.apiManager.model.ChartData
import ir.hosein.digicoin.apiManager.model.CoinAboutItem
import ir.hosein.digicoin.apiManager.model.CoinsData
import ir.hosein.digicoin.databinding.ActivityCoinBinding

@Suppress("DEPRECATION")
class CoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoinBinding
    lateinit var dataThisCoin: CoinsData.Data
    lateinit var dataThisCoinAbout: CoinAboutItem
    val apiManager = ApiManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.layoutToolbar.toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val fromIntent = intent.getBundleExtra("bundle")!!
        dataThisCoin = fromIntent.getParcelable<CoinsData.Data>("bundle1")!!
        if (fromIntent.getParcelable<CoinAboutItem>("bundle2") != null) {
            dataThisCoinAbout = fromIntent.getParcelable<CoinAboutItem>("bundle2")!!
        } else {
            dataThisCoinAbout = CoinAboutItem()
        }

        binding.layoutToolbar.toolbar.title = dataThisCoin.coinInfo.fullName
        initUi()
    }

    private fun initUi() {
        initChartUi()
        initStatisticUi()
        initAboutUi()
    }

    @SuppressLint("SetTextI18n")
    private fun initAboutUi() {

        binding.layoutAbout.txtWebsite.text = dataThisCoinAbout.coinWebsite
        binding.layoutAbout.txtGithub.text = dataThisCoinAbout.coinGitHub
        binding.layoutAbout.txtReddit.text = dataThisCoinAbout.coinReddit
        binding.layoutAbout.txtTwitter.text = "@" + dataThisCoinAbout.coinTwitter
        binding.layoutAbout.txtAboutCoin.text = dataThisCoinAbout.coinDesc

        binding.layoutAbout.txtWebsite.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!)
        }
        binding.layoutAbout.txtGithub.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinGitHub!!)
        }
        binding.layoutAbout.txtReddit.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinReddit!!)
        }
        binding.layoutAbout.txtTwitter.setOnClickListener {
            openWebsiteDataCoin(BASE_URL_TWITTER + dataThisCoinAbout.coinTwitter!!)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initStatisticUi() {


        binding.layoutStatistic.tvOpenAmount.text = dataThisCoin.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistic.tvTodaysHighAmount.text = dataThisCoin.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistic.tvTodayLowAmount.text = dataThisCoin.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistic.tvChangeTodayAmount.text = dataThisCoin.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistic.tvAlgorithm.text = dataThisCoin.coinInfo.algorithm
        binding.layoutStatistic.tvTotalVolume.text = dataThisCoin.dISPLAY.uSD.tOTALVOLUME24H
        binding.layoutStatistic.tvAvgMarketCapAmount.text = dataThisCoin.dISPLAY.uSD.mKTCAP
        binding.layoutStatistic.tvSupplyNumber.text = dataThisCoin.dISPLAY.uSD.sUPPLY
    }

    @SuppressLint("SetTextI18n")
    private fun initChartUi() {

        var period: String = HOUR
        requestAndShowData(period)
        binding.layoutChart.radioGroupMain.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.radio_12h -> {
                    period = HOUR
                }

                R.id.radio_1d -> {
                    period = HOURS24
                }

                R.id.radio_1w -> {
                    period = WEEK
                }

                R.id.radio_1m -> {
                    period = MONTH
                }

                R.id.radio_3m -> {
                    period = MONTH3
                }

                R.id.radio_1y -> {
                    period = YEAR
                }

                R.id.radio_all -> {
                    period = ALL
                }
            }
            requestAndShowData(period)
        }
        binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
        binding.layoutChart.txtChartChange1.text = " " + dataThisCoin.dISPLAY.uSD.cHANGEPCT24HOUR

        if (dataThisCoin.rAW.uSD.cHANGEPCT24HOUR == 0.0){
            binding.layoutChart.txtChartChange2.text = "0.00%"

        }else{
            binding.layoutChart.txtChartChange2.text =
                dataThisCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
        }
        val taghir = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR
        if (taghir > 0) {
            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )
            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )
            binding.layoutChart.txtChartUpdown.text = "▲"
            binding.layoutChart.sparkViewMain.lineColor = (ContextCompat.getColor(
                binding.root.context,
                R.color.colorGain
            ))
        } else {
            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )
            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )
            binding.layoutChart.txtChartUpdown.text = "▼"
            binding.layoutChart.sparkViewMain.lineColor = (ContextCompat.getColor(
                binding.root.context,
                R.color.colorLoss
            ))
        }

        binding.layoutChart.sparkViewMain.setScrubListener {
            if (it == null){
                binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
            }else{
                binding.layoutChart.txtChartPrice.text = "$"+(it as ChartData.Data).close.toString()
            }
        }


    }

    private fun requestAndShowData(period: String) {
        apiManager.getChartData(dataThisCoin.coinInfo.name, period, object :
            ApiManager.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
            override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {

                val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                binding.layoutChart.sparkViewMain.adapter = chartAdapter

            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@CoinActivity, "Error =>$errorMessage", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun openWebsiteDataCoin(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true

    }
}