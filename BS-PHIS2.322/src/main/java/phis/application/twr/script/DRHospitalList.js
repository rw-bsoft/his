$package("phis.application.twr.script")

$import("phis.script.SelectList", "util.dictionary.DictionaryLoader")

phis.application.twr.script.DRHospitalList = function(cfg) {
	phis.application.twr.script.DRHospitalList.superclass.constructor.apply(
			this, [cfg])
//	var zrjgvalue = "";
//	var zrjgmcvalue = "";
//	var zrksvalue = "";
//	var zrksmcvalue = "";
//	var zyrqvalue = "";
}
Ext.extend(phis.application.twr.script.DRHospitalList, phis.script.SelectList,
		{
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							html : this.getFormHtmlTemplatezy(),
							autoScroll : true
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this);
				return panel;
			},
			getFormHtmlTemplatezy : function() {
				return '<html>'
						+ '<head>'
						+ '<style type="text/css">'
						+ ' #apDiv1zy {'
						+ 'position:absolute;'
						+ 'width:360px;'
						+ 'height:115px;'
						+ 'z-index:1;'
						+ '}'
						+ '#apDiv2zy {'
						+ 'position:absolute;'
						+ 'width:360px;'
						+ 'height:115px;'
						+ 'z-index:2;'
						+ 'left: 380px;'
						+ '}'
						+ '</style>'
						+ '</head>'
						+ '<body>'
						+ '<div id="apDiv1zy">'
						+ '<table width="360" border="1" id="zrjg">'
						+ '<tr>'
						+ '<td colspan="2"><div id="zrjgbtDiv" align="center">转入机构</div></td>'
						+ '</tr>' + '<tr>'
						+ '<td><div align="center">机构名称</div></td>'
						+ '<td><div align="center">代码</div></td>' + '</tr>'
						+ '</table>' + '</div>' + '<div id="apDiv2zy">'
						+ '<table width="360" border="1" id="zrks">' + '<tr>'
						+ '<td colspan="2"><div align="center">转入科室</div></td>'
						+ '</tr>' + '<tr>'
						+ '<td><div align="center">科室名称</div></td>'
						+ '<td><div align="center">代码</div></td>' + '</tr>'
						+ '</table>' + '</div>' + '</body>' + '</html>';
			},
			onReady : function() {
				this.showZYSQ(new Date(Date.parse(Date.getServerDate())));
			},
			doNew : function() {
				var zrkstabRows = document.getElementById("zrks").rows.length;
				for (var zrks = 2; zrks < zrkstabRows; zrks++) {
					document.getElementById("zrks").deleteRow(zrks);
					zrkstabRows = zrkstabRows - 1;
					zrks = zrks - 1;
				}
			},
			showZYSQ : function(rq) {
				var this_ = this;
				this.zyrqvalue = rq.toLocaleDateString();
				var Hospitalzr = phis.script.rmi.miniJsonRequestSync({
							serviceId : "referralService",
							serviceAction : "getClinicHospital",
							body : {
								"referralDate" : rq.toLocaleDateString()
							}
						});
				var hospitalinfozr = Hospitalzr.json.body;
				document.getElementById("zrjgbtDiv").innerHTML = "转入机构（"
						+ rq.toLocaleDateString() + "）";
				var zrjgtabRows = document.getElementById("zrjg").rows.length;
				for (var i = 2; i < zrjgtabRows; i++) {
					document.getElementById("zrjg").deleteRow(i);
					zrjgtabRows = zrjgtabRows - 1;
					i = i - 1;
				}
				for (var i = 0; i < hospitalinfozr.length; i++) {
					var zrjgtable = document.getElementById("zrjg");
					var zrjgRow = zrjgtable.insertRow(i + 2); // 添加行
					zrjgRow.id = hospitalinfozr[i].hospitalCode;
					var zrjgCell1 = zrjgRow.insertCell(); // 添加列
					zrjgCell1.align = "center";
					zrjgCell1.id = hospitalinfozr[i].hospitalCode;
					zrjgCell1.innerHTML = hospitalinfozr[i].hospital; // 添加数据
					var zrjgCell2 = zrjgRow.insertCell(); // 添加列
					zrjgCell2.align = "center";
					zrjgCell2.id = hospitalinfozr[i].hospitalCode;
					zrjgCell2.innerHTML = hospitalinfozr[i].hospitalCode; // 添加数据
					Ext.getDom(hospitalinfozr[i].hospitalCode).onclick = function() {
						var trszrjg = document.getElementById('zrjg')
								.getElementsByTagName('tr');
						for (var t = 0; t < trszrjg.length; t++) {
							if (trszrjg[t] == this) {
								trszrjg[t].style.backgroundColor = '#DFEBF2';
								this_.zrjgvalue = trszrjg[t].id;
								this_.zrjgmcvalue = this.getElementsByTagName('td')[0].innerHTML;
								this_.zrksvalue = "";
								this_.zrksmcvalue = "";
								var officezr = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : "referralService",
											serviceAction : "getHospitalDepartments",
											body : {
												"registerType" : "2",
												"referralDate" : rq
														.toLocaleDateString(),
												"registerSchedule" : "0",
												"registerCategory" : "1",
												"hospitalCode" : trszrjg[t].id
											}
										});
								var officeinfozr = officezr.json.body;
								var zrkstabRows = document
										.getElementById("zrks").rows.length;
								for (var zrks = 2; zrks < zrkstabRows; zrks++) {
									document.getElementById("zrks")
											.deleteRow(zrks);
									zrkstabRows = zrkstabRows - 1;
									zrks = zrks - 1;
								}
								for (var zrks = 0; zrks < officeinfozr.length; zrks++) {
									var zrkstable = document
											.getElementById("zrks");
									var zrksRow = zrkstable.insertRow(zrks + 2); // 添加行
									zrksRow.id = officeinfozr[zrks].KESHIDM
											+ "ks";
									var zrksCell1 = zrksRow.insertCell(); // 添加列
									zrksCell1.align = "center";
									zrksCell1.id = officeinfozr[zrks].KESHIDM;
									zrksCell1.innerHTML = officeinfozr[zrks].KESHIMC; // 添加数据
									var zrksCell2 = zrksRow.insertCell(); // 添加列
									zrksCell2.align = "center";
									zrksCell2.id = officeinfozr[zrks].KESHIDM;
									zrksCell2.innerHTML = officeinfozr[zrks].KESHIDM; // 添加数据
									Ext.getDom(officeinfozr[zrks].KESHIDM
											+ "ks").onclick = function() {
										var trszrks = document
												.getElementById('zrks')
												.getElementsByTagName('tr');
										for (var ks = 0; ks < trszrks.length; ks++) {
											if (trszrks[ks] == this) {
												trszrks[ks].style.backgroundColor = '#DFEBF2';
												this_.zrksvalue = this.id;
												this_.zrksmcvalue = this
														.getElementsByTagName('td')[0].innerHTML;
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
			},
			doSave : function() {
				var brid = this.brid;
				var mzhm = this.mzhm;
				var jzxh = this.jzxh;
				var csny = this.csny;
				var brxb = this.brxb;
				var zzzd = this.zzzd;
				var empiId = this.empiId;
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
				var zljg = this.brxxform.findField("ZLJG").getValue();
				if (!zljg) {
					MyMessageTip.msg("提示", "治疗经过不能为空", true);
					return;
				}
				var bqms = this.brxxform.findField("BQMS").getValue();
				if (!bqms) {
					MyMessageTip.msg("提示", "病情描述不能为空", true);
					return;
				}
				if (typeof(this.zrjgvalue) == "undefined" || !this.zrjgvalue
						|| !this.zrjgmcvalue) {
					MyMessageTip.msg("提示", "未选择机构", true);
					return;
				}
				if (typeof(this.zrksvalue) == "undefined" || !this.zrksvalue
						|| !this.zrksmcvalue) {
					MyMessageTip.msg("提示", "未选择科室", true);
					return;
				}
				var zysx = this.brxxform.findField("ZYSX").getValue();
				var sqys = this.brxxform.findField("SQYS").getValue();
				var jgdh = this.brxxform.findField("JGDH").getValue();
				var sqjg = this.brxxform.findField("SQJG").getValue();
				var sqjgmc = this.brxxform.findField("SQJG").getRawValue();
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				phis.script.rmi.jsonRequest({
							serviceId : "referralService",
							serviceAction : "saveZySendExchange",
							body : {
								"EMPIID" : empiId,
								"JIUZHENKLX" : "3",
								"JIUZHENKH" : "",
								"YIBAOKLX" : "",
								"YIBAOKXX" : "",
								"YEWULX" : "2",
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
								"SHENQINGRQ" : this.zyrqvalue,
								"ZHUANZHENYY" : zzyy,
								"ZHUANZHENZD" : zzzd,
								"BINQINGMS" : bqms,
								"ZHUANZHENZYSX" : zljg,
								"ZHUANRUYYDM" : this.zrjgvalue,
								"ZHUANRUYYMC" : this.zrjgmcvalue,
								"ZHUANRUKSDM" : this.zrksvalue,
								"ZHUANRUKSMC" : this.zrksmcvalue,
								"YISHENGDM" : 0,
								"YISHENGXM" : 0,
								"JIUZHENSJ" : 0,
								"GUAHAOBC" : 0,
								"GUAHAOXH" : 0,
								"YIZHOUPBID" : 0,
								"DANGTIANPBID" : 0,
								"JZXH" : jzxh
							}
						}, function(code, msg, json) {
							this.panel.el.unmask()
							if (code > 300) {
								MyMessageTip.msg("提示", "保存失败!", true)
								return
							} else {
								MyMessageTip.msg("提示", "保存成功!", true);
								this.opener.opener.onClose();
								this.doPrint(json.ZHUANZHENDH);
								if(this.opener.opener.opener != null){
									this.opener.opener.opener.refresh();
								}
							}
						}, this)// jsonRequest
			},
			doPrint : function(zhuanzhendh) {
				var pages = "phis.prints.jrxml.RegistHospitalReqOrder";
				var url = "resources/" + pages
						+ ".print?silentPrint=1&zhuanzhendh="
						+ zhuanzhendh;
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