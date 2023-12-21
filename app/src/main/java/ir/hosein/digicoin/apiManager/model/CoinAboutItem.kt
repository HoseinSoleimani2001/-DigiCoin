package ir.hosein.digicoin.apiManager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinAboutItem(

    var coinWebsite: String? = "no-data",
    var coinTwitter: String? = "no-data",
    var coinReddit: String? = "no-data",
    var coinGitHub: String? = "no-data",
    var coinDesc: String? = "no-data"
):Parcelable
