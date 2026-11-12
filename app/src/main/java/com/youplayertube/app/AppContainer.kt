package com.youplayertube.app

import com.youplayertube.app.data.EmptyLibraryRepository
import com.youplayertube.app.domain.LibraryRepository

class AppContainer {
    val library: LibraryRepository = EmptyLibraryRepository()
}
