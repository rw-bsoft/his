$package("phis.application.twr.script")

$import("phis.script.SelectList", "util.dictionary.DictionaryLoader")

phis.application.twr.script.DRExamScheduleList = function(cfg) {
	phis.application.twr.script.DRExamScheduleList.superclass.constructor
			.apply(this, [cfg])
//	var jcxmvalue = '';
//	var jcxmmcvalue = '';
//	var jcbwvalue = '';
//	var jcbwmcvalue = '';
//	var jcfxvalue = '';
//	var jcyqvalue = '';
//	var jcyqmcvalue = '';
//	var yynovalue = '';
//	var yysjvalue = '';
	var rqvalue = '';
}
Ext.extend(phis.application.twr.script.DRExamScheduleList,
		phis.script.SelectList, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							html : this.getFormHtmlTemplatejc()
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this);
				return panel;
			},
			getFormHtmlTemplatejc : function() {
				return '<html>'
						+ '<head>'
						+ '<style type="text/css">'
						+ ' #apDiv1zy {'
						+ 'position:absolute;'
						+ 'width:200px;'
						+ 'height:115px;'
						+ 'z-index:1;'
						+ '}'
						+ '#apDiv2zy {'
						+ 'position:absolute;'
						+ 'width:420px;'
						+ 'height:115px;'
						+ 'z-index:2;'
						+ 'left: 210px;'
						+ '}'
						+ '</style>'
						+ '</head>'
						+ '<body>'
						+ '<div id="apDiv1zy">'
						+ '<table width="200" border="1" id="jczrjg">'
						+ '<tr>'
						+ '<td><div id="zrjgbtDiv" align="center">转入机构</div></td>'
						+ '</tr>' 
						+ '</table>' + '</div>' + '<div id="apDiv2zy">'
						+ '<table width="420" border="1" id="jcpb">' + '<tr>'
						+ '<td colspan="3"><div align="center">检查排班</div></td>'
						+ '</tr>' + '<tr>'
						+ '<td><div align="center">科室名称</div></td>'
						+ '<td><div align="center">项目名称</div></td>'
						+ '<td><div align="center">检查时段</div></td>'+ '</tr>'
						+ '</table>' + '</div>' + '</body>' + '</html>';
			},
