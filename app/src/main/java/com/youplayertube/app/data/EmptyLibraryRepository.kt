package com.youplayertube.app.data

import com.youplayertube.app.domain.LibraryRepository
import com.youplayertube.app.domain.LibrarySnapshot

/** No import/persistence exists yet, so the library is genuinely empty. */
class EmptyLibraryRepository : LibraryRepository {
    override fun snapshot() = LibrarySnapshot()
}
