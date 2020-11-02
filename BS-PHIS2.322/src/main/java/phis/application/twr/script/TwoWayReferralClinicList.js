$package("phis.application.twr.script")

$import("phis.script.SimpleList", "phis.script.util.DateUtil");

phis.application.twr.script.TwoWayReferralClinicList = function(cfg) {
	// this.exContext = {};
	phis.application.twr.script.TwoWayReferralClinicList.superclass.constructor
			.apply(this, [cfg]);
	var yyrqandsjvalue = '';
	var ysdmvalue = '';
	var ysmcvalue = '';
}

Ext.extend(phis.application.twr.script.TwoWayReferralClinicList,
		phis.script.SimpleList, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							html : this.getFormHtmlTemplate(),
							autoScroll : true
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this);
				return panel;
			},
			getFormHtmlTemplate : function() {
				return '<html>'
						+ '<head>'
						+ '<style type="text/css">'
						+ '#apDiv1 {'
						+ 'position:absolute;'
						+ 'width:409px;'
						+ 'height:28px;'
						+ 'z-index:1;'
						+ 'left: 12px;'
						+ '}'
						+ '#apDiv2 {'
						+ 'position:absolute;'
						+ 'width:410px;'
						+ 'height:29px;'
						+ 'z-index:2;'
						+ 'left: 10px;'
						+ 'top: 27px;'
						+ '}'
						+ '#apDiv3 {'
						+ 'position:absolute;'
						+ 'width:584px;'
						+ 'height:57px;'
						+ 'z-index:3;'
						+ 'left: 421px;'
						+ '}'
						+ '#apDiv4 {'
						+ 'position:absolute;'
						+ 'width:92px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 5px;'
						+ 'top: 3px;'
						+ '}'
						+ '#apDiv5 {'
						+ 'position:absolute;'
						+ 'width:92px;'
						+ 'height:25px;'
						+ 'z-index:1;'
						+ 'left: 5px;'
						+ 'top: 29px;'
						+ '}'
						+ '#apDiv6 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 99px;'
						+ 'top: 3px;'
						+ '}'
						+ '#apDiv7 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 99px;'
						+ 'top: 31px;'
						+ '}'
						+ '#apDiv8 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 168px;'
						+ 'top: 4px;'
						+ '}'
						+ '#apDiv9 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 168px;'
						+ 'top: 31px;'
						+ '}'
						+ '#apDiv10 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 241px;'
						+ 'top: 4px;'
						+ '}'
						+ '#apDiv11 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 241px;'
						+ 'top: 30px;'
						+ '}'
						+ '#apDiv12 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 306px;'
						+ 'top: 4px;'
						+ '}'
						+ '#apDiv13 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 306px;'
						+ 'top: 31px;'
						+ '}'
						+ '#apDiv14 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 380px;'
						+ 'top: 4px;'
						+ '}'
						+ '#apDiv15 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 380px;'
						+ 'top: 32px;'
						+ '}'
						+ '#apDiv16 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 450px;'
						+ 'top: 4px;'
						+ '}'
						+ '#apDiv17 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 450px;'
						+ 'top: 32px;'
						+ '}'
						+ '#apDiv18 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 515px;'
						+ 'top: 5px;'
						+ '}'
						+ '#apDiv19 {'
						+ 'position:absolute;'
						+ 'width:65px;'
						+ 'height:25px;'
						+ 'z-index:4;'
						+ 'left: 515px;'
						+ 'top: 32px;'
						+ '}'
						+ '#apDiv20 {'
						+ 'position:absolute;'
						+ 'width:99px;'
						+ 'height:210px;'
						+ 'z-index:4;'
						+ 'left: 10px;'
						+ 'top: 53px;'
						+ '}'
						+ '#apDiv21 {'
						+ 'position:absolute;'
						+ 'width:306px;'
						+ 'height:210px;'
						+ 'z-index:5;'
						+ 'left: 113px;'
						+ 'top: 75px;'
						+ '}'
						+ '#apDiv22 {'
						+ 'position:absolute;'
						+ 'width:766px;'
						+ 'height:210px;'
						+ 'z-index:6;'
						+ 'left: 420px;'
						+ 'top: 75px;'
						+ '}'
						+ '</style>'
						+ '</head>'
						+ '<body>'
						+ '<div id="apDiv1">'
						+ '<table width="405">'
						+ '<tr>'
						+ '<td width="51">机构：</td>'
						+ '<td width="224"><select name="jg" id="jg" style="width:180px">'
						+ '</select>'
						+ '科室：</td>'
						+ '<td width="108"><select name="ks" id="ks" style="width:108px">'
						+ '</select></td>'
						+ '</tr>'
						+ '</table>'
						+ '</div>'
						+ '<div id="apDiv2">'
						+ '<table width="412" height="27" border="1">'
						+ '<tr>'
						+ '<td width="92"><div align="center">医生</div></td>'
						+ '<td width="301"><div align="center">医生特长</div></td>'
						+ '</tr>'
						+ '</table>'
						+ '</div>'
						+ '<div id="apDiv3">'
						+ '<table width="583" border="1" id=notab>'
						+ '<tr>'
						+ '<td width="91" height="52"><div id="apDiv4">'
						+ '<div align="center">'
						+ '<select name="swxw" id="swxw" style="width:50px;">'
						+ '<option value="1">上午</option>'
						+ '<option value="2">下午</option>'
						+ '</select>'
						+ '</div>'
						+ '</div>'
						+ '<div id="apDiv5">'
						+ '<div align="center">'
						+ '<input type="button" name="sz" id="sz" value="上周" />'
						+ '<input type="button" name="xz" id="xz" value="下周" />'
						+ '</div>' + '</div></td>'
						+ '<td width="65"><div id="apDiv6" align="center">'
						+ '</div>' + '<div id="apDiv7" align="center">'
						+ '</div><div id="ycrq1" style="display:none;"></td>'
						+ '<td width="65"><div id="apDiv8" align="center">'
						+ '</div>' + '<div id="apDiv9" align="center">'
						+ '</div><div id="ycrq2" style="display:none;"></td>'
						+ '<td width="65"><div id="apDiv10" align="center">'
						+ '</div>' + '<div id="apDiv11" align="center">'
						+ '</div><div id="ycrq3" style="display:none;"></td>'
						+ '<td width="65"><div id="apDiv12" align="center">'
						+ '</div>' + '<div id="apDiv13" align="center">'
						+ '</div><div id="ycrq4" style="display:none;"></td>'
						+ '<td width="65"><div id="apDiv14" align="center">'
						+ '</div>' + '<div id="apDiv15" align="center">'
						+ '</div><div id="ycrq5" style="display:none;"></td>'
						+ '<td width="65"><div id="apDiv16" align="center">'
						+ '</div>' + '<div id="apDiv17" align="center">'
						+ '</div><div id="ycrq6" style="display:none;"></td>'
						+ '<td width="65"><div id="apDiv18" align="center">'
						+ '</div>' + '<div id="apDiv19" align="center">'
						+ '</div><div id="ycrq7" style="display:none;"></td>'
						+ '</tr></table>' + '</div>' + '<div id="apDiv20">'
						+ '<table width="98" border="1" id="ys">' + '</table>'
						+ '</div>' + '<div id="apDiv21" align="left"></div>'
						+ '</body>' + '</html>';
			},
			doNew : function() {
				document.getElementById("jg").selectedIndex = -1;
				document.getElementById("ks").selectedIndex = -1;
				var tabRows = document.getElementById("ys").rows.length;
				for (var i = 0; i < tabRows; i++) {
					document.getElementById("ys").deleteRow(i);
					tabRows = tabRows - 1;
					i = i - 1;
				}
				document.getElementById("apDiv21").innerHTML = "";
				var tabinfoRows = document.getElementById("notab").rows.length;
				for (var n = 1; n < tabinfoRows; n++) {
					document.getElementById("notab").deleteRow(n);
					tabinfoRows = tabinfoRows - 1;
					n = n - 1;
				}
			},
			onReady : function() {
				var this_ = this;
				var Hospital = phis.script.rmi.miniJsonRequestSync({
							serviceId : "referralService",
							serviceAction : "getClinicHospital",
							body : {
								"referralDate" : new Date()
							}
						});
				var hospitalinfo = Hospital.json.body;
				for (var i = 0; i < hospitalinfo.length; i++) {
					var hospitalCode = hospitalinfo[i].hospitalCode;
					var hospitalName = hospitalinfo[i].hospital;
					var optionhospital = new Option(hospitalName, hospitalCode);
					document.getElementById("jg").options.add(optionhospital);
					document.getElementById("jg").selectedIndex = -1;
				}
				Ext.getDom("jg").onchange = function() {
					var office = phis.script.rmi.miniJsonRequestSync({
						serviceId : "referralService",
						serviceAction : "getClinicDept",
						body : {
							"registerType" : "2",
							"referralDate" : new Date(),
							"registerSchedule" : document
									.getElementById("swxw").value,
							"registerCategory" : "1",
							"hospitalCode" : document.getElementById("jg").value
						}
					});
					var officeinfo = office.json.body;
					var length = document.getElementById("ks").options.length
					for (var j = 0; j < length; j++) {
						document.getElementById("ks").options.remove(j)
					}
					for (var i = 0; i < officeinfo.length; i++) {
						var officeCode = officeinfo[i].KESHIDM;
						var officeName = officeinfo[i].KESHIMC;
						var optionoffice = new Option(officeName, officeCode);
						document.getElementById("ks").options.add(optionoffice);
						document.getElementById("ks").selectedIndex = -1;
					}
				}
				document
				.getElementById("swxw").onchange = function() {
					this_.loadPaiBan();
				}
				Ext.getDom("ks").onchange = function() {
					var doctor = phis.script.rmi.miniJsonRequestSync({
						serviceId : "referralService",
						serviceAction : "getRegisterDoctor",
						body : {
							"registerType" : "2",
							"referralDate" : Date.getDateAfter(new Date(), 1),
							"registerSchedule" : document
									.getElementById("swxw").value,
							"registerCategory" : "1",
							"hospitalCode" : document.getElementById("jg").value,
							"departmentCode" : document.getElementById("ks").value
						}
					});
					var doctorinfo = doctor.json.body;
					var tabRows = document.getElementById("ys").rows.length;
					for (var i = 0; i < tabRows; i++) {
						document.getElementById("ys").deleteRow(i);
						tabRows = tabRows - 1;
						i = i - 1;
					}
					var ysmap = {};
					for (var i = 0; i < doctorinfo.length; i++) {
						var ystable = document.getElementById("ys");
						var ysRow = ystable.insertRow(i); // 添加行
						ysRow.id = doctorinfo[i].YISHENGDM;
						var ysCell = ysRow.insertCell(); // 添加列
						ysCell.align = "center";
						ysCell.id = doctorinfo[i].YISHENGDM;
						ysCell.innerHTML = doctorinfo[i].YISHENGXM; // 添加数据
						ysmap[doctorinfo[i].YISHENGDM] = doctorinfo[i].YISHENGXM;
						Ext.getDom(doctorinfo[i].YISHENGDM).onclick = function() {
							var trs = document.getElementById('ys')
									.getElementsByTagName('tr');
							for (var i = 0; i < trs.length; i++) {
								if (trs[i] == this) {
									trs[i].style.backgroundColor = '#DFEBF2';
									ysdmvalue = trs[i].id;
									ysmcvalue = ysmap[trs[i].id];
									document.getElementById("apDiv21").align = "center";
									document.getElementById("apDiv21").innerHTML = "优秀专家,多年临床经验";
									this_.doctorId = trs[i].id
									this_.loadPaiBan();
								} else {
									trs[i].style.backgroundColor = '';
								}
							}
						}
					}
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "referralService",
							serviceAction : "getDay"
						});
				var day = ret.json.body.day;
				var days = ret.json.body.days;
				var rqMap = ret.json.body.rqMap;
				this_.showWeek(day);
				document.getElementById("apDiv7").innerHTML = "(明天)";
				document.getElementById("apDiv9").innerHTML = "(后天)";
				document.getElementById("apDiv11").innerHTML = "(" + days.day3
						+ ")";
				document.getElementById("apDiv13").innerHTML = "(" + days.day4
						+ ")";
				document.getElementById("apDiv15").innerHTML = "(" + days.day5
						+ ")";
				document.getElementById("apDiv17").innerHTML = "(" + days.day6
						+ ")";
				document.getElementById("apDiv19").innerHTML = "(" + days.day7
						+ ")";
				document.getElementById("ycrq1").innerHTML = rqMap.rq1;
				document.getElementById("ycrq2").innerHTML = rqMap.rq2;
				document.getElementById("ycrq3").innerHTML = rqMap.rq3;
				document.getElementById("ycrq4").innerHTML = rqMap.rq4;
				document.getElementById("ycrq5").innerHTML = rqMap.rq5;
				document.getElementById("ycrq6").innerHTML = rqMap.rq6;
				document.getElementById("ycrq7").innerHTML = rqMap.rq7;
				Ext.getDom("sz").onclick = function() {
					this_.changeWeek("getBeforWeek")
				};
				Ext.getDom("xz").onclick = function() {
					this_.changeWeek("getNextWeek")
				};
			},
			loadPaiBan : function(){
if(!this.doctorId)
	return;
//				trs[i].style.backgroundColor = '#DFEBF2';
//				ysdmvalue = ysCell.id;
//				ysmcvalue = ysCell.innerHTML;
//				document.getElementById("apDiv21").align = "center";
//				document.getElementById("apDiv21").innerHTML = "优秀专家,多年临床经验";
//				this.doctorId = trs[i].id
				var no = phis.script.rmi
						.miniJsonRequestSync({
							serviceId : "referralService",
							serviceAction : "registerSourceHMD",
							body : {
								"registerType" : "2",
								"referralDate" : document
										.getElementById("ycrq1").innerHTML
										+ ","
										+ document
												.getElementById("ycrq2").innerHTML
										+ ","
										+ document
												.getElementById("ycrq3").innerHTML
										+ ","
										+ document
												.getElementById("ycrq4").innerHTML
										+ ","
										+ document
												.getElementById("ycrq5").innerHTML
										+ ","
										+ document
												.getElementById("ycrq6").innerHTML
										+ ","
										+ document
												.getElementById("ycrq7").innerHTML,
								"registerSchedule" : document
										.getElementById("swxw").value,
								"registerCategory" : "1",
								"hospitalCode" : document
										.getElementById("jg").value,
								"departmentCode" : document
										.getElementById("ks").value,
								"doctorId" : this.doctorId

							}
						});
				var noinfo = no.json.body;
				// console.debug(noinfo)
				var tabinfoRows = document
						.getElementById("notab").rows.length;
				for (var n = 1; n < tabinfoRows; n++) {
					document.getElementById("notab")
							.deleteRow(n);
					tabinfoRows = tabinfoRows - 1;
					n = n - 1;
				}
				var notable = document
						.getElementById("notab");
				for (var k = 0; k < noinfo.length; k++) {
					var noRow = notable.insertRow(k + 1); // 添加行
					var noCell1 = noRow.insertCell(); // 添加列
					var noCell2 = noRow.insertCell(); // 添加列
					var noCell3 = noRow.insertCell(); // 添加列
					var noCell4 = noRow.insertCell(); // 添加列
					var noCell5 = noRow.insertCell(); // 添加列
					var noCell6 = noRow.insertCell(); // 添加列
					var noCell7 = noRow.insertCell(); // 添加列
					var noCell8 = noRow.insertCell(); // 添加列
					noCell1.align = "center";
					noCell1.innerHTML = noinfo[k].JIUZHENSJ; // 添加数据
					if (noinfo[k].RIQI == document
							.getElementById("ycrq1").innerHTML
							|| noinfo[k][document
									.getElementById("ycrq1").innerHTML] == 1) {
						noCell2.align = "center";
						noCell2.innerHTML = "<button id=n"
								+ k
								+ '|'
								+ document
										.getElementById("ycrq1").innerHTML
								+ " value="
								+ noinfo[k].JIUZHENSJ
										.trim()
								+ "|"
								+ document
										.getElementById("ycrq1").innerHTML
								+ '|'
								+ noinfo[k].DANGTIANPBID
								+ '|'
								+ noinfo[k].YIZHOUPBID
								+ '|'
								+ noinfo[k].GUAHAOXH
								+ " class='Twbtn'>选择</button>"; // 添加数据
					}
					if (noinfo[k].RIQI == document
							.getElementById("ycrq2").innerHTML
							|| noinfo[k][document
									.getElementById("ycrq2").innerHTML] == 1) {
						// noCell3 = noRow.insertCell(); //
						// 添加列
						noCell3.align = "center";
						noCell3.innerHTML = "<button id=n"
								+ k
								+ '|'
								+ document
										.getElementById("ycrq2").innerHTML
								+ " value="
								+ noinfo[k].JIUZHENSJ
										.trim()
								+ "|"
								+ document
										.getElementById("ycrq2").innerHTML
								+ "|"
								+ noinfo[k].DANGTIANPBID
								+ '|'
								+ noinfo[k].YIZHOUPBID
								+ '|'
								+ noinfo[k].GUAHAOXH
								+ " class='Twbtn'>选择</button>"; // 添加数据
						// Ext.getDom("n" + k).onclick =
						// function() {
						// yyrqandsjvalue = this.value;
						// }
					}
					if (noinfo[k].RIQI == document
							.getElementById("ycrq3").innerHTML
							|| noinfo[k][document
									.getElementById("ycrq3").innerHTML] == 1) {
						// noCell4 = noRow.insertCell(); //
						// 添加列
						noCell4.align = "center";
						noCell4.innerHTML = "<button id=n"
								+ k
								+ '|'
								+ document
										.getElementById("ycrq3").innerHTML
								+ " value="
								+ noinfo[k].JIUZHENSJ
										.trim()
								+ "|"
								+ document
										.getElementById("ycrq3").innerHTML
								+ "|"
								+ noinfo[k].DANGTIANPBID
								+ '|'
								+ noinfo[k].YIZHOUPBID
								+ '|'
								+ noinfo[k].GUAHAOXH
								+ " class='Twbtn'>选择</button>"; // 添加数据
						// Ext.getDom("n" + k).onclick =
						// function() {
						// yyrqandsjvalue = this.value;
						// }
					}
					if (noinfo[k].RIQI == document
							.getElementById("ycrq4").innerHTML
							|| noinfo[k][document
									.getElementById("ycrq4").innerHTML] == 1) {
						// noCell5 = noRow.insertCell(); //
						// 添加列
						noCell5.align = "center";
						noCell5.innerHTML = "<button id=n"
								+ k
								+ '|'
								+ document
										.getElementById("ycrq4").innerHTML
								+ " value="
								+ noinfo[k].JIUZHENSJ
										.trim()
								+ "|"
								+ document
										.getElementById("ycrq4").innerHTML
								+ "|"
								+ noinfo[k].DANGTIANPBID
								+ '|'
								+ noinfo[k].YIZHOUPBID
								+ '|'
								+ noinfo[k].GUAHAOXH
								+ " class='Twbtn'>选择</button>"; // 添加数据
						// Ext.getDom("n" + k).onclick =
						// function() {
						// yyrqandsjvalue = this.value;
						// }
					}
					if (noinfo[k].RIQI == document
							.getElementById("ycrq5").innerHTML
							|| noinfo[k][document
									.getElementById("ycrq5").innerHTML] == 1) {
						// noCell6 = noRow.insertCell(); //
						// 添加列
						noCell6.align = "center";
						noCell6.innerHTML = "<button id=n"
								+ k
								+ '|'
								+ document
										.getElementById("ycrq5").innerHTML
								+ " value="
								+ noinfo[k].JIUZHENSJ
										.trim()
								+ "|"
								+ document
										.getElementById("ycrq5").innerHTML
								+ "|"
								+ noinfo[k].DANGTIANPBID
								+ '|'
								+ noinfo[k].YIZHOUPBID
								+ '|'
								+ noinfo[k].GUAHAOXH
								+ " class='Twbtn'>选择</button>"; // 添加数据
						// Ext.getDom("n" + k).onclick =
						// function() {
						// yyrqandsjvalue = this.value;
						// }
					}
					if (noinfo[k].RIQI == document
							.getElementById("ycrq6").innerHTML
							|| noinfo[k][document
									.getElementById("ycrq6").innerHTML] == 1) {
						// noCell7 = noRow.insertCell(); //
						// 添加列
						noCell7.align = "center";
						noCell7.innerHTML = "<button id=n"
								+ k
								+ '|'
								+ document
										.getElementById("ycrq6").innerHTML
								+ " value="
								+ noinfo[k].JIUZHENSJ
										.trim()
								+ "|"
								+ document
										.getElementById("ycrq6").innerHTML
								+ "|"
								+ noinfo[k].DANGTIANPBID
								+ '|'
								+ noinfo[k].YIZHOUPBID
								+ '|'
								+ noinfo[k].GUAHAOXH
								+ " class='Twbtn'>选择</button>"; // 添加数据
						// Ext.getDom("n" + k).onclick =
						// function() {
						// yyrqandsjvalue = this.value;
						// }
					}
					if (noinfo[k].RIQI == document
							.getElementById("ycrq7").innerHTML
							|| noinfo[k][document
									.getElementById("ycrq7").innerHTML] == 1) {
						// noCell8 = noRow.insertCell(); //
						// 添加列
						noCell8.align = "center";
						noCell8.innerHTML = "<button id=n"
								+ k
								+ '|'
								+ document
										.getElementById("ycrq7").innerHTML
								+ " value="
								+ noinfo[k].JIUZHENSJ
										.trim()
								+ "|"
								+ document
										.getElementById("ycrq7").innerHTML
								+ "|"
								+ noinfo[k].DANGTIANPBID
								+ '|'
								+ noinfo[k].YIZHOUPBID
								+ '|'
								+ noinfo[k].GUAHAOXH
								+ " class='Twbtn'>选择</button>"; // 添加数据
						// Ext.getDom("n" + k).onclick =
						// function() {
						// yyrqandsjvalue = this.value;
						// }
					}
					for (var i = 1; i < 8; i++) {
						var button = Ext
								.getDom("n"
										+ k
										+ '|'
										+ document
												.getElementById("ycrq"
														+ i).innerHTML.substring(0,10));
						if (button) {
							button.onclick = function() {
								yyrqandsjvalue = this.value;
								Ext
										.each(
												Ext
														.query('.Twbtn'),
												function(
														item) {
													item.innerHTML = "选择";
												})
								this.innerHTML = "已选择";
								var info = this.value
										.split('|');
								// MyMessageTip.msg("提示",
								// "已选择" + info[1]
								// + "号"
								// + info[0]
								// + "的号源",
								// true);

							}
						}

					}
				}
			},
			showWeek : function(day){
				if (day == 6) {
					document.getElementById("apDiv6").innerHTML = "星期六";
					document.getElementById("apDiv8").innerHTML = "星期日";
					document.getElementById("apDiv10").innerHTML = "星期一";
					document.getElementById("apDiv12").innerHTML = "星期二";
					document.getElementById("apDiv14").innerHTML = "星期三";
					document.getElementById("apDiv16").innerHTML = "星期四";
					document.getElementById("apDiv18").innerHTML = "星期五";
				}
				if (day == 7) {
					document.getElementById("apDiv6").innerHTML = "星期日";
					document.getElementById("apDiv8").innerHTML = "星期一";
					document.getElementById("apDiv10").innerHTML = "星期二";
					document.getElementById("apDiv12").innerHTML = "星期三";
					document.getElementById("apDiv14").innerHTML = "星期四";
					document.getElementById("apDiv16").innerHTML = "星期五";
					document.getElementById("apDiv18").innerHTML = "星期六";
				}
				if (day == 1) {
					document.getElementById("apDiv6").innerHTML = "星期一";
					document.getElementById("apDiv8").innerHTML = "星期二";
					document.getElementById("apDiv10").innerHTML = "星期三";
					document.getElementById("apDiv12").innerHTML = "星期四";
					document.getElementById("apDiv14").innerHTML = "星期五";
					document.getElementById("apDiv16").innerHTML = "星期六";
					document.getElementById("apDiv18").innerHTML = "星期日";
				}
				if (day == 2) {
					document.getElementById("apDiv6").innerHTML = "星期二";
					document.getElementById("apDiv8").innerHTML = "星期三";
					document.getElementById("apDiv10").innerHTML = "星期四";
					document.getElementById("apDiv12").innerHTML = "星期五";
					document.getElementById("apDiv14").innerHTML = "星期六";
					document.getElementById("apDiv16").innerHTML = "星期日";
					document.getElementById("apDiv18").innerHTML = "星期一";
				}
				if (day == 3) {
					document.getElementById("apDiv6").innerHTML = "星期三";
					document.getElementById("apDiv8").innerHTML = "星期四";
					document.getElementById("apDiv10").innerHTML = "星期五";
					document.getElementById("apDiv12").innerHTML = "星期六";
					document.getElementById("apDiv14").innerHTML = "星期日";
					document.getElementById("apDiv16").innerHTML = "星期一";
					document.getElementById("apDiv18").innerHTML = "星期二";
				}
				if (day == 4) {
					document.getElementById("apDiv6").innerHTML = "星期四";
					document.getElementById("apDiv8").innerHTML = "星期五";
					document.getElementById("apDiv10").innerHTML = "星期六";
					document.getElementById("apDiv12").innerHTML = "星期日";
					document.getElementById("apDiv14").innerHTML = "星期一";
					document.getElementById("apDiv16").innerHTML = "星期二";
					document.getElementById("apDiv18").innerHTML = "星期三";
				}
				if (day == 5) {
					document.getElementById("apDiv6").innerHTML = "星期五";
					document.getElementById("apDiv8").innerHTML = "星期六";
					document.getElementById("apDiv10").innerHTML = "星期日";
					document.getElementById("apDiv12").innerHTML = "星期一";
					document.getElementById("apDiv14").innerHTML = "星期二";
					document.getElementById("apDiv16").innerHTML = "星期三";
					document.getElementById("apDiv18").innerHTML = "星期四";
				}
			},
			changeWeek : function(serviceAction){
				var body = {
						ycrq1 : document.getElementById("ycrq1").innerHTML,
						ycrq2 : document.getElementById("ycrq2").innerHTML,
						ycrq3 : document.getElementById("ycrq3").innerHTML,
						ycrq4 : document.getElementById("ycrq4").innerHTML,
						ycrq5 : document.getElementById("ycrq5").innerHTML,
						ycrq6 : document.getElementById("ycrq6").innerHTML,
						ycrq7 : document.getElementById("ycrq7").innerHTML
					}
				if("getBeforWeek"==serviceAction){
					var dqsjd1 = Date.getDateAfter(new Date(), 1).substring(5,
							7);
					var dqsjd2 = Date.getDateAfter(new Date(), 1).substring(8);
					if ((document.getElementById("apDiv7").innerHTML == ("("
							+ dqsjd1 + "." + dqsjd2 + ")"))
							|| (document.getElementById("apDiv7").innerHTML == "(明天)")) {
						return;
					}
				}
					var sz = phis.script.rmi.miniJsonRequestSync({
								serviceId : "referralService",
								serviceAction : serviceAction,
								body : body
							});
					var xcday = sz.json.body.xcday;
					if (xcday) {
						if (xcday == 6) {
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : "referralService",
										serviceAction : "getDay"
									});
							var day = ret.json.body.day;
							var days = ret.json.body.days;
							var rqMap = ret.json.body.rqMap;
							this.showWeek(day);
							document.getElementById("apDiv7").innerHTML = "(明天)";
							document.getElementById("apDiv9").innerHTML = "(后天)";
							document.getElementById("apDiv11").innerHTML = "("
									+ days.day3 + ")";
							document.getElementById("apDiv13").innerHTML = "("
									+ days.day4 + ")";
							document.getElementById("apDiv15").innerHTML = "("
									+ days.day5 + ")";
							document.getElementById("apDiv17").innerHTML = "("
									+ days.day6 + ")";
							document.getElementById("apDiv19").innerHTML = "("
									+ days.day7 + ")";
							document.getElementById("ycrq1").innerHTML = rqMap.rq1;
							document.getElementById("ycrq2").innerHTML = rqMap.rq2;
							document.getElementById("ycrq3").innerHTML = rqMap.rq3;
							document.getElementById("ycrq4").innerHTML = rqMap.rq4;
							document.getElementById("ycrq5").innerHTML = rqMap.rq5;
							document.getElementById("ycrq6").innerHTML = rqMap.rq6;
							document.getElementById("ycrq7").innerHTML = rqMap.rq7;
							return;
						}
					}
					var week = sz.json.body.weekMap;
					if (week.day1 == 1) {
						document.getElementById("apDiv6").innerHTML = "星期日";
					} else if (week.day1 == 2) {
						document.getElementById("apDiv6").innerHTML = "星期一";
					} else if (week.day1 == 3) {
						document.getElementById("apDiv6").innerHTML = "星期二";
					} else if (week.day1 == 4) {
						document.getElementById("apDiv6").innerHTML = "星期三";
					} else if (week.day1 == 5) {
						document.getElementById("apDiv6").innerHTML = "星期四";
					} else if (week.day1 == 6) {
						document.getElementById("apDiv6").innerHTML = "星期五";
					} else if (week.day1 == 7) {
						document.getElementById("apDiv6").innerHTML = "星期六";
					}
					if (week.day2 == 1) {
						document.getElementById("apDiv8").innerHTML = "星期日";
					} else if (week.day2 == 2) {
						document.getElementById("apDiv8").innerHTML = "星期一";
					} else if (week.day2 == 3) {
						document.getElementById("apDiv8").innerHTML = "星期二";
					} else if (week.day2 == 4) {
						document.getElementById("apDiv8").innerHTML = "星期三";
					} else if (week.day2 == 5) {
						document.getElementById("apDiv8").innerHTML = "星期四";
					} else if (week.day2 == 6) {
						document.getElementById("apDiv8").innerHTML = "星期五";
					} else if (week.day2 == 7) {
						document.getElementById("apDiv8").innerHTML = "星期六";
					}
					if (week.day3 == 1) {
						document.getElementById("apDiv10").innerHTML = "星期日";
					} else if (week.day3 == 2) {
						document.getElementById("apDiv10").innerHTML = "星期一";
					} else if (week.day3 == 3) {
						document.getElementById("apDiv10").innerHTML = "星期二";
					} else if (week.day3 == 4) {
						document.getElementById("apDiv10").innerHTML = "星期三";
					} else if (week.day3 == 5) {
						document.getElementById("apDiv10").innerHTML = "星期四";
					} else if (week.day3 == 6) {
						document.getElementById("apDiv10").innerHTML = "星期五";
					} else if (week.day3 == 7) {
						document.getElementById("apDiv10").innerHTML = "星期六";
					}
					if (week.day4 == 1) {
						document.getElementById("apDiv12").innerHTML = "星期日";
					} else if (week.day4 == 2) {
						document.getElementById("apDiv12").innerHTML = "星期一";
					} else if (week.day4 == 3) {
						document.getElementById("apDiv12").innerHTML = "星期二";
					} else if (week.day4 == 4) {
						document.getElementById("apDiv12").innerHTML = "星期三";
					} else if (week.day4 == 5) {
						document.getElementById("apDiv12").innerHTML = "星期四";
					} else if (week.day4 == 6) {
						document.getElementById("apDiv12").innerHTML = "星期五";
					} else if (week.day4 == 7) {
						document.getElementById("apDiv12").innerHTML = "星期六";
					}
					if (week.day5 == 1) {
						document.getElementById("apDiv14").innerHTML = "星期日";
					} else if (week.day5 == 2) {
						document.getElementById("apDiv14").innerHTML = "星期一";
					} else if (week.day5 == 3) {
						document.getElementById("apDiv14").innerHTML = "星期二";
					} else if (week.day5 == 4) {
						document.getElementById("apDiv14").innerHTML = "星期三";
					} else if (week.day5 == 5) {
						document.getElementById("apDiv14").innerHTML = "星期四";
					} else if (week.day5 == 6) {
						document.getElementById("apDiv14").innerHTML = "星期五";
					} else if (week.day5 == 7) {
						document.getElementById("apDiv14").innerHTML = "星期六";
					}
					if (week.day1 == 6) {
						document.getElementById("apDiv16").innerHTML = "星期日";
					} else if (week.day6 == 2) {
						document.getElementById("apDiv16").innerHTML = "星期一";
					} else if (week.day6 == 3) {
						document.getElementById("apDiv16").innerHTML = "星期二";
					} else if (week.day6 == 4) {
						document.getElementById("apDiv16").innerHTML = "星期三";
					} else if (week.day6 == 5) {
						document.getElementById("apDiv16").innerHTML = "星期四";
					} else if (week.day6 == 6) {
						document.getElementById("apDiv16").innerHTML = "星期五";
					} else if (week.day6 == 7) {
						document.getElementById("apDiv16").innerHTML = "星期六";
					}
					if (week.day7 == 1) {
						document.getElementById("apDiv18").innerHTML = "星期日";
					} else if (week.day7 == 2) {
						document.getElementById("apDiv18").innerHTML = "星期一";
					} else if (week.day7 == 3) {
						document.getElementById("apDiv18").innerHTML = "星期二";
					} else if (week.day7 == 4) {
						document.getElementById("apDiv18").innerHTML = "星期三";
					} else if (week.day7 == 5) {
						document.getElementById("apDiv18").innerHTML = "星期四";
					} else if (week.day7 == 6) {
						document.getElementById("apDiv18").innerHTML = "星期五";
					} else if (week.day7 == 7) {
						document.getElementById("apDiv18").innerHTML = "星期六";
					}
					var days = sz.json.body.days;
					document.getElementById("apDiv7").innerHTML = "("
							+ days.day1 + ")";
					document.getElementById("apDiv9").innerHTML = "("
							+ days.day2 + ")";
					document.getElementById("apDiv11").innerHTML = "("
							+ days.day3 + ")";
					document.getElementById("apDiv13").innerHTML = "("
							+ days.day4 + ")";
					document.getElementById("apDiv15").innerHTML = "("
							+ days.day5 + ")";
					document.getElementById("apDiv17").innerHTML = "("
							+ days.day6 + ")";
					document.getElementById("apDiv19").innerHTML = "("
							+ days.day7 + ")";
					var rqMap = sz.json.body.rqMap;
					document.getElementById("ycrq1").innerHTML = rqMap.rq1;
					document.getElementById("ycrq2").innerHTML = rqMap.rq2;
					document.getElementById("ycrq3").innerHTML = rqMap.rq3;
					document.getElementById("ycrq4").innerHTML = rqMap.rq4;
					document.getElementById("ycrq5").innerHTML = rqMap.rq5;
					document.getElementById("ycrq6").innerHTML = rqMap.rq6;
					document.getElementById("ycrq7").innerHTML = rqMap.rq7;
					this.loadPaiBan();
			},
			doSave : function() {
				var jgid = document.getElementById("jg").value;
				var jgmc = document.getElementById("jg").options[document
						.getElementById("jg").selectedIndex].text
				var ksdm = document.getElementById("ks").value;
				var ksmc = document.getElementById("ks").options[document
						.getElementById("ks").selectedIndex].text
				var ghbc = document.getElementById("swxw").value;
				var ystc = document.getElementById("apDiv21").innerHTML;
				var yyhm = '';
				var yysj = '';
				var dtpbid = '';
				var yzpbid = '';
				var yyrqvalue = '';
				var yyxx = yyrqandsjvalue.split("|", 5);
				if (yyxx.length > 0) {
					yysj = yyxx[0];
					yyrqvalue = yyxx[1];
					dtpbid = yyxx[2];
					yzpbid = yyxx[3];
					yyhm = yyxx[4]
				}
				// console.debug(yyrqandsjvalue,yyrqvalue)
				var brid = this.brid;
				var mzhm = this.mzhm;
				var csny = this.csny;
				var jzxh = this.jzxh;
				var brxb = this.brxb;
				var empiId = this.empiId;
				var zzzd = this.zzzd;
				var brxm = this.brxxform.findField("BRXM").getValue();
				var nl = this.brxxform.findField("NL").getValue();
				var sfzh = this.brxxform.findField("SFZH").getValue();
				var lxdh = this.brxxform.findField("LXDH").getValue();
				var lxdz = this.brxxform.findField("LXDZ").getValue();
				var ysdh = this.brxxform.findField("YSDH").getValue();
				var zzyy = this.brxxform.findField("ZZYY").getValue();
				if (!zzzd) {
					MyMessageTip.msg("提示", "诊断不能为空", true);
					return;
				}
				if (!zzyy) {
					MyMessageTip.msg("提示", "转诊原因不能为空", true);
					return;
				}
				var bqms = this.brxxform.findField("BQMS").getValue();
				if (!bqms) {
					MyMessageTip.msg("提示", "病情描述不能为空", true);
					return;
				}
				var zljg = this.brxxform.findField("ZLJG").getValue();
				if (!zljg) {
					MyMessageTip.msg("提示", "治疗经过不能为空", true);
					return;
				}
				var sqjg = this.brxxform.findField("SQJG").getValue();
				var sqjgmc = this.brxxform.findField("SQJG").getRawValue();
				var jgdh = this.brxxform.findField("JGDH").getValue();
				var sqys = this.brxxform.findField("SQYS").getValue();
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				phis.script.rmi.jsonRequest({
							serviceId : "referralService",
							serviceAction : "saveSendExchange",
							body : {
								"EMPIID" : empiId,
								"JIUZHENKLX" : "3",
								"JIUZHENKH" : this.exContext.empiData.cardNo,
								"YIBAOKLX" : "",
								"YIBAOKXX" : "",
								"YEWULX" : "1",
								"BINGREMXM" : brxm,
								"BINGRENXB" : brxb,
								"BINGRENCSRQ" : csny,
								"BINGRENNL" : nl,
								"BINGRENSFZH" : sfzh,
								"BINGRENLXDH" : lxdh,
								"BINGRENLXDZ" : lxdz,
								"BINGRENFYLB" : "",
								"SHENQINGJGDM" : sqjg,
								"SHENQINGJGMC" : sqjgmc,
								"SHENQINGJGLXDH" : jgdh,
								"SHENQINGYS" : sqys,
								"SHENQINGYSDH" : ysdh,
								"SHENQINGRQ" : new Date().format('Y-m-d'),
								"ZHUANZHENYY" : zzyy,
								"ZHUANZHENZD" : zzzd,
								"BINQINGMS" : bqms,
								"ZHUANZHENZYSX" : zljg,
								"ZHUANRUYYDM" : jgid,
								"ZHUANRUYYMC" : jgmc,
								"ZHUANRUKSDM" : ksdm,
								"ZHUANRUKSMC" : ksmc,
								"YISHENGDM" : ysdmvalue,
								"YISHENGXM" : ysmcvalue,
								"JIUZHENSJ" : yysj,
								"GUAHAOBC" : ghbc,
								"GUAHAOXH" : yyhm,
								"YIZHOUPBID" : yzpbid,
								"DANGTIANPBID" : dtpbid,
								"ZHUANZHENRQ" : yyrqvalue,
								"GUAHAOLB" : "1",
								"JZXH" : jzxh
							}
						}, function(code, msg, json) {
							this.panel.el.unmask()
							if (code > 300) {
								MyMessageTip.msg("提示", msg, true)
								return
							} else {
								MyMessageTip.msg("提示", "保存成功!", true)
								this.opener.onClose();
								this.doPrint(json.ZHUANZHENDH, json.QUHAOMM);
								if(this.opener.opener != null){
									this.opener.opener.refresh();
								}
							}
						}, this)
			},
			doPrint : function(zhuanzhendh, quhaomm) {
				var pages = "phis.prints.jrxml.RegisterReqOrder";
				var url = "resources/" + pages
						+ ".print?silentPrint=1&zhuanzhendh=" + zhuanzhendh
						+ "&quhaomm=" + quhaomm;
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				// 预览LODOP.PREVIEW();
				// 预览LODOP.PRINT();
				// LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			}
		});