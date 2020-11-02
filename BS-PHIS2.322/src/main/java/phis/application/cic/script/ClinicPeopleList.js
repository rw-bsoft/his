$package("phis.application.cic.script")

$import("phis.script.SimpleList", "phis.application.cic.script.EMRView",
		"phis.application.cic.script.PEMRView","phis.script.HealthCardReader")

phis.application.cic.script.ClinicPeopleList = function(cfg) {
	cfg.enableRowBody = true;
	cfg.headerGroup = true;
	// cfg.showWinOnly = true;
	cfg.closeAction = "hide";
	cfg.autoLoadData = false;// 如果通过win打开的时候将该值修改为false，并去除initPanel方法
	this.Fost = ["ne", ["$", "b.JKZSB"], ["l", "1"]];
	this.Fost2 = ["ne", ["$", "a.JKZSB"], ["l", "1"]];
	this.initCndForst = this.initCnd;
	this.initCnd = this.Fost2;
	cfg.width = 760;
	cfg.height = 400;
	cfg.modal = true;
	cfg.listServiceId = "patientQuery";
	Ext.apply(this,phis.script.HealthCardReader);
	phis.application.cic.script.ClinicPeopleList.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
var lastRowIndex = null;
Ext.extend(phis.application.cic.script.ClinicPeopleList,
		phis.script.SimpleList, {
			initPanel : function() {
				if (!this.mainApp['phis'].departmentId) {
					MyMessageTip.msg("提示", "未设置当前科室!", true);
					return;
				}
				// this.requestData.pageSize = 25;
				// this.requestData.body = {
				// jzzt : 0,
				// departmentId : this.mainApp['phis'].reg_departmentId
				// }
				var panel = phis.application.cic.script.ClinicPeopleList.superclass.initPanel
						.call(this);
				this.onWinShow();
				return panel;
			},
			onRenderer_btn:function(value, metaData, r,
											row, col, store){
				return "<div><div style='float:left' id='GRDA_"+r.data.PLXH+"'></div><div style='float:left' id='GXY_"+r.data.PLXH+"'></div><div style='float:left' id='GXYSF_"+r.data.PLXH+"'></div><div style='float:left' id='GXYSZ_"+r.data.PLXH+"'></div><div style='float:left' id='TNB_"+r.data.PLXH+"'></div><div style='float:left' id='TNBGW_"+r.data.PLXH+"'></div><div style='float:left' id='TNBSF_"+r.data.PLXH+"'></div><div style='float:left' id='XXG_"+r.data.PLXH+"'></div></div>";				
			},
			expansion : function(cfg) {
				var radiogroup = [{
							xtype : "radio",
							boxLabel : '待  诊',
							inputValue : 0,
							name : "jzzt",
							checked : true,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '暂  挂',
							name : "jzzt",
							inputValue : 2,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '已  诊',
							name : "jzzt",
							inputValue : 9,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}];
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([radiogroup, "->", tbar]);
				Ext.apply(this, this.loadSystemParams({
									// commons : ['QYMZDZBL'],
									privates : ['YXWGHBRJZ', 'QYMZPD',
											'QYMZDZBL']
								}));
			},
			onStoreLoadData : function(store, records, ops) {
				// var tempstore = store;
				var _this = this;
				phis.application.cic.script.ClinicPeopleList.superclass.onStoreLoadData
						.call(this, store, records, ops);
				// 获取系统参数 “启用门诊病人列表公卫提醒”
				var params = this.loadSystemParams({
					"commons" : ['QYMZBRLBGWTX']
				})
				if (params.QYMZBRLBGWTX != "1") {
					return;
				}
				var delayedTask = new Ext.util.DelayedTask();
				delayedTask.delay(0,function(_this){
					// return;
					var tempstore = _this.grid.getStore();
					if (tempstore.getCount() > 0) {
						var infocount = tempstore.getCount();
						for (var k = 0; k < infocount; k++) {
							var data = tempstore.data.items[k].data;
							var i = data.PLXH;
							var body = {};
							body.callType = "01";// 01门诊 02住院 03急诊 99其他
							body.patientCardNo = data.SFZH;// 被操作病人卡号，如身份证、健康卡号等
							body.patientName = data.BRXM;// 被操作病人姓名
							body.fromDomain = "his";// 主调域名
							body.toDomain = "chis";// 被调域名
							body.operSystemCode = "his";
							body.operSystemName = "基层医疗系统";
							// 需创建个人档案
							if(tempstore.data.items[k].data.GRDA!=1){
								var button = new Ext.Button({
										"text" : "待建档",
										l : k,
										nodekey : "A01",
										handler : function(obj) {
											_this.doSave(obj.nodekey, obj.l);
										}
									});
									button.render("GRDA_" + i);
									continue;
							}
							if (tempstore.data.items[k].data.JZZT != 0) {// 暂挂或已诊
								// 需创建高血压档案
								if(tempstore.data.items[k].data.GXYZD==1 && tempstore.data.items[k].data.GXYDA!=1){
									var button = new Ext.Button({
											"text" : "高血压建档",
											l : k,
											body : body,
											nodekey : "A03",
											handler : function(obj) {
												_this.doSave(obj.nodekey, obj.l);
												obj.body.apiCode = "XTGWBB";// 调用接口编码
												obj.body.serviceBean = "esb.XTGWBB";// 被调服务名
												obj.body.methodDesc = "void opengxyda(string)";// 被调方法描述
												obj.body.stat = "1";// 调用结果，0失败，1成功
												//obj.body.avgTimeCost = "";//调用时长，单位毫秒
												_this.visitCountLogForInterface(obj.body);
											}
										});
										button.render("GXY_" + i);
										continue;
								}
								
								// 需创建糖尿病档案
								if(tempstore.data.items[k].data.TNBZD==1 && tempstore.data.items[k].data.TNBDA!=1){
									var button = new Ext.Button({
											"text" : "糖尿病建档",
											l : k,
											body : body,
											nodekey : "A07",
											handler : function(obj) {
												_this.doSave(obj.nodekey, obj.l);
												obj.body.apiCode = "XTGWBB";// 调用接口编码
												obj.body.serviceBean = "esb.XTGWBB";// 被调服务名
												obj.body.methodDesc = "void opentnbda(string)";// 被调方法描述
												obj.body.stat = "1";// 调用结果，0失败，1成功
												_this.visitCountLogForInterface(obj.body);
											}
										});
										button.render("TNB_" + i);
										continue;
								}
							}
							var rsMsgList = _this.CHISJDRWTS(
									tempstore.data.items[k].data.EMPIID,
									tempstore.data.items[k].data.SBXH + "");
							for (var n = 0; n < rsMsgList.length; n++) {
								if (tempstore.data.items[k].data.GXYDA==1 && rsMsgList[n].nodeName.indexOf('高血压随访') >= 0
										&& document
												.getElementById("GXYSF_" + i)) {
									var button = new Ext.Button({
										"text" : "高血压随访",
										l : k,
										body : body,
										nodekey : rsMsgList[n].nodeKey,
										handler : function(obj) {
											_this.doSave(obj.nodekey, obj.l);
											obj.body.apiCode = "XTGWSFGL";// 调用接口编码
											obj.body.serviceBean = "esb.XTGWSFGL";// 被调服务名
											obj.body.methodDesc = "void opengxysf(string)";// 被调方法描述
											obj.body.stat = "1";// 调用结果，0失败，1成功
											_this.visitCountLogForInterface(obj.body);
										}
									});
									button.render("GXYSF_" + i);
								}
								if (tempstore.data.items[k].data.TNBDA==1 && rsMsgList[n].nodeName.indexOf('糖尿病随访') >= 0
										&& document.getElementById("TNBSF_"
												+ i)) {
									var button = new Ext.Button({
										"text" : "糖尿病随访",
										l : k,
										body : body,
										nodekey : rsMsgList[n].nodeKey,
										handler : function(obj) {
											_this.doSave(obj.nodekey, obj.l);
											obj.body.apiCode = "XTGWSFGL";// 调用接口编码
											obj.body.serviceBean = "esb.XTGWSFGL";// 被调服务名
											obj.body.methodDesc = "void opentnbsf(string)";// 被调方法描述
											obj.body.stat = "1";// 调用结果，0失败，1成功
											_this.visitCountLogForInterface(obj.body);
										}
									});
									button.render("TNBSF_" + i);
								}
							}
						}
					}
				},this,[_this]);
			},
			CHISJDRWTS : function(empiId,ghxh) {// 社区建档案任务提示,执行
				if (!this.SFQYGWXT) {
					var publicParam = {
						"commons" : ['SFQYGWXT']
					}
					this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
				}
				// if (this.mainApp.chisActive && this.SFQYGWXT) {
				if (this.SFQYGWXT) {
					// 判断病人诊断，是否要创建高血压，糖尿病档案及做肿瘤问题
					var r = util.rmi.miniJsonRequestSync({
								serviceId : "chis.chisRecordFilter",
								serviceAction : "getBrlbMsgInfo",
								method : "execute",
								body : {
									empiId : empiId,// "32012419650404261700000000000000",
									GHXH : ghxh// "832076"
								}
							});
					if (r.code < 300) {
						var body = r.json.body;
						if (body) {
							return body.rsMsgList;
						}
					} else {
						this.processReturnMsg(code, msg);
					}
				}
			},
			visitCountLogForInterface : function(body){
				util.rmi.miniJsonRequestAsync({
						serviceId : "phis.publicService",
						serviceAction : "visitCountLogForInterface",
						method : "execute",
						body : body
					}, function(code, msg, json) {						
					}, this);
			},
			afterCheck : function(radio, checked) {
				// clearTimeout(t);
				if (checked) {
					var jzzt = radio.inputValue;
					this.jzzt = radio.inputValue;
					if (jzzt > 1) {
						this.initCnd = this.Fost;
						this.grid.getColumnModel().setColumnHeader(10, "就诊时间");
					} else {
						this.initCnd = this.Fost2;
						this.grid.getColumnModel().setColumnHeader(10, "挂号时间");
					}
					this.requestData.body = {
						jzzt : jzzt,
						departmentId : this.mainApp['phis'].reg_departmentId
					}
					// this.refresh();
					this.doCndQuery();
				}
			},
			doCall : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "savePdJzzt",
							body : {
								GHKS : this.mainApp['phis'].reg_departmentId,
								JGID : this.mainApp['phisApp'].deptId,
								JZZT : 0
							}
						});
				var code = res.code;
				var msg = res.msg;
				var json = res.json;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				MyMessageTip.msg("提示", '已成功呼叫病人!', true);
			},
			doSkip : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "saveSkipInfo",
							body : {
								GHKS : this.mainApp['phis'].reg_departmentId,
								JGID : this.mainApp['phisApp'].deptId
							}
						});
				var code = res.code;
				var msg = res.msg;
				var json = res.json;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				MyMessageTip.msg("提示", '已跳过当前就诊病人!', true);
			},
			onWinShow : function() {
				var toolBar = this.grid.getTopToolbar();
				if (this.YXWGHBRJZ != "1") {
					if (toolBar.find("cmd", 'new')[0]) {
						toolBar.find("cmd", 'new')[0].hide();
					}
				}
				if (this.QYMZPD != "1") {
					if (toolBar.find("cmd", 'call')[0]) {
						toolBar.find("cmd", 'call')[0].hide();
					}
					if (toolBar.find("cmd", 'skip')[0]) {
						toolBar.find("cmd", 'skip')[0].hide();
					}
				}
				// this.radiogroup.setValue(0);
				// this.list.grid.getColumnModel().setColumnHeader(6,
				// "就诊时间");
				this.requestData.body = {
					jzzt : 0,
					departmentId : this.mainApp['phis'].reg_departmentId
				}
				this.refresh();
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			doNew : function(item, e) {
				// MyMessageTip.msg("注意", '测试 测试 测试!', true);
				// this.showModule(item, e);
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
						// cardOrMZHM : data.cardOrMZHM
					});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, r.msg, this.doNewPerson);
					return;
				} else {
					if (!pdms.json.cardOrMZHM) {
						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
						return;
					}
				}
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.application.pix.script.EMPIInfoModule");
					m = new phis.application.pix.script.EMPIInfoModule({
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
					m.on("onEmpiReturn", this.checkRecordExist, this);
					m.on("close", this.active, this);
					this.midiModules["healthRecordModule"] = m;
				}
				m.EMPIID = null;
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				// 1卡号
				var form = m.midiModules[m.entryName].form.getForm();
				if (pdms.json.cardOrMZHM == 1) {
					form.findField("MZHM").setDisabled(true);
				}
				// 2门诊号码
				if (pdms.json.cardOrMZHM == 2) {
					form.findField("cardNo").setValue(form.findField("MZHM")
							.getValue());
					form.findField("personName").focus(true, 200);
				}
			},
			showModule : function(item, e) {
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.application.pix.script.EMPIInfoModule");
					m = new phis.application.pix.script.EMPIInfoModule({
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
					m.on("onEmpiReturn", this.checkRecordExist, this);
					m.on("close", this.active, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			checkRecordExist : function(data) {
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveInitClinicInfo",
							body : {
								EMPIID : data.empiId,
								BRXZ : data.BRXZ
							}
						}, function(code, msg, json) {
							if (code < 300) {
								this.refresh();
								if (this.QYMZDZBL + "" == '1') {
									data.EMPIID = json.empiId;
									data.BRID = json.BRID;
									data.SBXH = json.GHXH
									this.emrViewShow(data);
								} else {
									var clinicId = json.JZXH;// 就诊序号
									var m = new phis.application.cic.script.EMRView(
											{
												empiId : json.empiId,
												clinicId : clinicId,
												brid : json.BRID,
												ghxh : json.GHXH
											})
									m.setMainApp(this.mainApp);
									m.getWin().show()
								}
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},
			emrViewShow : function(data) {
				var empiId = data.EMPIID;
				var brid = data.BRID;
				// 1、远程请求，判断就诊信息有效性
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveInitClinicInfo",
							body : data
						}, function(code, msg, json) {
							if (code < 300) {
								// 科室类型为中医科时才将患者信息推送到省中医馆平台 zhaojian 2018-07-20
								if(this.mainApp.departmentType=="AA50" || this.mainApp.deptId.length>9){
									// 异步处理【中医馆服务】推送患者信息
									var tcm = phis.script.rmi.miniJsonRequestAsync({
										serviceId : "phis.TcmService",
										serviceAction : "uploadBrxxToTcm",
										body : {
											jzxh : json.JZXH
										}
									});
								}
								var clinicId = json.JZXH;// 就诊序号
								var age = json.age;
								var m = new phis.application.cic.script.PEMRView(
										{
											empiId : empiId,
											clinicId : clinicId,
											brid : brid,
											age : age,
											ghxh : data.SBXH
										})
								m.initModules = null;
								m.on("close", function() {
											this.mainApp.locked = false;
											this.refresh();
										}, this);
								var exContext = {};
								exContext.brxx = data;
								Ext.applyIf(m.exContext, exContext);
								m.setMainApp(this.mainApp);
								m.getWin().show()
								m.getWin().maximize()
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this)
			},
			doSave : function(jdrwts,rowindex) {
				if(jdrwts){
					// r=this.store.data.items[rowindex];
					this.grid.getSelectionModel().selectRow(rowindex);  
				}
				var r = this.getSelectedRecord();
				//就诊状态为结束就诊、就诊医生与当前医生不一致，提示
				/*if(r.get('JZZT')==9 && r.get('YSDM')!=this.mainApp.uid){
					Ext.Msg.confirm("请注意", "本次就诊医生与上次就诊医生不一致,是否修改就诊医生?(本次操作不可逆,请慎重!)",function(btn){
						console.log(btn)
						if(btn=='yes'){
							this.doSaveNow(1,jdrwts);
						}else{
							this.doSaveNow(0,jdrwts);
						}
					}, this);
				}else{*/
					this.doSaveNow(1,jdrwts);
				// }
			},
			saveOperationHistory:function(jlxh){
				if(jlxh && jlxh>=0){
					var body={
							CZGH:this.mainApp.uid,
							CZSJ:this.mainApp.serverDateTime,
							JLXH:jlxh
					};
					var res = phis.script.rmi.miniJsonRequestSync({
						serviceId : "simpleSave",
						op : 'create',
						schema : 'phis.application.cic.schemas.MS_BLCZJL',
						body : body		
					});
				}
			},
			/**
			 * 
			 * @param flag 是否修改就诊医生，0：否，1：是
			 * @returns {Boolean}
			 */
			doSaveNow : function(flag,jdrwts) {
				var r = this.getSelectedRecord();
				var empiId,brid,ghxh,data;
				// 如果GHTYPE=1,表示该病人是自助挂号,自动插入一条挂号信息,并在自动插入挂号收费信息,并在收费处收费
				if(r.get("GHTYPE") == 1){
					var zzghresData = phis.script.rmi.miniJsonRequestSync({
						serviceId : "mSzzghService",
						serviceAction : "insGhxxAndGhf",
						body : r.data,
						ksdm : this.mainApp.reg_departmentId
					});
					console.log(zzghresData)
					var code = zzghresData.code;
					var msg = zzghresData.msg;
					var json = zzghresData.json;
					// this.panel.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return false;
					} else {
						empiId = json.EMPIID;
						brid = json.body.BRID;
						ghxh = json.body.SBXH;
						data = json.body;
						data.EMPIID=empiId;
					}
				}else{
					empiId = r.get("EMPIID");
					brid = r.get("BRID");
					data = r.data;
					ghxh = r.get("SBXH")
				}
				data.GHTYPE=r.get("GHTYPE");
				var data_tcm = data;		
				// var clinicId = r.get("JZHM");
				// 1、远程请求，判断就诊信息有效性
				// alert(r.data.toSource())
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveInitClinicInfo",
							body : data
						}, function(code, msg, json) {
							if (code < 300) {
								// 科室类型为中医科时才将患者信息推送到省中医馆平台 zhaojian 2018-07-20
								if(this.mainApp.departmentType=="AA50" || this.mainApp.deptId.length>9){
									// 异步处理【中医馆服务】推送患者信息
									var tcm = phis.script.rmi.miniJsonRequestAsync({
										serviceId : "phis.TcmService",
										serviceAction : "uploadBrxxToTcm",
										body : {
											jzxh : json.JZXH
										}
									});
								}
								var clinicId = json.JZXH;// 就诊序号
								var age = json.age;
								// var pdgdbz = json.PDGDBZ;// 规定病种
								var m = new phis.application.cic.script.EMRView(
										{
											empiId : empiId,
											clinicId : clinicId,
											brid : brid,
											age : age,
											ghxh : ghxh,
											ghlx:r.data.GHTYPE,
											updatingDoctor:flag,// 修改就诊医生
											sbxh:r.data.SBXH,
											// 增加挂号识别序号
											jdrwts : jdrwts// "A07"
										})
								m.exContext.JZKH = r.get("JZKH");// 就诊卡号
								m.setMainApp(this.mainApp);
								m.on("close", function() {
											this.mainApp.locked = false;
											this.doCndQuery();
										}, this);
								m.getWin().show()
								
								debugger
								var jzzt = document.getElementsByName("jzzt");
								for(var i=0;i<jzzt.length;i++){
									if(jzzt[i].checked == true){
										this.jzzt = jzzt[i].value;
									}
								}							
								if(this.jzzt == 0){
									var xzResult = util.rmi.miniJsonRequestSync({
										serviceId : "hai.hmsInterfaceService",
										serviceAction : "getDownRecord",
										method : "execute",
						                body : {		                    
					                    	"idcard":r.data.SFZH,           
						                }
									});
									if(xzResult.code == 200){
										var body = xzResult.json.body;
										var xzTime = body.xzTime;
										var yyName = body.yyName;
										if(typeof xzTime != "undefined" && typeof yyName != "undefined"){
											MyMessageTip.msg("提示", "此患者 "+xzTime+"从"+yyName+"下转诊患者！", false)
										}	
									}
																	
									var szResult = util.rmi.miniJsonRequestSync({
										serviceId : "hai.hmsInterfaceService",
										serviceAction : "getUpRecord",
										method : "execute",
						                body : {		                    
					                    	"idcard":r.data.SFZH,           
						                }
									});	
									if(szResult.code == 200){
										var body = szResult.json.body;
										var szTime = body.szTime;
										var yyName = body.yyName;
										if(typeof szTime != "undefined" || typeof yyName != "undefined"){
											MyMessageTip.msg("提示", "此患者 "+szTime+"从"+yyName+"上转诊患者！", false)
										}							
									}	
								}				
								this.saveOperationHistory(r.get('JZXH'));
								this.mainApp.locked = true;
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this)
			},
			doRefresh:function(){
				this.refresh();
			},
			onDblClick : function(grid, index, e) {
				var r = this.store.getAt(index);
				if(r.data.JZZT==0 && r.data.BRID.substr(r.data.BRID.length-1,r.data.BRID.length)>3){
					this.visitCountForCityInterface(r.data);
				}
				debugger
				// 下转，更新转诊状态
				if(r.data.JZZT == 0){
					util.rmi.jsonRequest({
						serviceId : "hai.hmsInterfaceService",
						serviceAction : "updateDownStatus",
						method : "execute",
		                body : {		                    
	                    	"idcard":r.data.SFZH,
	                    	"deptCode":this.mainApp['phis'].departmentId,
	                    	"status":"4"             
		                }
					});	
					// 上转，更新转诊状态
					util.rmi.jsonRequest({
						serviceId : "hai.hmsInterfaceService",
						serviceAction : "updateUpStatus",
						method : "execute",
		                body : {		                    
	                    	"idcard":r.data.SFZH,
	                    	"deptCode":this.mainApp['phis'].departmentId,
	                    	"status":"4"             
		                }
					});	
				}			
				if(this.QYMZDZBL+""=='1'){
					this.emrViewShow(r.data);
				}else{
					this.doSave(); 
				}
			},
			// 2019-08-22 zhaojian 待诊病人双击后调用市健康档案浏览器调阅接口（统计次数）
			visitCountForCityInterface : function(data){
				util.rmi.miniJsonRequestAsync({
						serviceId : "phis.publicService",
						serviceAction : "visitCountForCityInterface",
						method : "execute",
						body : {ksmc:this.mainApp.departmentName,// 科室名称
							ip:"192.168.10.121",
							zjhm:data.SFZH,// 证件号码
							hzxm:data.BRXM,// 患者姓名
							ywxt:""
						}
					}, function(code, msg, json) {						
					}, this);
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				// var it = this.schema.items[index]
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					return;
				}
				this.resetFirstPage()
				var f = this.cndField;
				var v = f.getValue()

				if (v == null || v == "") {
					this.queryCnd = null;
					this.requestData.cnd = initCnd
					this.refresh()
					return
				}
				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}
				if (f.getXType() == "datetimefield") {
					v = v.format("Y-m-d H:i:s")
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode
						if (!node.isLeaf()) {
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
						} else {
							cnd.push(['s', v])
						}
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							if (v.format) {
								v = v.format("Y-m-d")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (v.format) {
								v = v.format("Y-m-d H:i:s")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				// 2017-08-10 zhaojian 医生站病人列表增加按病人姓名查询功能
				if(cnd[1][1]=="b.BRXM" || cnd[1][1]=="b.JZKH"){
					cnd[1] = ["$", "c.BRXM"];
					var cnd2 = [];
					cnd2[0]=cnd[0];
					cnd2[1] = ["$", "d.CardNo"];
					cnd2[2]=cnd[2];
					cnd= ['or', cnd2, cnd];					
				}
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable || it.queryable == 'false') {
						continue
					}
					if (it.selected == "true") {
						selected = it.id;
						defaultItem = it;
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = null;
				if (fields.length > 1) {
					combox = new Ext.form.ComboBox({
								store : store,
								valueField : "value",
								displayField : "text",
								value : selected,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 100
							});
					combox.on("select", this.onCndFieldSelect, this)
					this.cndFldCombox = combox
				} else {
					combox = new Ext.form.Label({
								text : fields[0].text
							});
					this.cndFldCombox = new Ext.form.Hidden({
								value : fields[0].value
							});
				}

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 150
						cndField = this.createDicField(defaultItem.dic)
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
								width : 150,
								selectOnFocus : true,
								name : "dftcndfld"
							})
				}
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				var queryBtn = new Ext.Toolbar.Button({
					text : '',
					iconCls : "query",
					notReadOnly : true
						// ** add by yzh **
						// menu : new Ext.menu.Menu({
						// items : {
						// text : "高级查询",
						// iconCls : "common_query",
						// handler : this.doAdvancedQuery,
						// scope : this
						// }
						// })
					})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			},
			doJkk : function(){// add by lizhi 2017-07-22健康卡读卡
				this.initHtmlElement();
				var initResult = this.initCard();
// alert("初始化结果："+initResult);
				var initArr = initResult.split("|");
				if(initArr.length>0 && initArr[0]>-1){
					var cardinfo = this.readCardInfo();
					this.OffCardRead();
// alert("读取卡信息："+cardinfo);
			        var arr = cardinfo.split("^");
					if(arr[0]<0){
						Ext.Msg.alert("提示", "读卡失败!");
						this.getWin().hide();
						return;
					}
			        if(arr.length<3){
			        	Ext.Msg.alert("提示", "读卡失败!");
			        	this.getWin().hide();
						return;
			        }
					var cardArr = arr[2].split("|");
					if(cardArr.length>0){
						var brxm = cardArr[0];
						var sfzh = cardArr[4];
						var store = this.grid.getStore();
						var n = store.getCount();
						for (var i = 0; i < n; i++) {
							var r = store.getAt(i);
// if ((r.get("BRXM")).indexOf(brxm) >= 0) {
							if ((r.get("SFZH")).indexOf(sfzh) >= 0) {
								this.grid.getSelectionModel().selectRow(i);
								var record = this.grid.store.getAt(i);
								if (record) {
									this.doSave();
// this.emrViewShow(record.data);
								}
								return;
							}
						}
					}
				}
			}
		});