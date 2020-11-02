$package("phis.application.ccl.script");
$import("phis.script.SelectList");

phis.application.ccl.script.CheckApplyFeeDetails_KD_WAR = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.printurl = util.helper.Helper.getUrl();
	phis.application.ccl.script.CheckApplyFeeDetails_KD_WAR.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyFeeDetails_KD_WAR,
		phis.script.SelectList, {
			doRemove : function() {
				if (this.getSelectedRecord() == undefined) {
					return;
				}
				delete this.selects[this.getSelectedRecord().id]
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
									this.selects={};
								}
							},
							scope : this
						});
			},
			doCommit : function() {
				var form = this.opener.opener.opener.midiModules["checkApplyForm"].form.getForm();
				if (this.store.getCount() == 0) {
					MyMessageTip.msg("提示", "提交失败，项目列表为空", true);
					return;
				}
//				if(form.findField("ZSXX").getValue().trim()==""){
//					MyMessageTip.msg("提示", "主诉信息不能为空", true);
//					return;
//				}
//				if(Ext.getCmp("sslx").getValue().inputValue == 1
//						&& form.findField("CTXX").getValue().trim()==""){
//					MyMessageTip.msg("提示", "体格检查不能为空", true);
//					return;
//				}
//				if(form.findField("XL").getValue()==""){
//					MyMessageTip.msg("提示", "心电图检查单心率不能为空", true);
//					return;
//				}
//				if(form.findField("XLV").getValue()==""){
//					MyMessageTip.msg("提示", "心电图检查单心律不能为空", true);
//					return;
//				}
				var list = [];
				var txm = this.getSelectedRecords()
				if (txm.length == 0) {
					MyMessageTip.msg("提示", "请选择收费项目！", true);
					return;
				}
				this.grid.getSelectionModel().clearSelections()
				for(var i=0;i<txm.length;i++){
					list.push(txm[i].data);
				}
				txm=[];
				Ext.Msg.show({
					title : '确认',
					msg : '确定提交吗？',
					modal : false,
					width : 200,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							//获得页面上的数据，为调接口做准备
							//var kdInfo = this.opener.opener.opener.kdInfo;
							var mainApp = this.opener.opener.opener.mainApp;
							var kdInfo = this.opener.opener.opener.exContext.empiData;
							var brid = kdInfo.BRID;//病人id
							var brxm = kdInfo.BRXM;//病人姓名
							var brxb = kdInfo.BRXB;//病人性别
							var brbq = kdInfo.BRBQ;//病人病区
							var brxb_text = kdInfo.BRXB_text;//病人性别_文本
							var birthday = kdInfo.CSNY;//生日
							var ksdm = kdInfo.BRKS;//科室代码
							var ksmc = kdInfo.BRKS_text;//科室名称
							var zyh = kdInfo.ZYH;//住院号
							var zrysdm = mainApp.uid;//主任医生代码
							var zrys = kdInfo.ZSYS_text;//主任医生
							var brch = kdInfo.BRCH;//病人床号
							var ryzd = form.findField("RYZD").getValue();
							var zsxx = form.findField("ZSXX").getValue();//主诉信息
							//var ctxx = form.findField("CTXX").getValue();//查体信息
							var xbs = form.findField("XBS").getValue();//现病史
							//检查录入加既往史， 过敏史， 体格检查，辅助检查
							var jws = form.findField("JWS").getValue();// 既往史信息
							var gms = form.findField("GMS").getValue();// 过敏史信息
							var tgjc = form.findField("TGJC").getValue();// 体格检查信息
							var fzjc = form.findField("FZJC").getValue();// 辅助检查信息
							//var syxx = form.findField("SYXX").getValue();//实验室和器材检查
							var bzxx = form.findField("BZXX").getValue();//备注信息
							//var xj = form.findField("XJ").getValue();//心界
							var xl = form.findField("XL").getValue();//心率
							//var xy = form.findField("XY").getValue();//心音
							var xlv = form.findField("XLV").getValue();//心律
							//var xlsj = form.findField("XLSJ").getValue();//心力衰竭
							//var xgjc = form.findField("XGJC").getValue();//X光检查
							if(xbs.length>74){
								Ext.MessageBox.alert("提示", "现病史最多能填 74 个汉字，当前字数 "+xbs.length+" 个！");
								return;
							}
							var res = util.rmi.miniJsonRequestSync({
										serviceId : "phis.checkApplyService",
										serviceAction : "commitCheckApplyProject_WAR",
										body : {
											list : list,
											brid : brid,
											brxm : brxm,
											brxb : brxb,
											brxb_text : brxb,
											brbq : brbq,
											birthday : birthday,
											ksdm : ksdm,
											ksmc : ksmc,
											ryzd : ryzd,
											zyh :zyh,
											zrysdm : zrysdm,
											zrys : zrys,
											brch : brch,
											zsxx : zsxx,
											//ctxx : ctxx,
											xbs : xbs,
											jws : jws,
											gms : gms,
											tgjc : tgjc,
											fzjc : fzjc,
											//syxx : syxx,
											bzxx : bzxx,
											//xj : xj,
											xl : xl,
											//xy : xy,
											xlv : xlv
											//xlsj : xlsj,
											//xgjc : xgjc
										}
									});
							if (res.code >= 300) {
								this.processReturnMsg(res.code, res.msg);
								return;
							}
							if(res.json && res.json.njjbmsg){
								alert("友情提示："+res.json.njjbmsg+"如有必要请删除！");
							}
							var pr = res.json.print;
							if (pr != null && pr.length > 0) {
								MyMessageTip.msg("提示", "提交成功!", true);	
								var mainApp = this.opener.opener.opener.mainApp;
								var kdInfo = this.opener.opener.opener.exContext.empiData;
								for (var i = 0; i < pr.length; i++) {
										var pri = pr[i];
										var sslx = pri.SSLB;
										var sqdh = pri.SQDH;
										var brid = kdInfo.BRID;//病人id
										var age = kdInfo.AGE;
										var zyh = kdInfo.ZYH;
										var zyhm = kdInfo.ZYHM;
										var brch = kdInfo.BRCH;
										var brksdm = kdInfo.BRKS;
										var zrysdm = mainApp.uid;
										var yllb = 2;// 住院
										var url = this.printurl + "resources/";
										// 根据所属类型跳到指定打印界面
										if (sslx == 1) {
											url += "phis.prints.jrxml.CheckApplyBillForECG.print?";
										} else if (sslx == 2) {
											url += "phis.prints.jrxml.CheckApplyBillForRAD.print?";
										} else if (sslx == 3) {
											url += "phis.prints.jrxml.CheckApplyBillForBC.print?";
										}else if (sslx == 4) {
											url += "phis.prints.jrxml.CheckApplyBillForWCJ.print?";
										}
										url += "&execJs=" + this.getExecJs() + "&brid=" + brid
											+ "&sqdh=" + sqdh + "&age=" + age + "&yllb=" + yllb
											+ "&zyh=" + zyh + "&zyhm=" + zyhm + "&brch=" + brch
											+ "&brksdm=" + brksdm + "&zrysdm=" + zrysdm;
										var LODOP = getLodop();
										LODOP.PRINT_INIT("打印控件");
										LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
										LODOP.ADD_PRINT_HTM(50, 10, "100%", "100%",
												util.rmi.loadXML({
													url : url,
													httpMethod : "get"
												}));
										LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
												"Full-Width");
										LODOP.ADD_PRINT_BARCODE(80, "70%", 138, 38,
												"128Auto", kdInfo.ZYHM);
										// 预览
										LODOP.PREVIEW();
								}
							} else {
								MyMessageTip.msg("提示", "提交失败!没有选中项目", true);
							}
							this.clear();
							this.opener.midiModules["checkPointList"].store.removeAll();
							this.opener.midiModules["checkProjectList"].store.removeAll();
						}
					},
					scope : this
				});
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			}
			
		});