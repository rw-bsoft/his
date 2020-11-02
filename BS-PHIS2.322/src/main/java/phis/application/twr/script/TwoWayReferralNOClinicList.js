$package("phis.application.twr.script")

$import("phis.script.SimpleList", "phis.script.util.DateUtil");

phis.application.twr.script.TwoWayReferralNOClinicList = function(cfg) {
	this.exContext = {};
	phis.application.twr.script.TwoWayReferralNOClinicList.superclass.constructor
			.apply(this, [cfg]);
	var yyrq = '';
	var ysdmvalue = '';
	var ysmcvalue = '';
}

Ext.extend(phis.application.twr.script.TwoWayReferralNOClinicList,
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
						+ 'height:34px;'
						+ 'z-index:1;'
						+ '}'
						+ '#apDiv2 {'
						+ 'position:absolute;'
						+ 'width:409px;'
						+ 'height:34px;'
						+ 'z-index:2;'
						+ 'left: 10px;'
						+ 'top: 50px;'
						+ '}'
						+ '#apDiv3 {'
						+ 'position:absolute;'
						+ 'width:112px;'
						+ 'height:502px;'
						+ 'z-index:3;'
						+ 'left: 421px;'
						+ '}'
						+ '#apDiv4 {'
						+ 'position:absolute;'
						+ 'width:103px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'left: 6px;'
						+ 'top: 3px;'
						+ '}'
						+ '#apDiv5 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:1;'
						+ 'left: 5px;'
						+ 'top: 32px;'
						+ '}'
						+ '#apDiv6 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'left: 5px;'
						+ 'top: 67px;'
						+ '}'
						+ '#apDiv7 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ '}'
						+ '#apDiv8 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'top: 128px;'
						+ '}'
						+ '#apDiv9 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ '}'
						+ '#apDiv10 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'top: 190px;'
						+ '}'
						+ '#apDiv11 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ '}'
						+ '#apDiv12 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'top: 252px;'
						+ '}'
						+ '#apDiv13 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ '}'
						+ '#apDiv14 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'top: 314px;'
						+ '}'
						+ '#apDiv15 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ '}'
						+ '#apDiv16 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'top: 376px;'
						+ '}'
						+ '#apDiv17 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ '}'
						+ '#apDiv18 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ 'top: 438px;'
						+ '}'
						+ '#apDiv19 {'
						+ 'position:absolute;'
						+ 'width:104px;'
						+ 'height:30px;'
						+ 'z-index:4;'
						+ '}'
						+ '#apDiv20 {'
						+ 'position:absolute;'
						+ 'width:450px;'
						+ 'height:61px;'
						+ 'z-index:4;'
						+ 'left: 534px;'
						+ 'top: 16px;'
						+ '}'
						+ '#apDiv21 {'
						+ 'position:absolute;'
						+ 'width:450px;'
						+ 'height:440px;'
						+ 'z-index:5;'
						+ 'left: 534px;'
						+ 'top: 80px;'
						+ '}'
						+ '#apDiv22 {'
						+ 'position:absolute;'
						+ 'width:108px;'
						+ 'height:115px;'
						+ 'z-index:6;'
						+ 'left: 10px;'
						+ 'top: 67px;'
						+ '}'
						+ '#apDiv23 {'
						+ 'position:absolute;'
						+ 'width:303px;'
						+ 'height:115px;'
						+ 'z-index:7;'
						+ 'left: 116px;'
						+ 'top: 67px;'
						+ '}'
						+ '</style>'
						+ '</head>'
						+ '<body>'
						+ '<div id="apDiv1">'
						+ '<table width="405">'
						+ '<tr>'
						+ '<td width="51">机构：</td>'
						+ '<td width="224"><select name="jg" id="jg" style="width:180px">'
						+ '</select></td>'
						+ '<td width="51">科室：</td>'
						+ '<td width="108"><select name="ks" id="ks" style="width:108px">'
						+ '</select></td>'
						+ '</tr>'
						+ '</table>'
						+ '</div>'
						+ '<div id="apDiv2">'
						+ '<table width="409" border="1">'
						+ '<tr>'
						+ '<td width="92"><div align="center">医生</div></td>'
						+ '<td width="301"><div align="center">医生特长</div></td>'
						+ '</tr>'
						+ '</table>'
						+ '</div>'
						+ '<div id="apDiv3">'
						+ '<table width="112" border="1" id="mz">'
						+ '<tr>'
						+ '<td height="60"><div id="apDiv4">'
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
						+ '</div>'
						+ '</div></td>'
						+ '</tr>'
						+ '<tr id="z1">'
						+ '<td height="60"><div id="apDiv6" align="center"></div>'
						+ '<div id="apDiv7" align="center"></div><div id="ycrq1" style="display:none;"></td>'
						+ '</tr>'
						+ '<tr id="z2">'
						+ '<td height="60"><div id="apDiv8" align="center"></div>'
						+ '<div id="apDiv9" align="center"></div><div id="ycrq2" style="display:none;"></td>'
						+ '</tr>'
						+ '<tr id="z3">'
						+ '<td height="60"><div id="apDiv10" align="center"></div>'
						+ '<div id="apDiv11" align="center"></div><div id="ycrq3" style="display:none;"></td>'
						+ '</tr>'
						+ '<tr id="z4">'
						+ '<td height="60"><div id="apDiv12" align="center"></div>'
						+ '<div id="apDiv13" align="center"></div><div id="ycrq4" style="display:none;"></td>'
						+ '</tr>'
						+ '<tr id="z5">'
						+ '<td height="60"><div id="apDiv14" align="center"></div>'
						+ '<div id="apDiv15" align="center"></div><div id="ycrq5" style="display:none;"></td>'
						+ '</tr>'
						+ '<tr id="z6">'
						+ '<td height="60"><div id="apDiv16" align="center"></div>'
						+ '<div id="apDiv17" align="center"></div><div id="ycrq6" style="display:none;"></td>'
						+ '</tr>'
						+ '<tr id="z7">'
						+ '<td height="60"><div id="apDiv18" align="center"></div>'
						+ '<div id="apDiv19" align="center"></div><div id="ycrq7" style="display:none;"></td>'
						+ '</tr>' + '</table>' + '</div>'
						+ '<div id="apDiv20">'
						+ '<div align="center">号源信息</div>' + '</div>'
						+ '<div id="apDiv21">' + '</div>'
						+ '<div id="apDiv22">'
						+ '<table width="97" border="1" id="ys"></table></div>'
						+ '<div id="apDiv23"></div></body>' + '</html>';
			},
			onReady : function() {
				// 获取机构
				var Hospital1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "referralService",
							serviceAction : "getClinicHospital",
							body : {
								"referralDate" : new Date()
							}
						});
				var hospitalinfo1 = Hospital1.json.body;
				var length1 = document.getElementById("jg").options.length;
				for (var j = 0; j < length1; j++) {
					document.getElementById("jg").options.remove(j);
				}
				for (var i = 0; i < hospitalinfo1.length; i++) {
					var hospitalCode1 = hospitalinfo1[i].hospitalCode;
					var hospitalName1 = hospitalinfo1[i].hospital;
					var optionhospital1 = new Option(hospitalName1,
							hospitalCode1);
					document.getElementById("jg").options.add(optionhospital1);
				}
				// 根据机构获取科室
				Ext.getDom("jg").onchange = function() {
					var office1 = phis.script.rmi.miniJsonRequestSync({
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
					var officeinfo1 = office1.json.body;
					var lengthks1 = document.getElementById("ks").options.length
					for (var j = 0; j < lengthks1; j++) {
						document.getElementById("ks").options.remove(j)
					}
					for (var i = 0; i < officeinfo1.length; i++) {
						var officeCode1 = officeinfo1[i].KESHIDM;
						var officeName1 = officeinfo1[i].KESHIMC;
						var optionoffice1 = new Option(officeName1, officeCode1);
						document.getElementById("ks").options
								.add(optionoffice1);
					}
				}
				Ext.getDom("ks").onchange = function() {
					var doctor1 = phis.script.rmi.miniJsonRequestSync({
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
					var doctorinfo1 = doctor1.json.body;
					var tabRows1 = document.getElementById("ys").rows.length;
					for (var i = 0; i < tabRows1; i++) {
						document.getElementById("ys").deleteRow(i);
						tabRows1 = tabRows1 - 1;
						i = i - 1;
					}
					for (var i = 0; i < doctorinfo1.length; i++) {
						var ystable1 = document.getElementById("ys");
						var ysRow1 = ystable1.insertRow(i); // 添加行
						ysRow1.id = doctorinfo1[i].YISHENGDM;
						var ysCell1 = ysRow1.insertCell(); // 添加列
						ysCell1.align = "center";
						ysCell1.id = doctorinfo1[i].YISHENGDM;
						ysCell1.innerHTML = doctorinfo1[i].YISHENGXM; // 添加数据
						Ext.getDom(doctorinfo1[i].YISHENGDM).onclick = function() {
							var trs1 = document.getElementById('ys')
									.getElementsByTagName('tr');
							for (var t = 0; t < trs1.length; t++) {
								if (trs1[t] == this) {
									trs1[t].style.backgroundColor = '#DFEBF2';
									ysdmvalue = ysCell1.id;
									ysmcvalue = ysCell1.innerHTML;
									document.getElementById("apDiv23").align = "left";
									document.getElementById("apDiv23").innerHTML = "优秀专家,多年临床经验";
									var ysid = trs1[t].id;
									Ext.getDom("z1").onclick = function() {
										var trsmz = document
												.getElementById('mz')
												.getElementsByTagName('tr');
										for (var z = 0; z < trsmz.length; z++) {
											if (trsmz[z] == this) {
												yyrq = document
														.getElementById("ycrq1").innerHTML;
												trsmz[z].style.backgroundColor = '#DFEBF2';
												var no1 = phis.script.rmi
														.miniJsonRequestSync({
															serviceId : "referralService",
															serviceAction : "registerSourceHY",
															body : {
																"registerType" : "2",
																"referralDate" : document
																		.getElementById("ycrq1").innerHTML,
																"registerSchedule" : document
																		.getElementById("swxw").value,
																"registerCategory" : "1",
																"hospitalCode" : document
																		.getElementById("jg").value,
																"departmentCode" : document
																		.getElementById("ks").value,
																"doctorId" : ysid
															}
														});
												var noinfo1 = no1.json.body;
												var hy1 = '';
												for (var f = 0; f < noinfo1.length; f++) {
													hy1 += '<input type="radio" name="hy" value='
															+ noinfo1[f].GUAHAOXH
															+ '|'
															+ noinfo1[f].JIUZHENSJ
																	.trim()
															+ '|'
															+ noinfo1[f].DANGTIANPBID
															+ '|'
															+ noinfo1[f].YIZHOUPBID
															+ '>'
															+ noinfo1[f].GUAHAOXH
															+ '号('
															+ noinfo1[f].JIUZHENSJ
															+ ')</radio> ';
												}
												document
														.getElementById("apDiv21").innerHTML = hy1; // 添加数据
											} else {
												trsmz[z].style.backgroundColor = '';
											}
										}
									}
									Ext.getDom("z2").onclick = function() {
										var trsmz = document
												.getElementById('mz')
												.getElementsByTagName('tr');
										for (var z = 0; z < trsmz.length; z++) {
											if (trsmz[z] == this) {
												yyrq = document
														.getElementById("ycrq2").innerHTML;
												trsmz[z].style.backgroundColor = '#DFEBF2';
												var no = phis.script.rmi
														.miniJsonRequestSync({
															serviceId : "referralService",
															serviceAction : "registerSource",
															body : {
																"registerType" : "2",
																"referralDate" : document
																		.getElementById("ycrq2").innerHTML,
																"registerSchedule" : document
																		.getElementById("swxw").value,
																"registerCategory" : "1",
																"hospitalCode" : document
																		.getElementById("jg").value,
																"departmentCode" : document
																		.getElementById("ks").value,
																"doctorId" : ysid

															}
														});
												var noinfo1 = no.json.body;
												var hy2 = '';
												for (var f = 0; f < noinfo1.length; f++) {
													hy2 += '<input type="radio" name="hy" value='
															+ noinfo1[f].GUAHAOXH
															+ '|'
															+ noinfo1[f].JIUZHENSJ
																	.trim()
															+ '|'
															+ noinfo1[f].DANGTIANPBID
															+ '|'
															+ noinfo1[f].YIZHOUPBID
															+ '>'
															+ noinfo1[f].GUAHAOXH
															+ '号('
															+ noinfo1[f].JIUZHENSJ
															+ ')</radio> ';
												}
												document
														.getElementById("apDiv21").innerHTML = hy2; // 添加数据
											} else {
												trsmz[z].style.backgroundColor = '';
											}
										}
									}
									Ext.getDom("z3").onclick = function() {
										var trsmz = document
												.getElementById('mz')
												.getElementsByTagName('tr');
										for (var z = 0; z < trsmz.length; z++) {
											if (trsmz[z] == this) {
												yyrq = document
														.getElementById("ycrq3").innerHTML;
												trsmz[z].style.backgroundColor = '#DFEBF2';
												var no = phis.script.rmi
														.miniJsonRequestSync({
															serviceId : "referralService",
															serviceAction : "registerSource",
															body : {
																"registerType" : "2",
																"referralDate" : document
																		.getElementById("ycrq3").innerHTML,
																"registerSchedule" : document
																		.getElementById("swxw").value,
																"registerCategory" : "1",
																"hospitalCode" : document
																		.getElementById("jg").value,
																"departmentCode" : document
																		.getElementById("ks").value,
																"doctorId" : ysid

															}
														});
												var noinfo1 = no.json.body;
												var hy3 = '';
												for (var f = 0; f < noinfo1.length; f++) {
													hy3 += '<input type="radio" name="hy" value='
															+ noinfo1[f].GUAHAOXH
															+ '|'
															+ noinfo1[f].JIUZHENSJ
																	.trim()
															+ '|'
															+ noinfo1[f].DANGTIANPBID
															+ '|'
															+ noinfo1[f].YIZHOUPBID
															+ '>'
															+ noinfo1[f].GUAHAOXH
															+ '号('
															+ noinfo1[f].JIUZHENSJ
															+ ')</radio> ';
												}
												document
														.getElementById("apDiv21").innerHTML = hy3; // 添加数据
											} else {
												trsmz[z].style.backgroundColor = '';
											}
										}
									}
									Ext.getDom("z4").onclick = function() {
										var trsmz = document
												.getElementById('mz')
												.getElementsByTagName('tr');
										for (var z = 0; z < trsmz.length; z++) {
											if (trsmz[z] == this) {
												yyrq = document
														.getElementById("ycrq4").innerHTML;
												trsmz[z].style.backgroundColor = '#DFEBF2';
												var no = phis.script.rmi
														.miniJsonRequestSync({
															serviceId : "referralService",
															serviceAction : "registerSource",
															body : {
																"registerType" : "2",
																"referralDate" : document
																		.getElementById("ycrq4").innerHTML,
																"registerSchedule" : document
																		.getElementById("swxw").value,
																"registerCategory" : "1",
																"hospitalCode" : document
																		.getElementById("jg").value,
																"departmentCode" : document
																		.getElementById("ks").value,
																"doctorId" : ysid

															}
														});
												var noinfo1 = no.json.body;
												var hy4 = '';
												for (var f = 0; f < noinfo1.length; f++) {
													hy4 += '<input type="radio" name="hy" value='
															+ noinfo1[f].GUAHAOXH
															+ '|'
															+ noinfo1[f].JIUZHENSJ
																	.trim()
															+ '|'
															+ noinfo1[f].DANGTIANPBID
															+ '|'
															+ noinfo1[f].YIZHOUPBID
															+ '>'
															+ noinfo1[f].GUAHAOXH
															+ '号('
															+ noinfo1[f].JIUZHENSJ
															+ ')</radio> ';
												}
												document
														.getElementById("apDiv21").innerHTML = hy4; // 添加数据
											} else {
												trsmz[z].style.backgroundColor = '';
											}
										}
									}
									Ext.getDom("z5").onclick = function() {
										var trsmz = document
												.getElementById('mz')
												.getElementsByTagName('tr');
										for (var z = 0; z < trsmz.length; z++) {
											if (trsmz[z] == this) {
												yyrq = document
														.getElementById("ycrq5").innerHTML;
												trsmz[z].style.backgroundColor = '#DFEBF2';
												var no = phis.script.rmi
														.miniJsonRequestSync({
															serviceId : "referralService",
															serviceAction : "registerSource",
															body : {
																"registerType" : "2",
																"referralDate" : document
																		.getElementById("ycrq5").innerHTML,
																"registerSchedule" : document
																		.getElementById("swxw").value,
																"registerCategory" : "1",
																"hospitalCode" : document
																		.getElementById("jg").value,
																"departmentCode" : document
																		.getElementById("ks").value,
																"doctorId" : ysid

															}
														});
												var noinfo1 = no.json.body;
												var hy5 = '';
												for (var f = 0; f < noinfo1.length; f++) {
													hy5 += '<input type="radio" name="hy" value='
															+ noinfo1[f].GUAHAOXH
															+ '|'
															+ noinfo1[f].JIUZHENSJ
																	.trim()
															+ '|'
															+ noinfo1[f].DANGTIANPBID
															+ '|'
															+ noinfo1[f].YIZHOUPBID
															+ '>'
															+ noinfo1[f].GUAHAOXH
															+ '号('
															+ noinfo1[f].JIUZHENSJ
															+ ')</radio> ';
												}
												document
														.getElementById("apDiv21").innerHTML = hy5; // 添加数据
											} else {
												trsmz[z].style.backgroundColor = '';
											}
										}
									}
									Ext.getDom("z6").onclick = function() {
										var trsmz = document
												.getElementById('mz')
												.getElementsByTagName('tr');
										for (var z = 0; z < trsmz.length; z++) {
											if (trsmz[z] == this) {
												yyrq = document
														.getElementById("ycrq6").innerHTML;
												trsmz[z].style.backgroundColor = '#DFEBF2';
												var no = phis.script.rmi
														.miniJsonRequestSync({
															serviceId : "referralService",
															serviceAction : "registerSource",
															body : {
																"registerType" : "2",
																"referralDate" : document
																		.getElementById("ycrq6").innerHTML,
																"registerSchedule" : document
																		.getElementById("swxw").value,
																"registerCategory" : "1",
																"hospitalCode" : document
																		.getElementById("jg").value,
																"departmentCode" : document
																		.getElementById("ks").value,
																"doctorId" : ysid

															}
														});
												var noinfo1 = no.json.body;
												var hy6 = '';
												for (var f = 0; f < noinfo1.length; f++) {
													hy6 += '<input type="radio" name="hy" value='
															+ noinfo1[f].GUAHAOXH
															+ '|'
															+ noinfo1[f].JIUZHENSJ
																	.trim()
															+ '|'
															+ noinfo1[f].DANGTIANPBID
															+ '|'
															+ noinfo1[f].YIZHOUPBID
															+ '>'
															+ noinfo1[f].GUAHAOXH
															+ '号('
															+ noinfo1[f].JIUZHENSJ
															+ ')</radio> ';
												}
												document
														.getElementById("apDiv21").innerHTML = hy6; // 添加数据
											} else {
												trsmz[z].style.backgroundColor = '';
											}
										}
									}
									Ext.getDom("z7").onclick = function() {
										var trsmz = document
												.getElementById('mz')
												.getElementsByTagName('tr');
										for (var z = 0; z < trsmz.length; z++) {
											if (trsmz[z] == this) {
												yyrq = document
														.getElementById("ycrq7").innerHTML;
												trsmz[z].style.backgroundColor = '#DFEBF2';
												var no = phis.script.rmi
														.miniJsonRequestSync({
															serviceId : "referralService",
															serviceAction : "registerSource",
															body : {
																"registerType" : "2",
																"referralDate" : document
																		.getElementById("ycrq7").innerHTML,
																"registerSchedule" : document
																		.getElementById("swxw").value,
																"registerCategory" : "1",
																"hospitalCode" : document
																		.getElementById("jg").value,
																"departmentCode" : document
																		.getElementById("ks").value,
																"doctorId" : ysid

															}
														});
												var noinfo1 = no.json.body;
												var hy7 = '';
												for (var f = 0; f < noinfo1.length; f++) {
													hy7 += '<input type="radio" name="hy" value='
															+ noinfo1[f].GUAHAOXH
															+ '|'
															+ noinfo1[f].JIUZHENSJ
																	.trim()
															+ '|'
															+ noinfo1[f].DANGTIANPBID
															+ '|'
															+ noinfo1[f].YIZHOUPBID
															+ '>'
															+ noinfo1[f].GUAHAOXH
															+ '号('
															+ noinfo1[f].JIUZHENSJ
															+ ')</radio> ';
												}
												document
														.getElementById("apDiv21").innerHTML = hy7; // 添加数据
											} else {
												trsmz[z].style.backgroundColor = '';
											}
										}
									}
								} else {
									trs1[t].style.backgroundColor = '';
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
					var body = {
						ycrq1 : document.getElementById("ycrq1").innerHTML,
						ycrq2 : document.getElementById("ycrq2").innerHTML,
						ycrq3 : document.getElementById("ycrq3").innerHTML,
						ycrq4 : document.getElementById("ycrq4").innerHTML,
						ycrq5 : document.getElementById("ycrq5").innerHTML,
						ycrq6 : document.getElementById("ycrq6").innerHTML,
						ycrq7 : document.getElementById("ycrq7").innerHTML
					}
					var dqsjd1 = Date.getDateAfter(new Date(), 1).substring(5,
							7);
					var dqsjd2 = Date.getDateAfter(new Date(), 1).substring(8);
					if ((document.getElementById("apDiv7").innerHTML == ("("
							+ dqsjd1 + "." + dqsjd2 + ")"))
							|| (document.getElementById("apDiv7").innerHTML == "(明天)")) {
						return;
					}
					var sz = phis.script.rmi.miniJsonRequestSync({
								serviceId : "referralService",
								serviceAction : "getBeforWeek",
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
				};
				Ext.getDom("xz").onclick = function() {
					var body = {
						ycrq1 : document.getElementById("ycrq1").innerHTML,
						ycrq2 : document.getElementById("ycrq2").innerHTML,
						ycrq3 : document.getElementById("ycrq3").innerHTML,
						ycrq4 : document.getElementById("ycrq4").innerHTML,
						ycrq5 : document.getElementById("ycrq5").innerHTML,
						ycrq6 : document.getElementById("ycrq6").innerHTML,
						ycrq7 : document.getElementById("ycrq7").innerHTML
					}
					var xz = phis.script.rmi.miniJsonRequestSync({
								serviceId : "referralService",
								serviceAction : "getNextWeek",
								body : body
							});
					var week = xz.json.body.weekMap;
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
					var days = xz.json.body.days;
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
					var rqMap = xz.json.body.rqMap;
					document.getElementById("ycrq1").innerHTML = rqMap.rq1;
					document.getElementById("ycrq2").innerHTML = rqMap.rq2;
					document.getElementById("ycrq3").innerHTML = rqMap.rq3;
					document.getElementById("ycrq4").innerHTML = rqMap.rq4;
					document.getElementById("ycrq5").innerHTML = rqMap.rq5;
					document.getElementById("ycrq6").innerHTML = rqMap.rq6;
					document.getElementById("ycrq7").innerHTML = rqMap.rq7;
				};
			},
			doSave : function() {
				var jgid = document.getElementById("jg").value;
				var jgmc = document.getElementById("jg").options[document
						.getElementById("jg").selectedIndex].text
				var ksdm = document.getElementById("ks").value;
				var ksmc = document.getElementById("ks").options[document
						.getElementById("ks").selectedIndex].text
				var ghbc = document.getElementById("swxw").value;
				var ystc = document.getElementById("apDiv23").innerHTML;
				var yyrqvalue = yyrq;
				var yyhy = document.getElementById("apDiv21").childNodes;
				var yyhyvalue = '';
				for (var i = 0; i < yyhy.length; i++) {
					if (yyhy[i].checked) {
						yyhyvalue = yyhy[i].value;
					}
				}
				var yyhm = '';
				var yysj = '';
				var dtpbid = '';
				var yzpbid = '';
				var yyxx = yyhyvalue.split("|", 4);
				if (yyxx.length > 0) {
					yyhm = yyxx[0];
					yysj = yyxx[1];
					dtpbid = yyxx[2];
					yzpbid = yyxx[3];
				}
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
							serviceAction : "saveTWRBRZL",
							body : {
								"EMPIID" : empiId,
								"JIUZHENKLX" : "3",
								"JIUZHENKH" : "",
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
								"ZHUANZHENRQ" : new Date(Date.parse(Date.getServerDateTime())).format('Y-m-d h:m:s'),
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
							}
						}, this)// jsonRequest
			}
		});