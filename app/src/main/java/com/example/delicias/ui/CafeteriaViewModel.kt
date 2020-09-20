package com.example.delicias.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.delicias.domain.Cafeteria
import com.example.delicias.domain.Menu
import kotlin.collections.ArrayList

class CafeteriaViewModel : ViewModel(){
    var cafeteriaLiveData: MutableLiveData<ArrayList<Cafeteria>> = MutableLiveData()
    var cafeteriaList: ArrayList<Cafeteria>
    var isfavorite: MutableLiveData<Boolean> = MutableLiveData()

    //todo 여기서 cafeteriaRepository.getAll() 해야함
    init {

        var menuList1: ArrayList<Menu> = ArrayList()
        menuList1.add(Menu("쇠고기새송이볶음", 4000))
        menuList1.add(Menu("고등어 구이", 4000))
        menuList1.add(Menu("감자탕", 4000))


        var menuList2: ArrayList<Menu> = ArrayList()
        menuList2.add(Menu("회덮밥", 4000))
        menuList2.add(Menu("너비아니구이", 4000))

        cafeteriaList = ArrayList()
        cafeteriaList.add(Cafeteria("학생회관 식당", menuList1))
        cafeteriaList.add(Cafeteria("농생대 3식당", menuList2))

        cafeteriaLiveData.value = cafeteriaList
    }

    fun onClickfavoriteButton(){
        isfavorite.value = !isfavorite.value!!
    }


}