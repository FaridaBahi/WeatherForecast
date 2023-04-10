package com.example.myweather.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.myweather.model.Favourite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.collection.IsEmptyCollection
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DAOTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var db: WeatherDataBase
    private lateinit var dao: WeatherDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()
        dao = db.getWeatherDao()
    }

    @After
    fun close() {
        db.close()
    }

    @Test
    fun getFavourites_insertFavourite_retrievedItemsEqualInserted() = runBlockingTest {
        //Given -> Creating 4 objects og Favourite class
        val data = Favourite("Aswan", 0.0, 0.0)
        val data1 =  Favourite("Greece", 0.0, 0.0)
        val data2 =  Favourite("Hurghada", 0.0, 0.0)
        val data3 =  Favourite("Paris", 0.0, 0.0)

       //Inserting in database
        dao.insertFavourite(data)
        dao.insertFavourite(data1)
        dao.insertFavourite(data2)
        dao.insertFavourite(data3)

        //When -> calling getFavourites
        val result = dao.getFavourites().first()

        //Then -> Returned list of favourites is equal the data inserted
        assertThat(result.size, CoreMatchers.`is`(4))
    }

   @Test
    fun insertFavourite_insertSingleItem_notNull() = runBlockingTest {

       //Given -> Creating an objects of Favourite class
       val data = Favourite("Italy", 0.0, 0.0)

       //When -> calling insert method to insert object in database
       dao.insertFavourite(data)
       //calling getFavourites to retrieve data from database
        val result = dao.getFavourites().first()

       //Then -> Returned list of favourites size is equal to the number of inserted objects
        MatcherAssert.assertThat(result[0], IsNull.notNullValue())
    }

    @Test
    fun deleteFavourites_insertItem_emptyList() = runBlockingTest {

        //Given -> Creating an objects of Favourite class
        val data = Favourite("Cairo", 0.0, 0.0)
        //Insert to database
        dao.insertFavourite(data)

        //When -> deleting data
        dao.deleteFavourites(data)
        //calling getFavourites to retrieve data from database
        val result = dao.getFavourites().first()

        //Then -> retrieved list  equals zero
        assertThat(result, IsEmptyCollection.empty())
        assertThat(result.size, `is`(0))

    }
}