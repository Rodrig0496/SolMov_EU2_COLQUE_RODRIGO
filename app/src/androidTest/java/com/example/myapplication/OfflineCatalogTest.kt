package com.example.myapplication

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.local.database.AppDatabase
import com.example.myapplication.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineCatalogTest {
    @Test
    fun refreshPreservesLocalEditsAndDeletes() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = db.productDao()
            val remote = ProductEntity(1, "Remote", 12.0, "Description", "electronics", "")
            dao.mergeRemote(listOf(remote))
            dao.update(remote.copy(title = "Edited offline", locallyModified = true, pendingSync = true))
            dao.mergeRemote(listOf(remote))
            assertEquals("Edited offline", dao.getProductById(1).first()?.title)
            dao.markDeleted(1)
            dao.mergeRemote(listOf(remote))
            assertNull(dao.getProductById(1).first())
            assertTrue(dao.getAllProducts().first().isEmpty())
            assertEquals(1, dao.pendingProducts().size)
        } finally {
            db.close()
        }
    }

    @Test
    fun localProductsHaveUniqueIdsAndAreImmediatelyObservable() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = db.productDao()
            val product = ProductEntity(0, "Local", 10.0, "", "local", "")
            dao.createLocal(product)
            dao.createLocal(product)
            val rows = dao.getAllProducts().first()
            assertEquals(2, rows.size)
            assertEquals(2, rows.map { it.id }.distinct().size)
            assertTrue(rows.all { it.id < -1 && it.pendingSync && it.locallyModified })
        } finally {
            db.close()
        }
    }
}
