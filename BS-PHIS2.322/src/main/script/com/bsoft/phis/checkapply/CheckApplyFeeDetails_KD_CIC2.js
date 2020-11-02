$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleList");

com.bsoft.phis.checkapply.CheckApplyFeeDetails_KD_CIC2 = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	com.bsoft.phis.checkapply.CheckApplyFeeDetails_KD_CIC2.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyFeeDetails_KD_CIC2,
		com.bsoft.phis.SimpleList, {
			doRemove : function() {
				if (this.getSelectedRecord() == undefined) {
					return;
				}
				this.store.remove(this.getSelectedRecord());
			},
			doRenew : function() {
				Ext.Msg.show({
							title : '确认',
							msg : '确定要清空已添加的项目么？',
							modal : false,
							width : 200,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									this.clear();
								}
							},
							scope : this
						});
			},
			doCommit : function() {
				var form = this.opener.opener.opener.midiModules["checkApplyForm"].form.getForm();
				if(form.findField("LCZD").getValue().trim()==""){
					MyMessageTip.msg("提示", "请先录入临床诊断", true);
					return;
				}
				if (this.store.getCount() == 0) {
					MyMessageTip.msg("提示", "提交失败，项目列表为空", true);
					return;
				}
				if(form.findField("ZSXX").getValue().trim()==""){
					MyMessageTip.msg("提示", "主诉信息不能为空", true);
					return;
				}
				if(form.findField("CTXX").getValue().trim()==""){
					MyMessageTip.msg("提示", "查体信息不能为空", true);
					return;
				}
				if(Ext.getCmp("sslx2").getValue().inputValue==1&&form.findField("XL").getValue()==""){
					MyMessageTip.msg("提示", "心电图检查单心率不能为空", true);
					return;
				}
				if(Ext.getCmp("sslx2").getValue().inputValue==1&&form.findField("XLV").getValue()==""){
					MyMessageTip.msg("提示", "心电图检查单心律不能为空", true);
					return;
				}
				var list = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					list.push(this.store.data.items[i].data)
				}
				Ext.Msg.show({
					title : '确认',
					msg : '确定提交么？',
					modal : false,
					width : 200,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							//获得页面上的数据，为调接口做准备
							var mainApp = this.opener.opener.opener.mainApp;
							var empiData = this.opener.opener.opener.exContext.empiData;
							var ids = this.opener.opener.opener.exContext.ids;
							var mzhm = empiData.MZHM;//门诊号码
							var brid = ids.brid;//病人id
							var clinicId = ids.clinicId;//就诊序号
							var brxm = empiData.personName;//病人姓名
							var brxb = empiData.sexCode;//病人性别
							var brxb_text = empiData.sexCode_text;//病人性别_文本
							var birthday = empiData.birthday;//生日
							var address = empiData.address;//联系地址
							var phoneNumber = empiData.phoneNumber;//联系方式
							var ysdm = mainApp.uid;//申请医生代码
							var ysxm = mainApp.uname;//医生姓名
							var ksdm = mainApp.departmentId;//科室代码
							var ksmc = mainApp.departmentName;//科室名称
							var lczd = form.findField("LCZD").getValue();//诊断
							var zsxx = form.findField("ZSXX").getValue();//主诉信息
							var ctxx = form.findField("CTXX").getValue();//查体信息
							var syxx = form.findField("SYXX").getValue();//实验室和器材检查
							var bzxx = form.findField("BZXX").getValue();//备注信息
							var xj = form.findField("XJ").getValue();//心界
							var xl = form.findField("XL").getValue();//心率
							var xy = form.findField("XY").getValue();//心音
							var xlv = form.findField("XLV").getValue();//心律
							var xlsj = form.findField("XLSJ").getValue();//心力衰竭
							var xgjc = form.findField("XGJC").getValue();//X光检查
							var res = util.rmi.miniJsonRequestSync({
										serviceId : "checkApplyService",
										serviceAction : "commitCheckApplyProject_CIC",
										body : {
											list : list,
											mzhm : mzhm,
											brid : brid,
											brxm : brxm,
											clinicId : clinicId,
											brxb : brxb,
											brxb_text : brxb,
											birthday : birthday,
											address : address,
											phoneNumber : phoneNumber,
											ysdm : ysdm,
											ysxm : ysxm,
											ksdm : ksdm,
											ksmc : ksmc,
											lczd : lczd,
											zsxx : zsxx,
											ctxx : ctxx,
											syxx : syxx,
											bzxx : bzxx,
											xj : xj,
											xl : xl,
											xy : xy,
											xlv : xlv,
											xlsj : xlsj,
											xgjc : xgjc
										}
									});
							if (res.code >= 300) {
								this.processReturnMsg(res.code, res.msg);
								return;
							}
							MyMessageTip.msg("提示", "提交成功!", true);
							var pr=res.json.print;
							if(pr!=null&&pr.length>0){
							for(var i=0;i<pr.length;i++){
							var pri=pr[i];
							var sslx = pri.SSLB;
							var sqdh = pri.SQDH;
							var brid = this.opener.opener.opener.exContext.ids.brid;
							var age = this.opener.opener.opener.exContext.ids.age;
							var yllb = 1;//门诊
							var url = this.printurl+".print?pages=";
							// 根据所属类型跳到指定打印界面
							if (sslx == 1) {
								url+="CheckApplyBillForECG";
							} else if (sslx == 2) {
								url+="CheckApplyBillForRAD";
							} else if (sslx == 3) {
								url+="CheckApplyBillForBC";
							}
							url += "&execJs=jsPrintSetup.setPrinter('rb');&brid="+brid+"&sqdh="+sqdh+"&age="+age+"&yllb="+yllb;
								var new_win=window
										.open(
												url,
												"",
												"height="
														+ (screen.height - 100)
														+ ", width="
														+ (screen.width - 10)
														+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
								}
							}
							new_win.onload=function(){
									new_win.print();
									new_win.close();
								}
							this.initClinicRecord();
							this.clear();
							this.opener.midiModules["checkPointList"].store.removeAll();
							this.opener.midiModules["checkProjectList"].store.removeAll();
						}
					},
					scope : this
				});
			},
			initClinicRecord : function() {
				// 载入病历信息
				util.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "loadClinicInfo",
							body : {
								"brid" : this.opener.opener.opener.exContext.ids.brid,
								"clinicId" : this.opener.opener.opener.exContext.ids.clinicId,
								"type" : "3"
							}
						}, function(code, msg, json) {
							if (code < 300) {
								var measures = json.measures;
								var disposal = json.disposal;
								document.getElementById("measuresDiv").innerHTML = this
										.getMeasuresHtml(measures, disposal);
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},
			getMeasuresHtml : function(measures, disposal) {
				var html = '<table cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
				var num = 1;
				var isHerbs = false;
				if (measures != null && measures.length > 0) {
					for (var i = 0; i < measures.length; i++) {
						var r = measures[i];
						html += "<tr>";
						if (num == 1) {
							if (r.TYPE == 3) {
								isHerbs = true;
							}
							html += '<td>' + num + '.</td>';
							num++;
						} else {
							if (r.TYPE == 3 && !isHerbs) {
								html += '<td>' + num + '.</td>';
								num++;
								isHerbs = true;
							} else {
								html += '<td>&nbsp;</td>';
							}
						}
						if (r.TYPE == "3") {
							html += '<td>' + r.YPMC + '</td><td colspan="6">'
									+ r.YPSL + r.YFDW + '</td></tr>';
							// 判断是否需要添加尾部信息
							if (i + 1 >= measures.length
									|| r.CFSB != measures[i + 1].CFSB
									|| r.YPZH != measures[i + 1].YPZH) {
								html += '<td colspan="7" align="center">帖数：'
										+ r.CFTS
										+ '&nbsp;&nbsp;&nbsp;&nbsp;用法：'
										+ r.GYTJ_text
										// + '&nbsp;&nbsp;&nbsp;&nbsp;服法：'
										// + r.YPZS_text
										+ '&nbsp;&nbsp;&nbsp;&nbsp;'
										+ r.YPYF_text + '</td>';
							}
						} else {
							html += '<td>' + r.YPMC;
							if (r.YFGG) {
								html += "/" + r.YFGG + '&nbsp;';
							}
							// 判断是否皮试
							if (r.PSPB == 1) {

								if (r.PSJG) {
									var psjg_text = "<font color='#FFFA4C'>未知</font>";
									if (r.PSJG == 1) {
										psjg_text = "<font color='red'>阳性</font>";
									} else if (r.PSJG == -1) {
										psjg_text = "<font color='green'>阴性</font>";
									}
									html += ' (' + psjg_text + ')';
								} else {
									html += '<img id="jzxh_'
											+ r.SBXH
											+ '" src="resources/css/app/biz/images/pi.png" width="21" height="21"  style="cursor:pointer;" onClick="openSkinTestWin('
											+ r.SBXH + ',' + r.YPXH + ')"/>';
								}
								html += '</td>';
							}
							html += '<td>' + r.YPYF_text + '</td>' + '<td>'
									+ r.YCJL + (r.JLDW ? r.JLDW : '') + '</td>'
									+ '<td>' + r.YPSL + (r.YFDW ? r.YFDW : '')
									+ '</td>' + '<td>' + r.GYTJ_text + '</td>';
							html += '<td>&nbsp</td></tr>';
						}
					}
				}
				if (disposal != null && disposal.length > 0) {
					for (var i = 0; i < disposal.length; i++) {
						var r = disposal[i];
						html += "<tr>";
						html += '<td>' + num + '.</td>';
						html += '<td>' + r.FYMC + '</td>' + '<td colspan="6">'
								+ r.YLSL + r.FYDW + '</td>';
						html += "</tr>";
						num++;
					}
				}
				html += '</table>';
				return html;
			}
		});