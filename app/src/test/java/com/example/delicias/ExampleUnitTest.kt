package com.example.delicias

import com.example.delicias.domain.Restaurant
import com.example.delicias.domain.Date
import com.example.delicias.util.Util
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getDates(){
        var dateList = ArrayList<Date>()
        var calender = Calendar.getInstance(Locale.KOREA)

        for (i in 0..6) {
            var temp =  if (i == 0) 0 else 1

            calender.add(Calendar.DATE,  temp)

        }

        for (date in dateList){
            println(date.date.toString() + date.dayOfWeek + date.isToday)
        }
    }

    private fun getKorDayOfWeek(dayOfWeek: Int): String{
        var korDayOfWeek: String = ""

        when(dayOfWeek){
            1 -> korDayOfWeek = "일"
            2 -> korDayOfWeek = "월"
            3 -> korDayOfWeek = "화"
            4 -> korDayOfWeek = "수"
            5 -> korDayOfWeek = "목"
            6 -> korDayOfWeek = "금"
            7 -> korDayOfWeek = "토"
        }
        return korDayOfWeek
    }

    @Test
    fun jsonTest(){
        var cafeteriaJson = JSONObject()
            .put("id", "1")
            .put("name", "학생회관 식당")
            .put("place", "19동")
            .put("contact", "021234567")

        var breakfastJSON = JSONObject()
        breakfastJSON.put("message", "breakfast")
        var breakfastMenuJSONArray = JSONArray()
        breakfastMenuJSONArray.put(JSONObject().put("name","치킨텐더").put("price", "3000"))
            .put(JSONObject().put("name","연두부비빔밥").put("price", "2500"))
        breakfastJSON.put("menus", breakfastMenuJSONArray)
        breakfastJSON.put("isValid", true)
        cafeteriaJson.put("breakfast", breakfastJSON)

        var lunchJSON = JSONObject()
        lunchJSON.put("message", "lunch")
        var lunchMenuJSONArray = JSONArray()
        lunchMenuJSONArray.put(JSONObject().put("name","꽁치조림").put("price", "4000"))
            .put(JSONObject().put("name","제육볶음").put("price", "4000"))
        lunchJSON.put("menus", lunchMenuJSONArray)
        lunchJSON.put("isValid", true)
        cafeteriaJson.put("lunch", lunchJSON)

        var dinnerJSON = JSONObject()
        dinnerJSON.put("message", "dinner")
        var dinnerMenuJSONArray = JSONArray()
        dinnerMenuJSONArray.put(JSONObject().put("name","비빔국수").put("price", "4000"))
            .put(JSONObject().put("name","쇠고기새송이볶음").put("price", "4000"))
        dinnerJSON.put("menus", dinnerMenuJSONArray)
        dinnerJSON.put("isValid", true)
        cafeteriaJson.put("dinner", dinnerJSON)
        cafeteriaJson.put("latitude", 3)
        cafeteriaJson.put("longitude", 3)
        cafeteriaJson.put("breakfastTime", "7:30-9:30")
        cafeteriaJson.put("lunchTime", "11:30-13:30")
        cafeteriaJson.put("dinnerTime", "17:30-19:30")
        cafeteriaJson.put("date", "20201017")

        println(cafeteriaJson.toString(4))

        val restaurant = Gson().fromJson<Restaurant>(cafeteriaJson.toString(), object : TypeToken<Restaurant>(){}.type)

        println(restaurant)
    }

    @Test
    fun httpTest(){
        val response = URL("https://ce63f316-f695-4c2e-9e69-2c85ef983182.mock.pstmn.io/cafeteria").readText()
        println(response)
        val restaurantList = Gson().fromJson<List<Restaurant>>(response, object : TypeToken<List<Restaurant>>(){}.type)
        println(restaurantList)
    }

    @Test
    fun encodeTest(){
        println(URLEncoder.encode("동원낙성대아파트", "UTF-8") + " " + "%EB%8F%99%EC%9B%90%EB%82%99%EC%84%B1%EB%8C%80%EC%95%84%ED%8C%8C%ED%8A%B8".equals(URLEncoder.encode("동원낙성대아파트", "UTF-8")))
    }

    @Test
    fun koreanSortingTest(){
        var list = arrayListOf("관악공이", "농생대 식당", "아워홈" ,"학생회관", "낙성대", "나" , "낢")
        Collections.sort(list, kotlin.Comparator { left, right ->
            if (left.first() > right.first())
                return@Comparator 1
            else if (left.first() < right.first())
                return@Comparator -1
            else return@Comparator 0
        })

        println(list)
    }
}
