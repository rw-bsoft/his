// @@ 一些公用的函数
$package("chis.script.util.helper");

chis.script.util.helper.Helper = {};
// @@ 计算月龄。
chis.script.util.helper.Helper.getAgeMonths = function(birthday, datum) {
	if (birthday > datum) {
		return;
	}
	var birMon = birthday.format("m");
	var datumMon = datum.format("m");
	var birDay = birthday.format("d");
	var datumDay = datum.format("d");
	var mon = datumMon - birMon;
	var lastDateOfMonth = datum.getLastDateOfMonth().format("d");
	if (datumDay < birDay) {
		if (!(datumDay == lastDateOfMonth)) {
			mon -= 1;
		}
	}
	var birYear = birthday.format("Y");
	var datumYear = datum.format("Y");
	if (birYear == datumYear) {
		return mon;
	}
	return 12 * (datumYear - birYear) + mon;
}
// @@ 取两个日期的间隔天数。
chis.script.util.helper.Helper.getPeriod = function(date1, date2) {
	if (date1 == null && date2 == null) {
		return 0;
	}
	if (date1 != null && date2 != null && date1 == date2) {
		return 0;
	}
	var begin = new Date();
	if (date1 != null) {
		begin = date1;
	}
	var end = new Date();
	if (date2 != null) {
		end = date2;
	}
	if (begin > end) {
		var temp = end;
		end = begin;
		begin = temp;
	}
	var beginY = begin.format("Y");
	var endY = end.format("Y");
	if (beginY == endY) {
		return end.getDayOfYear() - begin.getDayOfYear();
	}
	var years = endY - beginY;
	var mdy = new Date("12/31/" + beginY);
	var maxDays = mdy.getDayOfYear();
	var days = maxDays - begin.getDayOfYear();
	for (var i = 0; i < years - 1; i++) {
		beginY += 1;
		var d = new Date("12/31/" + beginY);
		var maxDays = d.getDayOfYear() + 1;
		days += maxDays;
	}
	days += end.getDayOfYear() + 1;
	return days;
}
// ** 获取某个日期的前一天
chis.script.util.helper.Helper.getOneDayBeforeDate = function(date) {
	var Yday = new Array(2);
	Yday[0] = new Array(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
	Yday[1] = new Array(0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
	var sYear = date.getFullYear();
	var smonth = date.getMonth() + 1;
	var sday = date.getDate();
	var yn = 0;
	if (sYear % 400 == 0 || sYear % 100 != 0 && sYear % 4 == 0) {
		yn = 1;
	}
	if ((smonth == 1) && (sday == 1)) {
		sYear = sYear - 1;
		smonth = 12;
		sday = 31;
	} else if (sday == 1) {
		sday = (yn == 1) ? Yday[1][smonth - 1] : Yday[0][smonth - 1]
		smonth = smonth - 1
	} else {
		sday = sday - 1;
	}
	return new Date(sYear, smonth - 1, sday);
}

// ** 获取某个日期的后一天
chis.script.util.helper.Helper.getOneDayAfterDate = function(date) {
	var Yday = new Array(2);
	Yday[0] = new Array(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
	Yday[1] = new Array(0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
	var sYear = date.getFullYear();
	var smonth = date.getMonth() + 1;
	var sday = date.getDate();
	var yn = 0;
	if (sYear % 400 == 0 || sYear % 100 != 0 && sYear % 4 == 0) {
		yn = 1;
	}
	var nextDay = sday + 1;
	var maxDay;
	if (yn == 1) {
		maxDay = Yday[1][smonth];
	} else {
		maxDay = Yday[0][smonth];
	}
	if (maxDay >= nextDay) {
		sday = sday + 1;
	} else {
		sday = 1;
		smonth = smonth + 1;
	}
	return new Date(sYear, smonth - 1, sday);
}

chis.script.util.helper.Helper.getAgeBetween = function(date1, date2) {
	var ageStr = this.getPreciseAge(date1, date2);
	var year = ageStr.substring(0, ageStr.indexOf("_"));
	var month = ageStr.substring(ageStr.indexOf("_") + 1, ageStr
					.lastIndexOf("_"));
	var day = ageStr.substring(ageStr.lastIndexOf("_") + 1, ageStr.length);
	return year + "岁" + month + "月" + day + "天";
}

chis.script.util.helper.Helper.getAgeYear = function(date1, date2) {
	var ageStr = this.getPreciseAge(date1, date2);
	return ageStr.substring(0, ageStr.indexOf("_"));
}

chis.script.util.helper.Helper.getPreciseAge = function(date1, date2) {
	if (date1 == null) {
		return;
	}
	if (date2 == null) {
		date2 = new Date();
	}
	if (date1 > date2) {
		return;
	}
	var btwY = 0;
	var btwM = 0;
	var btwD = 0;
	var date1Day = date1.format("d");
	var date2Day = date2.format("d");
	btwD = date2Day - date1Day;
	if (btwD < 0) {
		btwM = -1;
		date2.setMonth(date2.getMonth() - 1);
		btwD = date2.getDaysInMonth() + btwD;
	}
	var date1Mon = date1.format("m");
	var date2Mon = date2.format("m");
	btwM = date2Mon - date1Mon;
	if (btwM < 0) {
		date2.setYear(date2.getFullYear() - 1);
		btwM = btwM + 12;
	}
	var date1Year = date1.format("Y");
	var date2Year = date2.format("Y");
	btwY = date2Year - date1Year;
	return btwY + "_" + btwM + "_" + btwD;
}

chis.script.util.helper.Helper.getUrl = function() {
	var protocol = location.protocol;
	var host = location.host;
	var pathname = location.pathname;
	return protocol + "//" + host
			+ pathname.substring(0, pathname.substr(1).indexOf('/') + 2);
}
/**
 * 解析URL
 * @param {} url
 * @return {}
 */
chis.script.util.helper.Helper.parseUrl = function(url) {
	var r = {
	    protocol: /([^\/]+:)\/\/(.*)/i,
	    host: /(^[^\:\/]+)((?:\/|:|$)?.*)/,
	    port: /\:?([^\/]*)(\/?.*)/,
	    pathname: /([^\?#]+)(\??[^#]*)(#?.*)/
	};
    var tmp, res = {};
    res["href"] = url;
    for (p in r) {
        tmp = r[p].exec(url);
        res[p] = tmp[1];
        url = tmp[2];
        if (url === "") {
            url = "/";
        }
        if (p === "pathname") {
            res["pathname"] = tmp[1];
            res["search"] = tmp[2];
            res["hash"] = tmp[3];
        }
    }
    //console.log(url);
    return res;
}
/**
 * 纯数字判定
 */
chis.script.util.helper.Helper.isNum = function(s){
	var regu = /^[0-9]+$/;
	if (regu.test(s)) {
		return true;
	}
	return false;
}
/**
 * 特殊字符判定
 */
chis.script.util.helper.Helper.isExistsSpecialCharacters = function(s){
	var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im,
	regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
	if (regEn.test(s) || regCn.test(s)) {
		return true;
	}
	return false;
}
/**
 * 身份证号判定
 */
chis.script.util.helper.Helper.validateIdcard = function(s){
	if(s=="00000000000000000X"){
		return true;
	}
	var regu = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
	if (regu.test(s) && chis.script.util.helper.Helper.checkProv(s.substr(0,2))){
		return true;
	}
	return false;
}
chis.script.util.helper.Helper.checkProv = function (s) {
    var provs = {11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北 ",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门"};
    if(provs[s]) {
        return true;
    }
    return false;
}
