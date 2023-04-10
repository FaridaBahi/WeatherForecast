package com.example.myweather.favourites.viewmodel

import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweather.MainDispatcherRule
import com.example.myweather.database.roomState.FavRoomState
import com.example.myweather.model.FakeRepository
import com.example.myweather.model.Favourite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class FavViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var repo: FakeRepository
    lateinit var viewModel: FavViewModel

    private var favList: MutableList<Favourite> = mutableListOf(
        Favourite("Egypt",2.6, 5.3),
        Favourite("Paris", 8.3, 9.7),
        Favourite("Greece", 7.4, 6.6)
    )

   @get:Rule
    val mainDispatcherRule= MainDispatcherRule()

    @Before
    fun setUp() {
        repo= FakeRepository()
        viewModel= FavViewModel(repo)
    }

    @Test
    fun getLocalSavedLocation_List_returnFavList() = runBlockingTest{
        //Given -> list of favourite
        repo.favData= favList

        //When -> calling getLocalSavedLocation and saving returned list in FavRoomState
        viewModel.getLocaleSavedLocation()
        var favListResult: List<Favourite> = listOf()

       /* when (val result = viewModel.favStateFlow.first()) {
            is FavRoomState.Success -> {
                favListResult = result.data
            }
            else -> {}
        }*/
        viewModel.favStateFlow.collectLatest{
            when(it){
                is FavRoomState.Success -> {
                    favListResult= it.data
                }
                else -> {}
            }
        }

        //Then -> favListResult is equal to the given list
        assertThat(favListResult.size, `is`(3))
    }

    @Test
    fun delete_deleteFav() =  runBlockingTest{

        //give
        repo.favData = favList
        viewModel.delete(favList[2])

        //When
        viewModel.getLocaleSavedLocation()
        var favListResult: List<Favourite> = listOf()

        when (val result = viewModel.favStateFlow.first()) {
            is FavRoomState.Success -> {
                favListResult = result.data
            }
            else -> {}
        }

        //Then
        MatcherAssert.assertThat(favListResult.size, CoreMatchers.`is`(2))
    }
}