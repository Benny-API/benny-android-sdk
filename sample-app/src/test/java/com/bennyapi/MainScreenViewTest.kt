package bennyapi

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_6_PRO
import app.cash.paparazzi.Paparazzi
import com.bennyapi.MainScreen
import org.junit.Rule
import org.junit.Test

class MainScreenViewTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_6_PRO,
        theme = "android:Theme.Material.Light.NoActionBar",

    )

    @Test
    fun mainScreenRenders() {
        paparazzi.snapshot { MainScreen() }
    }
}
