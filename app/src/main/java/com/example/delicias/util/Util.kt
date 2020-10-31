package com.example.delicias.util

import com.example.delicias.domain.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

class Util {
    companion object{
        fun getDates(): Flow<ArrayList<Date>> = flow {
            var dateList = ArrayList<Date>()
            var calender = Calendar.getInstance(Locale.KOREA)

            for (i in 0..6) {
                var temp =  if (i == 0) 0 else 1

                calender.add(Calendar.DATE,  temp)

                dateList.add(
                    Date(getKorDayOfWeek(calender.get(Calendar.DAY_OF_WEEK)),
                        getDateString(calender.get(Calendar.DATE)), i == 0)
                )
            }

            emit(dateList)
        }


        private fun getKorDayOfWeek(dayOfWeek: Int): String{
            var korDayOfWeek = ""

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

        private fun getDateString(date: Int): String{
            var dateString = date.toString()

            if (dateString.length == 1){
                dateString = "0$dateString"
            }

            return dateString
        }
    }
}