//			getFormHtmlTemplatejc : function() {
//				return '<html>'
//						+ '<head>'
//						+ '<style type="text/css">'
//						+ '#apDiv2jc {'
//						+ 'position:absolute;'
//						+ 'width:120px;'
//						+ 'height:20px;'
//						+ 'z-index:2;'
//						+ 'left: 156px;'
//						+ 'top: 16px;'
//						+ '}#apDiv3jc {'
//						+ 'position:absolute;'
//						+ 'width:265px;'
//						+ 'height:115px;'
//						+ 'z-index:3;'
//						+ 'left: 10px;'
//						+ 'top: 39px;'
//						+ '}#apDiv4jc {'
//						+ 'position:absolute;'
//						+ 'width:112px;'
//						+ 'height:139px;'
//						+ 'z-index:4;'
//						+ 'left: 277px;'
//						+ 'top: 15px;'
//						+ '}#apDiv5jc {'
//						+ 'position:absolute;'
//						+ 'width:100px;'
//						+ 'height:139px;'
//						+ 'z-index:5;'
//						+ 'left: 398px;'
//						+ 'top: 15px;'
//						+ '}#apDiv6jc {'
//						+ 'position:absolute;'
//						+ 'width:240px;'
//						+ 'height:139px;'
//						+ 'z-index:6;'
//						+ 'left: 519px;'
//						+ 'top: 15px;'
//						+ '}'
//						+ '#apDiv1jc {'
//						+ 'position:absolute;'
//						+ 'width:150px;'
//						+ 'height:20px;'
//						+ 'z-index:9;'
//						+ 'top: 16px;'
//						+ '}'
//						+ '</style></head><body><div id="apDiv2jc">'
//						+ '<div align="left">类型:  '
//						+ '<select name="lx" id="lx" style="width:80px">'
//						+ '</select>'
//						+ '</div></div><div id="apDiv3jc"><table width="265" border="1" id="jcxm"><tr><td><div id="jcxmbtDiv" align="center">检查项目</div></td></tr></table></div>'
//						+ '<div id="apDiv4jc"><table width="120" border="1" id="jcbw"><tr><td><div align="center">检查部位</div></td></tr></table></div>'
//						+ '<div id="apDiv5jc"><table width="120" border="1" id="jcfx"><tr><td><div align="center">检查方向</div></td></tr></table></div>'
//						+ '<div id="apDiv6jc"><table width="240" border="1" id="sbxx"><tr><td width="80"><div align="center">仪器</div></td>'
//						+ '<td width="80"><div align="center">预约号</div></td><td width="80"><div align="center">时间</div></td></tr></table></div>'
//						+ '<div id="apDiv1jc">机构'
//						+ '<select name="jcjg" id="jcjg" style="width:120px">'
//						+ '</select>' + '</div>' + '</body>' + '</html>';
//			},
			onReady : function() {
				var nDate = new Date(Date.parse(Date.getServerDate()));
				this.showJCSQ(nDate.toLocaleDateString());
			},
			showJCSQ : function(rq) {
				this.selectjcpb = false;
				var this_ = this;
				rqvalue = rq;
//				document.getElementById("jcxmbtDiv").innerHTML = "检查项目（" + rq
//						+ "）";
				var Hospital = phis.script.rmi.miniJsonRequestSync({
							serviceId : "referralService",
							serviceAction : "getClinicHospital",
							body : {
								"referralDate" : rq
							}
						});
				var hospitalinfozr = Hospital.json.body;
				var zrjgtabRows = document.getElementById("jczrjg").rows.length;
				for (var i = 1; i < zrjgtabRows; i++) {
					document.getElementById("jczrjg").deleteRow(i);
					zrjgtabRows = zrjgtabRows - 1;
					i = i - 1;
				}
				this.hospitals = {};
				for (var i = 0; i < hospitalinfozr.length; i++) {
					this.hospitals[hospitalinfozr[i].hospitalCode] = hospitalinfozr[i].hospital;
					var zrjgtable = document.getElementById("jczrjg");
					var zrjgRow = zrjgtable.insertRow(i + 1); // 添加行
					zrjgRow.id = hospitalinfozr[i].hospitalCode;
					var zrjgCell1 = zrjgRow.insertCell(); // 添加列
					zrjgCell1.align = "center";
					zrjgCell1.id = hospitalinfozr[i].hospitalCode;
					zrjgCell1.innerHTML = hospitalinfozr[i].hospital; // 添加数据
//					var zrjgCell2 = zrjgRow.insertCell(); // 添加列
//					zrjgCell2.align = "center";
//					zrjgCell2.id = hospitalinfozr[i].hospitalCode;
//					zrjgCell2.innerHTML = hospitalinfozr[i].hospitalCode; // 添加数据
//					continue;
					var form = this.opener.opener.jcmodule.form.getForm();
					form.findField("JGMC").setValue();
					form.findField("JCJG").setValue();
					form.findField("XMMC").setValue();
					form.findField("JCXM").setValue();
					Ext.getDom(hospitalinfozr[i].hospitalCode).onclick = function() {
						this.selectjcpb = false;
						var trszrjg = document.getElementById('jczrjg')
								.getElementsByTagName('tr');
						for (var t = 0; t < trszrjg.length; t++) {
							if (trszrjg[t] == this) {
								trszrjg[t].style.backgroundColor = '#DFEBF2';
								this_.zrjgvalue = trszrjg[t].id;
								this_.zrjgmcvalue = this.getElementsByTagName('td')[0].innerHTML;
								form.findField("JGMC").setValue(this.getElementsByTagName('td')[0].innerHTML)
								form.findField("JCJG").setValue(trszrjg[t].id);
//								this_.zrksvalue = "";
//								this_.zrksmcvalue = "";
								var officezr = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : "referralService",
											serviceAction : "getMedicalStatus",
											body : {
												"referralDate" : rq,
												"hospitalCode" : trszrjg[t].id
											}
										});
								var officeinfozr = officezr.json.body;
								var zrkstabRows = document
										.getElementById("jcpb").rows.length;
								for (var zrks = 2; zrks < zrkstabRows; zrks++) {
									document.getElementById("jcpb")
											.deleteRow(zrks);
									zrkstabRows = zrkstabRows - 1;
									zrks = zrks - 1;
								}
								this_.jcpbs = {};
								for (var zrks = 0; zrks < officeinfozr.length; zrks++) {
									this_.jcpbs[officeinfozr[zrks].recordId+ "jcpb"] = officeinfozr[zrks];
									var zrkstable = document
											.getElementById("jcpb");
									var zrksRow = zrkstable.insertRow(zrks + 2); // 添加行
									zrksRow.id = officeinfozr[zrks].recordId
											+ "jcpb";
									var zrksCell1 = zrksRow.insertCell(); // 添加列
									zrksCell1.align = "center";
									zrksCell1.id = officeinfozr[zrks].departmentCode.key;
									zrksCell1.innerHTML = officeinfozr[zrks].departmentCode.text; // 添加数据
									var zrksCell2 = zrksRow.insertCell(); // 添加列
									zrksCell2.align = "center";
									zrksCell2.id = officeinfozr[zrks].itemCode.key;
									zrksCell2.innerHTML = officeinfozr[zrks].itemCode.text; // 添加数据
									var zrksCell3 = zrksRow.insertCell(); // 添加列
									zrksCell3.align = "center";
									zrksCell3.id = officeinfozr[zrks].checkInterval.key;
									zrksCell3.innerHTML = officeinfozr[zrks].checkInterval.text; // 添加数据
									Ext.getDom(officeinfozr[zrks].recordId
											+ "jcpb").onclick = function() {
										var trszrks = document
												.getElementById('jcpb')
												.getElementsByTagName('tr');
										for (var ks = 0; ks < trszrks.length; ks++) {
											if (trszrks[ks] == this) {
												this_.selectjcpb = this_.jcpbs[this.id];
												form.findField("XMMC").setValue(this_.selectjcpb.itemCode.text);
												form.findField("JCXM").setValue(this_.selectjcpb.itemCode.key);
												trszrks[ks].style.backgroundColor = '#DFEBF2';
											} else {
												trszrks[ks].style.backgroundColor = '';
											}
										}
									}
								}
							} else {
								trszrjg[t].style.backgroundColor = '';
							}
						}
					}
				}
				
