package com.youplayertube.app

import android.app.Application

class YouPlayerTubeApplication : Application() {
    val container by lazy { AppContainer(this) }
}
