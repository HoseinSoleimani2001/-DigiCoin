package ir.hosein.digicoin.features.MarketActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import ir.hosein.digicoin.apiManager.ApiManager
import ir.hosein.digicoin.apiManager.model.CoinAboutItem
import ir.hosein.digicoin.apiManager.model.CoinsAboutData
import ir.hosein.digicoin.apiManager.model.CoinsData
import ir.hosein.digicoin.databinding.MarketActivityBinding
import ir.hosein.digicoin.features.CoinActivity.CoinActivity

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {
    lateinit var binding: MarketActivityBinding
    val apiManager = ApiManager()
    lateinit var dataNews: ArrayList<Pair<String, String>>
    lateinit var aboutDataMap: MutableMap<String, CoinAboutItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MarketActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutToolbar.toolbar.title = "digiCoin Market"


        binding.layoutWatchlist.btShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }

        binding.swipeRefreshMain.setOnRefreshListener {

            initUi()
            binding.swipeRefreshMain.isRefreshing = false


        }
        getAboutDataFromAssets()
    }

    override fun onResume() {
        super.onResume()

        initUi()
    }

    private fun initUi() {


        getNewsFromApi()
        getTopCoinsFromApi()
    }

    private fun getNewsFromApi() {

        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {

                dataNews = data
                refreshNews()
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "Error = $errorMessage", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun refreshNews() {

        val randomAccess = (0..49).random()
        binding.layoutNews.txtNews.text = dataNews[randomAccess].first
        binding.layoutNews.imgNews.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[randomAccess].second))
            startActivity(intent)
        }
        binding.layoutNews.txtNews.setOnClickListener {
            refreshNews()
        }
    }

    private fun cleanData(data: List<CoinsData.Data>): List<CoinsData.Data> {

        val newData = mutableListOf<CoinsData.Data>()
        data.forEach {
            if(it.rAW != null || it.dISPLAY != null){
                newData.add(it)
            }
        }
        return newData

    }

    private fun getTopCoinsFromApi() {

        apiManager.getCoinsList(object : ApiManager.ApiCallback<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {

                showDataInRecycler(cleanData(data))
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "Error = $errorMessage", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun showDataInRecycler(data: List<CoinsData.Data>) {

        val marketAdapter = MarketAdapter(ArrayList(data), this)
        binding.layoutWatchlist.recyclerMain.adapter = marketAdapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)


    }

    override fun onCoinItemClicked(dataCoin: CoinsData.Data) {

        val intent = Intent(this, CoinActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("bundle1", dataCoin)
        bundle.putParcelable("bundle2", aboutDataMap[dataCoin.coinInfo.name])
        intent.putExtra("bundle", bundle)
        startActivity(intent)

    }

    private fun getAboutDataFromAssets() {

        val fileInString = applicationContext.assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }

        aboutDataMap = mutableMapOf()
        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString, CoinsAboutData::class.java)
        dataAboutAll.forEach {

            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.web,
                it.info.twt,
                it.info.reddit,
                it.info.github,
                it.info.desc
            )

        }

    }
}