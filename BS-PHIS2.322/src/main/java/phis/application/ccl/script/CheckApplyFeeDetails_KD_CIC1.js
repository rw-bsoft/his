$package("phis.application.ccl.script");
$import("phis.script.EditorList", "phis.script.util.DateUtil");

phis.application.ccl.script.CheckApplyFeeDetails_KD_CIC1 = function(cfg) {
	cfg.printurl = util.helper.Helper.getUrl();
	cfg.autoLoadData = false;
	// cfg.disablePagingTbr = true;
	phis.application.ccl.script.CheckApplyFeeDetails_KD_CIC1.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyFeeDetails_KD_CIC1,
		phis.script.EditorList, {
			doRemove : function() {
				if (this.getSelectedRecord() == undefined) {
					return;
				}
				// delete this.selects[this.getSelectedRecord().id]
				this.store.remove(this.getSelectedRecord());

			},
			showColor : function(value, metaData, r, row, col) {
				if(value==undefined){
					return "";
				}
				return "<font style='color:red;font-weight:bold'>"+value+"</font>";
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
							this.selects = {};
						}
					},
					scope : this
				});
			},
		//wy 智能提醒
		initHtmlElement: function () {
			var html = '<OBJECT id="DemoActiveX"  type="application/x-itst-activex" style="border:0px;width:0px;height:0px;" clsid="{354C50F1-89F5-4728-B041-76C6F13FFFDE}" codebase="DatamatrixDevice.cab"></OBJECT>'
			var node = document.getElementById("DemoActiveX");
			if (node) {
				node.parentNode.removeChild(node);
			}
			var ele = document.createElement("div");
			ele.setAttribute("width", "0px")
			ele.setAttribute("height", "0px")
			ele.innerHTML = html;
			document.body.appendChild(ele);
		},
			doCommit : function() {
				//wy 智能提醒
				try {
					this.initHtmlElement();
				}catch (e) {

				}
				var form = this.opener.opener.opener.midiModules["checkApplyForm"].form
						.getForm();
				var jgid = this.mainApp['phis'].phisApp.deptId;
				jgid = jgid.substring(jgid.length - 2, jgid.length);
				/*
				 * if(form.findField("LCZD").getValue().trim()==""){
				 * MyMessageTip.msg("提示", "请先录入临床诊断", true); return; }
				 */
				if (this.store.getCount() == 0) {
					MyMessageTip.msg("提示", "提交失败，项目列表为空", true);
					return;
				}
				/*
				 * if(form.findField("ZSXX").getValue().trim()==""){
				 * MyMessageTip.msg("提示", "主诉信息不能为空", true); return; }
				 */
				// if(form.findField("CTXX").getValue().trim()==""){
				// MyMessageTip.msg("提示", "查体信息不能为空", true);
				// return;
				// }
				if (Ext.getCmp("sslx1").getValue().inputValue == 1
						&& form.findField("XL").getValue() == "") {
					MyMessageTip.msg("提示", "心电图检查单心率不能为空", true);
					return;
				}
				if (Ext.getCmp("sslx1").getValue().inputValue == 1
						&& form.findField("XLV").getValue() == "") {
					MyMessageTip.msg("提示", "心电图检查单心律不能为空", true);
					return;
				}
				var list = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					list.push(this.store.data.items[i].data);
				}				
				if (this.opener.opener.opener.exContext.empiData.idCard != ""
						&& this.store.getCount() > 0) {
					// 获取系统参数“是否开启重复检查提醒 QYCFJCTXBZ“
					var params = this.loadSystemParams({
						"privates" : ['QYCFJCTXBZ']
					})
					if (params.QYCFJCTXBZ == "1") {
						var infos = "";
							// 获取系统参数“重复检查判定周期”
							params = this.loadSystemParams({
								"privates" : ['CFJCPDZQ']
							})
							this.mainApp['phisApp'].cfjcpdzq = (params.CFJCPDZQ != null
									&& params.CFJCPDZQ != "null"
									&& params.CFJCPDZQ != "")?params.CFJCPDZQ:"15";
						// ****************************************************浦口：调用大数据健康档案浏览器接口服务校验 zhaojian 2017-11-15*****************************************************//
						if (this.mainApp['phisApp'].deptId.indexOf("320124") == 0) {
							for (var i = 0; i < this.store.getCount(); i++) {
								// 检查信息，可以有多组检查信息，每组检查信息以英文分号“;”区分，检查信息包括检查名称、医疗序号，互相之间以英文“,”区分，检查信息中为空的属性需要预留位置
								infos += this.store.data.items[i].data.FYMC+ ',';// 暂未使用系统参数控制使用哪种属性进行配置，后续可扩展
							}
							if(infos.indexOf("DR")>=0){
								infos="DR,;CR,;X-ray,;CT,;DX,;";
							}
							else if(infos.indexOf("B超")>=0 || infos.indexOf("超声")>=0){
								infos="超声,;UIS,;";
							}
							var start_date = Date.getDateTimeAfter("-"
										+ this.mainApp['phisApp'].cfjcpdzq);
							var end_date = Date.getServerDateTime();
							var params_array = [{
								name : "idcard",
								value : this.opener.opener.opener.exContext.empiData.idCard.replace(/(^\s*)|(\s*$)/g, "")
									}, {
										name : "start_date",
										value : start_date
									}, {
										name : "end_date",
										value : end_date
									}, {
										name : "infos",
										value : infos
									}, {
										name : "sys_organ_code",
										value : this.mainApp['phisApp'].deptId
									}, {
										name : "sys_code",
										value : "his"
									}, {
										name : "opeCode",
										value : this.mainApp.uid
									}, {
										name : "opeName",
										value : this.mainApp.uname
									}];
							util.rmi.jsonRequest({
								serviceId : "chis.desedeService",
								schema : "",
								serviceAction : "getDesInfo",
								method : "execute",
								params : JSON.stringify(params_array)
							}, function(code, msg, json) {
								if (msg == "Success") {
									var res = this.getEHRView(json,
											"getRepeatExaminationRecord", "2");
								}
							}, this)
						}
						// ****************************************************溧水：使用HIS库校验zhaojian2017-11-16*****************************************************//
						else if (this.mainApp['phisApp'].deptId.indexOf("320124") == 0) {
							for (var i = 0; i < this.store.getCount(); i++) {
								// 药品信息，可以有多组药品信息，每组药品信息以英文分号“;”区分，药品信息包括通用名、药品序号、规格、药品产地，互相之间以英文“,”区分，药品信息中为空的属性需要预留位置
								infos += "'" + this.store.data.items[i].data.FYXH+ "',";// 暂未使用系统参数控制使用哪种属性进行配置，后续可扩展
							}
							var body = {};
							body["SFZH"] = this.opener.opener.opener.exContext.empiData.idCard;
							body["BRID"] = this.opener.opener.opener.exContext.empiData.BRID;
							body["YPXH"] = "";
							body["YLXH"] = infos.substr(0, infos.length - 1);
							var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "checkYpsy",
								body : body
							});
							if (!(res.code > 300)) {
								var data = res.json.body;
								var cfyp = "";
								for (var i = 0; i < data.length; i++) {
									cfyp += (i + 1) + ". " + data[i][0] + "，"
											+ data[i][1] + "，" + data[i][2] + "<br>";
								}
								if (cfyp != "") {
									MyMessageTip.msg("该病人"+this.mainApp['phisApp'].cfjcpdzq+"天内已做过如下检查：", cfyp, true);
								}
							}
						}
					}
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
							// 获得页面上的数据，为调接口做准备
							var mainApp = this.opener.opener.opener.mainApp;
							var empiData = this.opener.opener.opener.exContext.empiData;
							var ids = this.opener.opener.opener.exContext.ids;
							var mzhm = empiData.MZHM;// 门诊号码
							var brid = ids.brid;// 病人id
							var clinicId = ids.clinicId;// 就诊序号
							var brxm = empiData.personName;// 病人姓名
							var brxb = empiData.sexCode;// 病人性别
							var brxb_text = empiData.sexCode_text;// 病人性别_文本
							var birthday = empiData.birthday;// 生日
							var address = empiData.address;// 联系地址
							var phoneNumber = empiData.phoneNumber;// 联系方式
							var ysdm = mainApp.uid;// 申请医生代码
							var ysxm = mainApp.uname;// 医生姓名
							var ksdm = mainApp.departmentId;// 科室代码
							var ksmc = mainApp.departmentName;// 科室名称
							var lczd = form.findField("LCZD").getValue();// 诊断
							var bzxx = form.findField("BZXX").getValue();// 备注信息
							var xl = form.findField("XL").getValue();// 心率
							var xlv = form.findField("XLV").getValue();// 心律
							var zsxx = form.findField("ZSXX").getValue();// 主诉信息
							var xbs = form.findField("XBS").getValue();// 现病史信息
							var jws = form.findField("JWS").getValue();// 既往史信息
							var gms = form.findField("GMS").getValue();// 过敏史信息
							var tgjc = form.findField("TGJC").getValue();// 体格检查信息
							var fzjc = form.findField("FZJC").getValue();// 辅助检查信息
							//var t = form.findField("TW").getValue();// 体温
							//var r = form.findField("HXPL").getValue();// 呼吸频率
							//var p = form.findField("MB").getValue()// 脉搏
							//var ssy = form.findField("SSY").getValue();// 收缩压
							//var szy = form.findField("SZY").getValue();// 舒张压
							//var height = document.getElementById("H").value;// 身高
							//var weight = document.getElementById("W").value;// 体重
							//var tgjc = Ext.getCmp("TGJC").getValue();// 体格检查
							//var ctxx = "T:" + t + "℃    P:" + p + "次/分   R:"
								//	+ r + "次/分    BP:" + ssy + " / " + szy
								//	+ "mmHg" + "   " + tgjc;
							// var ctxx =
							// form.findField("CTXX").getValue();//查体信息
							//var syxx = form.findField("SYXX").getValue();// 实验室和器材检查
							// var xj = form.findField("XJ").getValue();//心界
							// var xy = form.findField("XY").getValue();//心音
							// var xlsj =
							// form.findField("XLSJ").getValue();//心力衰竭
							// // var xgjc =
							// form.findField("XGJC").getValue();//X光检查
							// if(xbs == null || xbs==""){
							// MyMessageTip.msg("提示", "没有现病史不能提交检查单", true);
							// return;
							//								
							// }
							//智能提醒
							try {
								var obj=document.getElementById("DemoActiveX");
								var ip=obj.GetIpAddressAndHostname().split(",")[0];//支付终端IP
								var restx = util.rmi.miniJsonRequestSync({
									serviceId: "phis.checkApplyService",
									serviceAction: "SendMsg",
									body: {
										list: list,
										kh: this.opener.opener.opener.exContext.empiData.idCard,
										manageUnit: this.mainApp.deptId,
										YYKSBM: mainApp.departmentId,
										YYYSGH: mainApp.uid,
										YSXM: mainApp.uname,
										AGENTIP: ip

									}
								});
							}catch (e) {
								console.log(e);
							}

							var res = util.rmi.miniJsonRequestSync({
								serviceId : "phis.checkApplyService",
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
									xl : xl,
									xlv : xlv,
									zsxx : zsxx,
									xbs : xbs,
									jws : jws,
									gms : gms,
									tgjc : tgjc,
									fzjc : fzjc,
									bzxx : bzxx
									//ctxx : ctxx,
								//	syxx : syxx,
								//	xj : " ",
								//	xy : " ",	
								//	xlsj : " ",
								//	xgjc : " "
								}
							});
							list = [];
							if (res.code >= 300) {
								// MyMessageTip.msg("提示", "当前没有设置医技科室!",
								// true);
								this.processReturnMsg(res.code, res.msg);
								return;
							}
							if(res.json && res.json.njjbmsg){
								alert("友情提示："+res.json.njjbmsg+"如有必要请删除！");
							}
							var pr = res.json.print;
							if (pr != null && pr.length > 0) {
								MyMessageTip.msg("提示", "提交成功!", true);
								for (var i = 0; i < pr.length; i++) {
									var pri = pr[i];
									var sslx = pri.SSLB;
									var sqdh = pri.SQDH;
									var brid = this.opener.opener.opener.exContext.ids.brid;
									var age = this.opener.opener.opener.exContext.ids.age;
									var yllb = 1;// 门诊
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
									}// http://localhost:8080/phis/resources/phis.prints.jrxml.CheckApplyBillForECG.print?execJs=jsPrintSetup.setPrinter%28%27rb%27%29;&brid=1&sqdh=3972&age=21&yllb=1
									url += "execJs=jsPrintSetup.setPrinter('rb')&brid="
											+ brid
											+ "&sqdh="
											+ sqdh
											+ "&age="
											+ age + "&yllb=" + yllb;
									var LODOP = getLodop();
									LODOP.PRINT_INIT("打印控件");
									// LODOP.SET_PRINT_PAGESIZE("1", "148mm",
									// "210mm", "");
									//LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
									LODOP.SET_PRINT_PAGESIZE("0", "", "", "A5");
									LODOP.ADD_PRINT_HTM(50, 10, "100%", "100%",
											util.rmi.loadXML({
												url : url,
												httpMethod : "get"
											}));
									LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
											"Full-Width");
									LODOP.ADD_PRINT_BARCODE(80, "65.5%", 138,
											38, "128Auto", mzhm);
									// 直接打印
		                             LODOP.PRINT();
								}
							} else { 
								MyMessageTip.msg("提示", "提交失败!没有选中项目", true);
							}
							this.initClinicRecord();
							this.clear();
							this.opener.midiModules["checkPointList"].store
									.removeAll();
							this.opener.midiModules["checkProjectList"].store
									.removeAll();
						}
					},
					scope : this
				});
				return;
			},
			/**
			 * 知情同意书打印
			 */

			doPrint : function() {
				var jgid = this.mainApp['phis'].phisApp.deptId;
				if (jgid == '33010470') {
					var wins = new Ext.Window({
						title : "打印格式选择",
						width : 380,
						height : 90,
						closeAction : 'hide',
						// layout:"form",
						// labelWidth:45,
						resizable : false,
						plain : true,
						// bodyStyle:"padding:3px",
						// defaults:{xtype:"textfield",width:180},

						items : [{
							xtype : 'radiogroup',
							id : 'gs',
							fieldLabel : '打印格式',
							items : [{
								boxLabel : '常规产科同意',
								name : 'gs',
								inputValue : '1',
								checked : true
							}, {
								boxLabel : '知情同意书',
								name : 'gs',
								inputValue : '2'
							}, {
								boxLabel : 'NT告知书',
								name : 'gs',
								inputValue : '3'
							}]
						}],
						buttons : [{
							text : "确定",
							handler : function() {
								var LODOP = getLodop();
								LODOP.PRINT_INIT("特检打印");
								var gs = Ext.getCmp('gs').getValue().inputValue;
								var fileList = [];
								var queryFile = {
									intOrient : "1",
									PageWidth : "210mm",
									PageHeight : "140mm"
								}
								$FSActiveXObjectUtils.initHtmlElement();// 初始化
								var fsoObj = $FSActiveXObjectUtils.getObject();
								// var pages = "phis.prints.jrxml.cgckcsjczqty";
								if (gs == 1) {
									pages = "phis.prints.jrxml.cgckcsjczqty_New";
								} else if (gs == 2) {
									pages = "phis.prints.jrxml.zqtys";
								} else {
									pages = "phis.prints.jrxml.zqtys_NT";
								}
								var url = "resources/" + pages
										+ ".print?silentPrint=1";
								try {
									var reader = fsoObj.openTextFile(
											"C:\\YSDY.txt", 1);
									while (!reader.AtEndofStream) {
										var content = reader.readline();
										if (content.indexOf('#') >= 0) {
											continue;
										}
										fileList.push(content.split("="));
									}
									reader.close();
									for (var i = 0; i < fileList.length; i++) {
										for (var key in queryFile) {
											if (fileList[i][0] == key) {
												queryFile[key] = fileList[i][1]
											}
										}
									}
									var lodopLeft = "0";
									var PRINT_PAGE_PERCENT = "Full-Page";
									if (gs == 1) {
										lodopLeft = "0";
									} else {
										lodopLeft = "-36mm";
									}
									if (queryFile.intOrient == 1) {
										lodopLeft = "0";
									}
									if (queryFile.intOrient == 3) {
										queryFile.intOrient = 1;
										lodopLeft = "31mm"
										PRINT_PAGE_PERCENT = "Full-Width";
									}
									// if(gs==3){
									// LODOP.SET_PRINT_PAGESIZE(2,0,0,"A4");
									// LODOP.ADD_PRINT_HTM("0","50%","50%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
									// LODOP.SET_PRINT_MODE
									// ("PRINT_PAGE_PERCENT","Full-Width");
									// LODOP.PRINT();
									// }else{
									LODOP.SET_PRINT_PAGESIZE(
											queryFile.intOrient,
											queryFile.PageWidth,
											queryFile.PageHeight, "");
									LODOP.ADD_PRINT_HTM("0", lodopLeft,
											queryFile.PageWidth, "100%",
											util.rmi.loadXML({
												url : url,
												httpMethod : "get"
											}));
									LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
											PRINT_PAGE_PERCENT);
									LODOP.SET_PRINTER_INDEX(-1);
									LODOP.PRINT();
									// }
								} catch (e) {

									LODOP.SET_PRINT_PAGESIZE(2, 0, 0, "A4");
									LODOP.ADD_PRINT_HTM("0", "50%", "50%",
											"100%", util.rmi.loadXML({
												url : url,
												httpMethod : "get"
											}));
									LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
											"Full-Width");

									// 直接打印
									LODOP.PRINT();
								}

							}
						}]
					});
				} else {
					var wins = new Ext.Window({
						title : "打印格式选择",
						width : 280,
						height : 90,
						closeAction : 'hide',
						// layout:"form",
						// labelWidth:45,
						resizable : false,
						plain : true,
						// bodyStyle:"padding:3px",
						// defaults:{xtype:"textfield",width:180},

						items : [{
							xtype : 'radiogroup',
							id : 'gs',
							fieldLabel : '打印格式',
							items : [{
								boxLabel : '常规产科同意',
								name : 'gs',
								inputValue : '1',
								checked : true
							}, {
								boxLabel : '知情同意书',
								name : 'gs',
								inputValue : '2'
							}]
						}],
						buttons : [{
							text : "确定",
							handler : function() {
								var LODOP = getLodop();
								LODOP.PRINT_INIT("特检打印");
								var gs = Ext.getCmp('gs').getValue().inputValue;
								var fileList = [];
								var queryFile = {
									intOrient : "1",
									PageWidth : "210mm",
									PageHeight : "140mm"
								}
								$FSActiveXObjectUtils.initHtmlElement();// 初始化
								var fsoObj = $FSActiveXObjectUtils.getObject();
								var pages = "phis.prints.jrxml.cgckcsjczqty";
								if (gs == 1) {
									pages = "phis.prints.jrxml.cgckcsjczqty";
								} else {
									pages = "phis.prints.jrxml.zqtys";
								}
								var url = "resources/" + pages
										+ ".print?silentPrint=1";
								try {
									var reader = fsoObj.openTextFile(
											"C:\\YSDY.txt", 1);
									while (!reader.AtEndofStream) {
										var content = reader.readline();
										if (content.indexOf('#') >= 0) {
											continue;
										}
										fileList.push(content.split("="));
									}
									reader.close();
									for (var i = 0; i < fileList.length; i++) {
										for (var key in queryFile) {
											if (fileList[i][0] == key) {
												queryFile[key] = fileList[i][1]
											}
										}
									}
									var lodopLeft = "0";
									var PRINT_PAGE_PERCENT = "Full-Page";
									if (gs == 1) {
										lodopLeft = "0";
									} else {
										lodopLeft = "-36mm";
									}
									if (queryFile.intOrient == 1) {
										lodopLeft = "0";
									}
									if (queryFile.intOrient == 3) {
										queryFile.intOrient = 1;
										lodopLeft = "31mm"
										PRINT_PAGE_PERCENT = "Full-Width";
									}
									LODOP.SET_PRINT_PAGESIZE(
											queryFile.intOrient,
											queryFile.PageWidth,
											queryFile.PageHeight, "");
									LODOP.ADD_PRINT_HTM("0", lodopLeft,
											queryFile.PageWidth, "100%",
											util.rmi.loadXML({
												url : url,
												httpMethod : "get"
											}));
									LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
											PRINT_PAGE_PERCENT);
									LODOP.SET_PRINTER_INDEX(-1);
									LODOP.PRINT();
								} catch (e) {

									LODOP.SET_PRINT_PAGESIZE(2, 0, 0, "A4");
									LODOP.ADD_PRINT_HTM("0", "50%", "50%",
											"100%", util.rmi.loadXML({
												url : url,
												httpMethod : "get"
											}));
									if (jgid == '33010420') {
										LODOP.SET_PRINT_MODE(
												"PRINT_PAGE_PERCENT",
												"Full-Page");

									} else {
										LODOP.SET_PRINT_MODE(
												"PRINT_PAGE_PERCENT",
												"Full-Width");
									}
									// 直接打印
									LODOP.PRINT();
								}

							}
						}]
					});

				}
				wins.show();
			},
			initClinicRecord : function() {
				// 载入病历信息
				phis.script.rmi.jsonRequest({
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