//				var lengthjcjg = document.getElementById("jcjg").options.length;
//				for (var i = 0; i < lengthjcjg; i++) {
//					document.getElementById("jcjg").options.remove(i);
//				}
//				for (var i = 0; i < hospitalinfojc.length; i++) {
//					var hospitalCodejc = hospitalinfojc[i].hospitalCode;
//					var hospitalNamejc = hospitalinfojc[i].hospital;
//					var optionhospitaljc = new Option(hospitalNamejc,
//							hospitalCodejc);
//					document.getElementById("jcjg").options
//							.add(optionhospitaljc);
//				}
//				document.getElementById("jcjg").selectedIndex = -1;
//				var form = this.opener.opener.jcmodule.form.getForm();
//				Ext.getDom("jcjg").onchange = function() {
//					form.findField("JGMC").setValue(document
//							.getElementById('jcjg').options[document
//							.getElementById('jcjg').selectedIndex].text);
//					form.findField("JCJG").setValue(document
//							.getElementById("jcjg").value);
//					var jcfl = phis.script.rmi.miniJsonRequestSync({
//						serviceId : "referralService",
//						serviceAction : "getItemCategory",
//						body : {
//							"referralDate" : rq,
//							"hospitalCode" : document.getElementById("jcjg").value
//						}
//					});
//					if (jcfl.json == null || jcfl.json.body == null) {
//						MyMessageTip.msg("提示", "无法获取检查分类", true);
//						return;
//					}
//					var jcflinfo = jcfl.json.body;
//					var length = document.getElementById("lx").options.length;
//					for (var j = 0; j < length; j++) {
//						document.getElementById("lx").options.remove(j);
//					}
//					for (var i = 0; i < jcflinfo.length; i++) {
//						var classifyCode = jcflinfo[i].classifyCode;
//						var classifyName = jcflinfo[i].classifyName;
//						var optionjcfl = new Option(classifyName, classifyCode);
//						document.getElementById("lx").options.add(optionjcfl);
//					}
//					document.getElementById("lx").selectedIndex = -1;
//					var jcfx = phis.script.rmi.miniJsonRequestSync({
//						serviceId : "referralService",
//						serviceAction : "getCheckDirection",
//						body : {
//							"referralDate" : rq,
//							"hospitalCode" : document.getElementById("jcjg").value
//						}
//					});
//					var jcfxinfo = jcfx.json.body;
//					var jcfxtabRows = document.getElementById("jcfx").rows.length;
//					for (var i = 1; i < jcfxtabRows; i++) {
//						document.getElementById("jcfx").deleteRow(i);
//						jcfxtabRows = jcfxtabRows - 1;
//						i = i - 1;
//					}
//					for (var i = 0; i < jcfxinfo.length; i++) {
//						var jcfxtable = document.getElementById("jcfx");
//						var jcfxRow = jcfxtable.insertRow(i + 1); // 添加行
//						jcfxRow.id = jcfxinfo[i].directionCode;
//						var jcfxCell = jcfxRow.insertCell(); // 添加列
//						jcfxCell.align = "center";
//						jcfxCell.id = jcfxinfo[i].directionCode;
//						jcfxCell.innerHTML = jcfxinfo[i].directionName; // 添加数据
//						Ext.getDom(jcfxinfo[i].directionCode).onclick = function() {
//							var jcfxtrs = document.getElementById('jcfx')
//									.getElementsByTagName('tr');
//							for (var n = 0; n < jcfxtrs.length; n++) {
//								if (jcfxtrs[n] == this) {
//									jcfxtrs[n].style.backgroundColor = '#DFEBF2';
//									jcfxvalue = jcfxCell.id;
//								} else {
//									jcfxtrs[n].style.backgroundColor = '';
//								}
//							}
//						}
//					}
//					var jcbw = phis.script.rmi.miniJsonRequestSync({
//						serviceId : "referralService",
//						serviceAction : "getCheckPart",
//						body : {
//							"referralDate" : rq,
//							"hospitalCode" : document.getElementById("jcjg").value
//						}
//					});
//					var jcbwinfo = jcbw.json.body;
//					var jcbwtabRows = document.getElementById("jcbw").rows.length;
//					for (var i = 1; i < jcbwtabRows; i++) {
//						document.getElementById("jcbw").deleteRow(i);
//						jcbwtabRows = jcbwtabRows - 1;
//						i = i - 1;
//					}
//					for (var i = 0; i < jcbwinfo.length; i++) {
//						var jcbwtable = document.getElementById("jcbw");
//						var jcbwRow = jcbwtable.insertRow(i + 1); // 添加行
//						jcbwRow.id = jcbwinfo[i].partCode;
//						var jcbwCell = jcbwRow.insertCell(); // 添加列
//						jcbwCell.align = "center";
//						jcbwCell.id = jcbwinfo[i].partCode;
//						jcbwCell.innerHTML = jcbwinfo[i].partName; // 添加数据
//						Ext.getDom(jcbwinfo[i].partCode).onclick = function() {
//							var jcbwtrs = document.getElementById('jcbw')
//									.getElementsByTagName('tr');
//							for (var n = 0; n < jcbwtrs.length; n++) {
//								if (jcbwtrs[n] == this) {
//									jcbwtrs[n].style.backgroundColor = '#DFEBF2';
//									jcbwvalue = jcbwCell.id;
//									jcbwmcvalue = jcbwCell.innerHTML;
//								} else {
//									jcbwtrs[n].style.backgroundColor = '';
//								}
//							}
//						}
//					}
//				}
//				Ext.getDom("lx").onchange = function() {
//					var jcxm = phis.script.rmi.miniJsonRequestSync({
//						serviceId : "referralService",
//						serviceAction : "getCheckItems",
//						body : {
//							"referralDate" : rq,
//							"hospitalCode" : document.getElementById("jcjg").value,
//							"classifyCode" : document.getElementById("lx").value
//						}
//					});
//					var jcxminfo = jcxm.json.body;
//					var jcxmtabRows = document.getElementById("jcxm").rows.length;
//					for (var i = 1; i < jcxmtabRows; i++) {
//						document.getElementById("jcxm").deleteRow(i);
//						jcxmtabRows = jcxmtabRows - 1;
//						i = i - 1;
//					}
//					for (var i = 0; i < jcxminfo.length; i++) {
//						var jcxmtable = document.getElementById("jcxm");
//						var jcxmRow = jcxmtable.insertRow(i + 1); // 添加行
//						jcxmRow.id = jcxminfo[i].itemCode;
//						var jcxmCell = jcxmRow.insertCell(); // 添加列
//						jcxmCell.align = "center";
//						jcxmCell.id = jcxminfo[i].itemCode;
//						jcxmCell.innerHTML = jcxminfo[i].itemName; // 添加数据
//						Ext.getDom(jcxminfo[i].itemCode).onclick = function() {
//							var trs = document.getElementById('jcxm')
//									.getElementsByTagName('tr');
//							for (var n = 0; n < trs.length; n++) {
//								if (trs[n] == this) {
//									jcxmvalue = jcxmCell.id;
//									jcxmmcvalue = jcxmCell.innerHTML;
//									form.findField("XMMC")
//											.setValue(jcxmmcvalue);
//									form.findField("JCXM").setValue(jcxmvalue);
//									trs[n].style.backgroundColor = '#DFEBF2';
//									var sbxx = phis.script.rmi
//											.miniJsonRequestSync({
//												serviceId : "referralService",
//												serviceAction : "getMedicalStatus",
//												body : {
//													"referralDate" : rq,
//													"itemCode" : trs[n].id,
//													"CHAXUNLX" : "0",
//													"hospitalCode" : document
//															.getElementById("jcjg").value
//												}
//											});
//									var sbxxinfo = sbxx.json.body;
//									var yqtabRows = document
//											.getElementById("sbxx").rows.length;
//									for (var sbxx = 1; sbxx < yqtabRows; sbxx++) {
//										document.getElementById("sbxx")
//												.deleteRow(sbxx);
//										yqtabRows = yqtabRows - 1;
//										sbxx = sbxx - 1;
//									}
//									for (var sbxx = 0; sbxx < sbxxinfo.length; sbxx++) {
//										var sbxxtable = document
//												.getElementById("sbxx");
//										var sbxxRow = sbxxtable.insertRow(sbxx
//												+ 1); // 添加行
//										sbxxRow.id = sbxxinfo[sbxx].JIANCHASBDM;
//										var sbxxCell1 = sbxxRow.insertCell(); // 添加列
//										sbxxCell1.align = "center";
//										sbxxCell1.id = sbxxinfo[sbxx].JIANCHASBDM;
//										sbxxCell1.innerHTML = sbxxinfo[sbxx].JIANCHASBMC; // 添加数据
//										var sbxxCell2 = sbxxRow.insertCell(); // 添加列
//										sbxxCell2.align = "center";
//										// --------------
//										// sbxxCell2.id = sbxxinfo[sbxx].YUYUEH;
//										// sbxxCell2.innerHTML =
//										// sbxxinfo[sbxx].YUYUEH; // 添加数据
//										sbxxCell2.id = sbxx;
//										sbxxCell2.innerHTML = sbxx; // 添加数据
//										// --------------
//										var sbxxCell3 = sbxxRow.insertCell(); // 添加列
//										sbxxCell3.align = "center";
//										sbxxCell3.id = sbxxinfo[sbxx].YUYUEKSSJ
//												+ "-"
//												+ sbxxinfo[sbxx].YUYUEJSSJ;
//										sbxxCell3.innerHTML = sbxxinfo[sbxx].YUYUEKSSJ
//												+ "-"
//												+ sbxxinfo[sbxx].YUYUEJSSJ; // 添加数据
//										Ext.getDom(sbxxinfo[sbxx].JIANCHASBDM).onclick = function() {
//											var sbxxtrs = document
//													.getElementById('sbxx')
//													.getElementsByTagName('tr');
//											for (var n = 0; n < sbxxtrs.length; n++) {
//												if (sbxxtrs[n] == this) {
//													sbxxtrs[n].style.backgroundColor = '#DFEBF2';
//													jcyqvalue = sbxxCell1.id;
//													jcyqmcvalue = sbxxCell1.innerHTML;
//													yynovalue = sbxxCell2.id;
//													yysjvalue = sbxxCell3.id;
//												} else {
//													sbxxtrs[n].style.backgroundColor = '';
//												}
//											}
//										}
//									}
//								} else {
//									trs[n].style.backgroundColor = '';
//								}
//							}
//						}
//					}
//				}
			},
			doSave : function() {
				var form = this.opener.opener.jcmodule.form.getForm();
				var jgidvalue = form.findField("JCJG").getValue();
				if (jgidvalue == "" || jgidvalue == null) {
					MyMessageTip.msg("提示", "请选择检查机构", true);
					return;
				}
				var jgmcvalue = form.findField("JGMC").getValue();
//				var jclxvalue = document.getElementById("lx").value;
//				if (jclxvalue == "" || jclxvalue == null) {
//					MyMessageTip.msg("提示", "请选择检查类型", true);
//					return;
//				}
				var brid = this.brid;
				var mzhm = this.mzhm;
				var csny = this.csny;
				var brxb = this.brxb;
				var brzd = this.zzzd;
				var empiId = this.empiId
				var brtz = this.brxxform.findField("BRTZ").getValue();
				if (!brtz) {
					MyMessageTip.msg("提示", "病人体征不能为空", true);
					return;
				}
				if (!brzd) {
					MyMessageTip.msg("提示", "诊断不能为空", true);
					return;
				}
				var brtzmc = this.brxxform.findField("MSZD").getValue();
				// var jcsm = this.brxxform.findField("JCSM").getValue();
				// if (!jcsm) {
				// MyMessageTip.msg("提示", "检查说明不能为空", true);
				// return;
				// }
				var bqms = this.brxxform.findField("BQMS").getValue();
				if (!bqms) {
					MyMessageTip.msg("提示", "病情描述不能为空", true);
					return;
				}
				if (!this.selectjcpb) {
					MyMessageTip.msg("提示", "未选择检查排班", true);
					return;
				}
//				if (typeof(jcxmvalue) == "undefined" || !jcxmvalue
//						|| !jcxmmcvalue) {
//					MyMessageTip.msg("提示", "未选择检查项目", true);
//					return;
//				}
//				if (typeof(jcxmvalue) == "undefined" || !jcxmvalue
//						|| !jcxmmcvalue) {
//					MyMessageTip.msg("提示", "未选择检查项目", true);
//					return;
//				}
//				if (typeof(jcbwvalue) == "undefined" || !jcbwvalue
//						|| !jcbwmcvalue) {
//					MyMessageTip.msg("提示", "未选择检查部位", true);
//					return;
//				}
//				if (typeof(jcyqvalue) == "undefined" || !jcyqvalue
//						|| !jcyqmcvalue) {
//					MyMessageTip.msg("提示", "未选择检查仪器", true);
//					return;
//				}
//				if (typeof(jcfxvalue) == "undefined" || !jcfxvalue) {
//					MyMessageTip.msg("提示", "未选择检查方向", true);
//					return;
//				}

				var brxm = form.findField("BRXM").getValue();
				var sfzh = form.findField("SFZH").getValue();
				var nl = form.findField("NL").getValue();
				var lxdz = form.findField("LXDZ").getValue();
				var lxdh = form.findField("LXDH").getValue();
				var jcsm = form.findField("JCSM").getValue();
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				phis.script.rmi.jsonRequest({
							serviceId : "referralService",
							serviceAction : "saveSendExamine",
							body : {
								"JIUZHENKLX" :"3",
								"JIUZHENKH" : this.exContext.empiData.cardNo,
								"BINGRENXB" : brxb,
								"BINGRENCSRQ" : csny,
								"BINGRENNL" : nl,
								"BINGRENSFZH" : sfzh,
								"BINGRENLXDH" : lxdh,
								"BINGRENLXDZ" : lxdz,
								"BINGREMXM" : brxm,
								"SHENQINGJGDM" : "",//后台赋值
								"SHENQINGJGMC" : "",//后台赋值
								"SHENQINGYSMC" : "",//后台赋值sqysxm,
								"SHENQINGYSDM" : "",//后台赋值
								"BINGRENLB" : "1",
								"SONGJIANYS" : this.selectjcpb.departmentCode.text,//存本地,dr赋空值,
								"SONGJIANKS" : this.selectjcpb.departmentCode.key,
								"SONGJIANKSMC" : jgmcvalue,
								"SHOUFEISB" : "0",
								"YEWULX" : "1",
								"BINGQU" : "",
								"BINGCHUANGHAO" : "",
								"JIANCHAYUYUESJ" : rqvalue,
								"BINGQINGMS" : bqms,
								"ZHENDUAN" : brtzmc,
								"BINGRENTZ" : brtz,
								"QITAJC" : "",
								"BINGRENZS" : "",
								"JIANCHALY" : "2",
								"ZHUANRUYYDM" : jgidvalue,
								"JIANCHAXMBH" : this.selectjcpb.itemCode.key,
								"JIANCHAXMMC" : this.selectjcpb.itemCode.text,
								"JIANCHAFLBM" : "",
								"JIANCHASTBW" : "",
								"JIANCHAFXDM" : "",
								"JIANCHAZYDM" : "",
								"JIANCHATS" : jcsm,
								"EMPIID" : empiId,
								"MZHM" : mzhm
//								
//								"SONGJIANYS" : sqys,
//								"SHENQINGRQ" : new Date().format('Y-m-d'),
//								"SONGJIANKS" : this.mainApp.departmentId,
//								"SONGJIANKSMC" : this.mainApp.departmentName,
//								"JIANCHAYUYUESJ" : rqvalue,
//								"ZHUANRUYYDM" : jgidvalue,
//								"ZHUANRUYYMC" : jgmcvalue,
//								"BINGRENLB" : "3",
//								"BINGQINGMS" : bqms,
//								"ZHENDUAN" : brtzmc,
//								"QITAJC" : "",
//								"BINGRENZS" : bqms,
//								"JIANCHALY" : 2,
//								"JIESHOUFS" : 0,
//								"JIANCHASQDH" : "",
//								"JIANCHARQ" : rqvalue,
//								"JIANCHAKSDM" : "",
//								"JIANCHAKSMC" : "",
//								"BINGRENFPH" : "",
//								"BINGRENLX" : 1,
//								"BINGRENLXMC" : "",
//								"BINGRENKH" : 1,
//								"BINGRENMZH" : mzhm,
//								"BINGRENZYH" : "",
//								"BINGRENBQDM" : "",
//								"BINGRENBQMC" : "",
//								"BINGRENCWH" : "",
//								"SHENQINGYSGH" : sqys,
//								"SHENQINGYYDM" : sqjg,
//								"SHENQINGYYMC" : sqjgmc,
//								"JIANCHAXMDM" : this.selectjcpb.itemCode.key,//jcxmvalue,
//								"JIANCHAXMMC" : this.selectjcpb.itemCode.text,//jcxmmcvalue,
//								"JIANCHAXMLX" : "",//jclxvalue,
//								"JIANCHABWDM" : "",//jcbwvalue,
//								"JIANCHABWMC" : "",//jcbwmcvalue,
//								"YUYUERQ" : rqvalue,
//								"YUYUESJ" : "",//yysjvalue,
//								"YUYUEH" : this.selectjcpb.recordId,//yynovalue,
//								"JIANCHASBDM" : "",//jcyqvalue,
//								"JIANCHASBMC" : "",//jcyqmcvalue,
//								"JIANCHASBDD" : "",
//								"SHENFENZH" : sfzh,
//								"YUYUESF" : 1,
//								"YUYUEZT" : "1",
//								"JIANCHASQDBH" : "1",
//								"YINGXIANGFX" : "",//jcfxvalue,
//								"YEWULY" : "1",
								
							}
						}, function(code, msg, json) {
							this.panel.el.unmask()
							if (code > 300) {
								MyMessageTip.msg("提示", msg, true)
								return
							} else {
								MyMessageTip.msg("提示", "保存成功!", true);
								this.opener.opener.onClose();
								this.doPrint(json.JIANCHASQDH);
								if(this.opener.opener.opener != null){
									this.opener.opener.opener.refresh();
								}
							}
						}, this)// jsonRequest
			},
			doPrint : function(zhuanzhendh) {
				var pages = "phis.prints.jrxml.RegisterCheckReqOrder";
				var url = "resources/" + pages
						+ ".print?silentPrint=1&zhuanzhendh="
						+ zhuanzhendh;
				/*
				 * window .open( url, "", "height=" + (screen.height - 100) + ",
				 * width=" + (screen.width - 10) + ", top=0, left=0, toolbar=no,
				 * menubar=yes, scrollbars=yes, resizable=yes,location=no,
				 * status=no")
				 */
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