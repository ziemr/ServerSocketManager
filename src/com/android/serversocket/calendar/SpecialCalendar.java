package com.android.serversocket.calendar;

import java.util.Calendar;

public class SpecialCalendar {
	 private int daysOfMonth = 0;      //�^���I�V��
	 private int dayOfWeek = 0;        //��̖^��V�������{




	 // ���f���۔N
	 public boolean isLeapYear(int year) {
	  if (year % 100 == 0 && year % 400 == 0) {
	   return true;
	  } else if (year % 100 != 0 && year % 4 == 0) {
	   return true;
	  }
	  return false;
	 }

	 //�����^���L�����V��
	 public int getDaysOfMonth(boolean isLeapyear, int month) {
	  switch (month) {
	  case 1:
	  case 3:
	  case 5:
	  case 7:
	  case 8:
	  case 10:
	  case 12:
	   daysOfMonth = 31;
	   break;
	  case 4:
	  case 6:
	  case 9:
	  case 11:
	   daysOfMonth = 30;
	   break;
	  case 2:
	   if (isLeapyear) {
	    daysOfMonth = 29;
	   } else {
	    daysOfMonth = 28;
	   }

	  }
	  return daysOfMonth;
	 }

	 //�w��^�N���I�^���I���V�������{
	 public int getWeekdayOfMonth(int year, int month){
	  Calendar cal = Calendar.getInstance();
	  cal.set(year, month-1, 1);
	  dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
	  return dayOfWeek;
	 }


}
