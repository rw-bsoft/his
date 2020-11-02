package chis.source.util;

import java.util.Date;

/**
 * @Auther: Fengld
 * @Date: 2018/9/16 09:57
 * @Description:g根据当前更新时间回写年度
 */
public class YearsUtil {
    /**
     * 年度开始月份
     */
    private static int startMon = 9;
    /**
     * 年度结束月份
     */
    private static int endMon = 8;
    /**
     * 年度获取算法
     */
    private static int yearsType = 1;
    /**
     * 传入empiId的情况
     * @return sql With empiId
     */
    public String setYearsWithEmpiId(){
        StringBuffer sql = new StringBuffer();
        Date today = new Date();
        int years = yearsType ==1 ?getYearsWithStart(today):getYearsWithMore(today);
        sql.append("update EHR_HEALTHRECORD set years = ").append(years).append(" where empiId =:empiId ");

        return sql.toString();
    }

    /**
     *传入 phrId的情况
     * @return sqlWith phrId
     */
    public String setYearsWithPhrid(){
        StringBuffer sql = new StringBuffer();
        Date today = new Date();
        int years = yearsType ==1 ?getYearsWithStart(today):getYearsWithMore(today);
        sql.append("update EHR_HEALTHRECORD set years = ").append(years).append(" where phrId =:phrId");

        return sql.toString();
    }

    /**
     * 根据年度开始时间计算年度 ，例如年度开始日期为9月，今日为2018年9月，
     * 则今日是2018年度
     *
     * @param date
     * @return years
     */
    private int getYearsWithStart(Date date) {
        int years;
        if (date.getMonth() - startMon > 0) {
            years = date.getYear() - 1;
        } else {
            years = date.getYear();
        }
        return years + 1900;
    }

    /**
     * 根据年度结束时间计算年度，例如年度结束时间为8月，今日为2018年8月，
     * 则今日是2018年度
     *
     * @param date
     * @return years
     */
    private int getYearsWithEnd(Date date) {
        int years;
        if (date.getMonth() - endMon < 0) {
            years = date.getYear();
        } else {
            years = date.getYear() + 1;
        }
        return years + 1900;
    }

    /**
     * 根据年度中大部分时间落在当前还是下一年度计算
     *
     * @param date
     * @return years
     */
    private int getYearsWithMore(Date date) {
        int years;
        if (startMon < endMon) {
            //只有一种情况，从1月到12月
            years = date.getYear();
        } else if (startMon - 7 < 0) {
            //则大部分时间落在本年度
            if (date.getMonth() + 1 > startMon) {
                years = date.getYear();
            } else {
                years = date.getYear() - 1;
            }
        } else {
            //大部分时间落在下一年度
            if (date.getMonth() + 1 - startMon >= 0) {
                years = date.getYear() + 1;
            } else {
                years = date.getYear();
            }
        }

        return years + 1900;
    }
}

