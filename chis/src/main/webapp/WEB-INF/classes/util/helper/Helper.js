// @@ һЩ���õĺ���
$package("util.helper");

util.helper.Helper = {};
// @@ �������䡣
util.helper.Helper.getAgeMonths = function(birthday, datum) {
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
// @@ ȡ�������ڵļ������
util.helper.Helper.getPeriod = function(date1, date2) {
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

util.helper.Helper.getUrl = function() {
	var protocol = location.protocol;
	var host = location.host;
	var pathname = location.pathname;
	return protocol + "//" + host
			+ pathname.substring(0, pathname.substr(1).indexOf('/') + 2);
	}
