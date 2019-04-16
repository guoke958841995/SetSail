package com.sxhalo.PullCoal.utils;


import com.sxhalo.PullCoal.common.base.Constant;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by amoldZhang on 2016/11/3.
 */
public class DateUtil {

    /**
     * 获取当前日期，以type格式返回字符串
     * @param type
     * @return
     */
    public static String getNewTimeType(String type){
        SimpleDateFormat sd = new SimpleDateFormat(type);
        return sd.format(new Date());
    }

    /**
     * 获取当前日期，返回时间戳
     * @return
     */
    public static long getNewTimeLong(){
            Date date = new Date();
            long lre_time = date.getTime();
        return lre_time;
    }
    /**
     * 将日期字符串转为日期类型
     *
     * @param date
     * @param format
     * @return
     */
    public static Date getString2Date(String date, String format) {
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 将日期转为字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDate2FormatString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取两个日期之间相差的时间
     *
     * @param start
     * @param end
     * @param type
     * @return 指定类型的时间间隔
     */
    public static int getBetweenTimes(Date start, Date end, String type) {
        long times = end.getTime() - start.getTime();
        if (type.equals(Constant.TIME_BETWEEN_DAY))
            return (int) times / (24 * 60 * 60 * 1000);
        if (type.equals(Constant.TIME_BETWEEN_HOUR))
            return (int) times / (60 * 60 * 10000);
        if (type.equals(Constant.TIME_BETWEEN_MINUTE))
            return (int) times / (60 * 10000);
        if (type.equals(Constant.TIME_BETWEEN_SECONDS))
            return (int) times / 1000;
        return 0;
    }

    // 将字符串转为时间戳
    public static String getTime(String user_time,String type) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        }catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return re_time;
    }
    // 将时间戳转为字符串
    public static String getStrTime(String cc_time,String type) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        // 例如：
//        cc_time=1291778220 ;
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /**
     * Date（long） 转换 String
     *
     * @param time
     * @param format
     * @return
     */
    public static String date2String(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String s = sdf.format(time);
        return s;
    }



    /**
     * 通过时间格式
     * @param pushTime
     * @return
     */
    public static String getAM_PM(String pushTime){
        String time = "";
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(new Long(pushTime));
        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        int apm = mCalendar.get(Calendar.AM_PM);
        // apm=0 表示上午，apm=1表示下午。
        if (apm == 0){
            time = "上午 " + hour+":"+minute;
        }else{
            time = "下午 " + hour+":"+minute;
        }
        return time;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate,String TYPE) {
        SimpleDateFormat formatter = new SimpleDateFormat(TYPE);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**  * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss  *   * @param dateDate  * @return  */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将字符串转换成 yyyy-MM-dd 的短时间格式
     * @param nowdate
     * @return
     */
    public static String strToStr(String nowdate){
        Date strtodate = strToDateLong(nowdate);
        return dateToStr(strtodate);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将短时间格式字符串 yyyy-MM-dd HH:mm:ss 转换成想要的时间格式 type
     *
     * @param strDate
     * @return
     */
    public static String strToStrMYType(String strDate,String type) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return getDate2FormatString(strtodate,type);
    }

    /**
     * 将短时间格式字符串 yyyy-MM-dd 转换成想要的时间格式 type
     *
     * @param strDate
     * @return
     */
    public static String strToStrType(String strDate,String type) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return getDate2FormatString(strtodate,type);
    }



    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     */
    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,
     * nowdate为时间,
     * delay为前移或后延的天数
     */
    public static String getNextDay(String nowdate, String delay) {
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        }catch(Exception e){
            return "";
        }
    }

    /**
     * 获取两个日期间隔的时间
     * @param start
     * @param end
     * @return
     */
    public static int[] getBetweenTimesDetail(Date start, Date end) {
        int[] result = new int[4];

        long offset = end.getTime() - start.getTime();
        result[0] = (int) (offset / (24 * 60 * 60 * 1000)); //天
        offset = offset - result[0] * 24 * 60 * 60 * 1000;
        result[1] = (int) (offset / (60 * 60 * 1000)); //时
        offset = offset - result[1] * 60 * 60 * 1000;
        result[2] = (int) (offset / (60 * 1000));//分
        offset = offset - result[2] * 60 * 1000;
        result[3] = (int) (offset / 1000); //秒

        return result;
    }

    /**
     *  判断一个时间是否为当天时间
     * @param strDate0 需要判断的时间0
     * @param strDate1 需要判断的时间1
     * @return true 是同一天 false 不在同一天
     */
    public static boolean getDayOfTheDay(String strDate0,String strDate1,String TYPE) {
        boolean flage = false;
        try {
            //将需要判断的时间转换成Date格式
            Date currentDate = getString2Date(strDate0, TYPE);
            Date strtodate = getString2Date(strDate1, TYPE);

            SimpleDateFormat sft = new SimpleDateFormat("yyyyMMdd");// 日期格式化
            if ((sft.format(currentDate)).equals((sft.format(strtodate)))){
                flage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flage;
    }

    /**
     * 获取时间差
     *
     * @return 时间差
     * @startTime 开始时间
     * @endTime 结束时间
     * @format 时间的格式
     * @str 时差的返回类型
     */
    public static Long dateDiff(String startTime, String endTime,
                                String format, String str) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh + day * 24;// 计算差多少小时
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
            sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            // System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时"
            // + (min - day * 24 * 60) + "分钟" + sec + "秒。");
            // System.out.println("hour=" + hour + ",min=" + min);
            if (str.equalsIgnoreCase("h")) {
                return hour;
            } else if (str.equalsIgnoreCase("s")) {
                return sec;
            } else if (str.equalsIgnoreCase("d")) {
                return day;
            } else if (str.equalsIgnoreCase("m")) {
                return min;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (str.equalsIgnoreCase("h")) {
            return hour;
        } else if (str.equalsIgnoreCase("s")) {
            return sec;
        } else if (str.equalsIgnoreCase("d")) {
            return day;
        } else {
            return min;
        }
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time    需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *                <p/>如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String time, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("yyyy-MM-dd");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("MM-dd").format(tDate);
                    } else {

                        if (dTime < tMin) {
                            display = "刚刚";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "分钟前";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "小时前";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天 " + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return display;
    }

    /***
     * 获取当前日期距离过期时间的日期差值
     * @param endTime
     * @return
     */
    public static String dateDiff(String endTime) {
        String strTime = null;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sd.format(curDate);
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime()
                    - sd.parse(str).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (day >= 1) {
                strTime = day + "天" + hour + "时"+ min + "分";
            } else {

                if (hour >= 1) {
                    strTime = day + "天" + hour + "时" + min + "分";

                } else {
                    if (sec >= 1) {
                        strTime = day + "天" + hour + "时" + min + "分" + sec + "秒";
                    } else if (sec < 0){
                        strTime = "已过期";
                    }else{
                        strTime = "即将到期";
                    }
                }
            }

            return strTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
