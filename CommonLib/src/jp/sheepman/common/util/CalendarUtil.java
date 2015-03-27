package jp.sheepman.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.SparseIntArray;

public class CalendarUtil {
	private final static String SEPARATER = "-";
	
	
	//文字列⇒Calendar変換
	public static Calendar str2cal(String str){
		Calendar cal = Calendar.getInstance(Locale.JAPAN);
		cal.set(Calendar.YEAR, Integer.parseInt(str.split(SEPARATER)[0]));
		cal.set(Calendar.MONTH, Integer.parseInt(str.split(SEPARATER)[1])-1);
		cal.set(Calendar.DATE, Integer.parseInt(str.split(SEPARATER)[2]));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal;
	}

	//Calendar⇒文字列変換
	public static String cal2str(Calendar cal){
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%1$04d", getYear(cal)))
			.append(SEPARATER)
			.append(String.format("%1$02d", getMonth(cal)))
			.append(SEPARATER)
			.append(String.format("%1$02d", getDate(cal)));
		return sb.toString();
	}
	
	//Date⇒Calendar変換
	public static Calendar date2cal(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	//Calendarから年を取得
	public static int getYear(Calendar cal){
		return cal.get(Calendar.YEAR);
		
	}
	
	//Calendarから月を取得
	public static int getMonth(Calendar cal){
		return (cal.get(Calendar.MONTH) + 1);
		
	}
	//Calendarから日を取得
	public static int getDate(Calendar cal){
		return cal.get(Calendar.DATE);
		
	}
	
	/**
	 * 2つの日付間の差分の絶対値を返す
	 * @param start
	 * @param end
	 * @param type	CalendarクラスのYEAR、MONTH、DATEのいずれか
	 * @return
	 */
	public static int getDiffAbs(Calendar start, Calendar end, int type){
		SparseIntArray sia = new SparseIntArray();
		final int[] types = {Calendar.YEAR, Calendar.MONTH, Calendar.DATE};

		int flg = 1;
		int count = 0;
		
		Calendar tmp = (Calendar)start.clone();
		
		//未来日付の場合符号を逆転する
		if(tmp.after(end)){
			flg *= -1;
		}
		//年、月、日別にカウントする
		for(int t : types){
			//フィールドに1加算できるかチェックし、できた場合にはカウントを進める
			tmp.add(t, flg);
			count = 0;
			while((flg * tmp.compareTo(end)) <= 0){
				tmp.add(t, flg);
				count ++;
			}
			//フィールド別に結果Mapにセット
			sia.put(t, count);
			//進めていた数値を戻す
			tmp.add(t, (-1) * flg);
		}
		//指定したフィールドの数値を返却
		return sia.get(type);
	}
}
