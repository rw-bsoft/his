/**
 * 医技业务处理
 * 
 * @author liyunt
 */
$package("phis.application.med.script");
$import("phis.script.SimpleModule", "util.dictionary.DictionaryLoader",
		"util.dictionary.TreeDicFactory");
phis.application.med.script.MedicalTechnicalSectionsModule = function(cfg) {
	this.exContext = this.exContext || {};
	this.cfg = cfg;
	phis.application.med.script.MedicalTechnicalSectionsModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.med.script.MedicalTechnicalSectionsModule,
		phis.script.SimpleModule, {
			onReady : function() {
				if (this.autoLoadData) {
					this.loadData();
				}
				var el = this.panel.el
				if (!el) {
					return
				}
				var actions = this.actions
				if (!actions) {
					return
				}
				var f1 = 112
				var keyMap = new Ext.KeyMap(el)
				keyMap.stopEvent = true

				var btnAccessKeys = {}
				var keys = []
				if (this.showButtonOnTop && this.panel.getTopToolbar()) {
					var btns = this.panel.getTopToolbar().items;
					if (btns) {
						var n = btns.getCount()
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i)
							var key = btn.accessKey
							if (key) {
								btnAccessKeys[key] = btn
								keys.push(key)
							}
						}
					}
				} else {
					var btns = this.panel.buttons
					if (btns) {
						for (var i = 0; i < btns.length; i++) {
							var btn = btns[i]
							var key = btn.accessKey
							if (key) {
								btnAccessKeys[key] = btn
								keys.push(key)
							}
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				keyMap.on(keys, this.onAccessKey, this)
				if (this.win) {
					keyMap.on({
								key : Ext.EventObject.ESC,
								shift : true
							}, this.onEsc, this)
				}

				var toolBar = this.panel.getTopToolbar()
				var search = toolBar.getComponent("searchObj");
				search.on("specialkey", function(v, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								this.doFilter(v);
							}
						}, this)
				search.on("focus", function(v, e) {
							v.setValue(null);
						}, this)

			},
			doFilter : function(v) {
				var s = v.getValue();
				if (v.validate()) {
					if (this.curTable == 'mz') {
						this.tabModule.mzList.requestData["search"] = s;
						this.tabModule.mzList.loadData();
						this.tabModule.mzList.requestData["search"] = null;

					} else if(this.curTable == 'zy'){
						this.tabModule.zyList.requestData["search"] = s;
						this.tabModule.zyList.loadData();
						this.tabModule.zyList.requestData["search"] = null;
					}else{
						this.tabModule.jcList.requestData["search"] = s;
						this.tabModule.jcList.loadData();
						this.tabModule.jcList.requestData["search"] = null;
					}
				}
			},
			onAccessKey : function(key, e) {
				e.stopEvent()
				var btn = this.btnAccessKeys[key]
				if (!btn.disabled) {
					if (btn.enableToggle) {
						btn.toggle(!btn.pressed)
					}
					this.doAction(btn)
				}
				var ev = window.event
				try {
					ev.keyCode = 0;
					ev.returnValue = false
					return false
				} catch (e) {
				}
			},
			onEsc : function() {
				this.win.hide()
			},
			initPanel : function() {
				if (!this.mainApp['phis'].MedicalId) {
					MyMessageTip.msg("提示", "当前不存在医技科室，请先选择医技科室!", true);
					return;
				}
				var re = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicalTechnicalSectionService",
							serviceAction : "queryXTCS",
							CSMC : "YJZXQXZD"
						});
				if (re.code > 300) {
					this.processReturnMsg(re.code, re.msg, this.initPanel);
					return;
				} else {
					this.YJZXQXZD = re.json.body;
				}
				if (this.panel) {
					return this.panel;
				}
				this.tbar = this.tbar
						|| this.initConditionFields().concat(this
								.createButtons())
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getTabs()
									}],
							tbar : this.tbar
						})
				panel.on("render", this.onReady, this);
				return (this.panel = panel)

			},
			getTabs : function() {
				var module = this.createModule("medModuleTab", this.refTable,
						this.cfg);
				if (module) {
					module.exContext = this.exContext;
					module.opener = this;
					module.openBy = this.openBy;
					this.tabModule = module;
					return module.initPanel();
				}
			},
			// 生成查询框
			initConditionFields : function(items) {
				return [new Ext.form.Label({
									cmd : 'CQHM',
									text : "卡号："
								}), new Ext.form.TextField({
									id : "searchObj",
									fieldLabel : '门诊号码',
									bodyStyle : 'border:0px',
									name : 'CQHM',
									regex : /^[0-9]+$/
								})];
			},
			doAdd : function(item, e) {
				if (this.curTable == 'mz') {
					var module = this.createModule("mzListProjectManage",
							"phis.application.med.MED/MED/MED01010101");
					module.on("save", this.doRefresh, this)
					module.param = {
						XZPB : 1,
						SFSF : 0
					};
					module.getWin().show();
					module.doNew();
				} else if (this.curTable == 'zy') {
					var module = this.createModule("zyListProjectManage",
							"phis.application.med.MED/MED/MED01010201");
					module.on("save", this.doRefresh, this)
					module.param = {
						XZPB : 1,
						SFSF : 2
						// 本科室输入
					};
					module.getWin().show();
					module.doNew();
				}else{
					var module = this.createModule("jcListProjectManage",
					"phis.application.med.MED/MED/MED01010501");
					module.on("save", this.doRefresh, this)
					module.param = {
						XZPB : 1,
						SFSF : 3
						// 本科室输入
					};
					module.getWin().show();
					module.doNew();
				}
			},
			doModify : function(i, e) {
				if (this.curTable == 'mz') {
					var r1 = this.tabModule.mzList.getSelectedRecords();
					if (r1.length > 1) {
						MyMessageTip.msg("提示", "每次只能修改一条", true);
						return;
					}

					var r = this.tabModule.mzList.getSelectedRecord();
					if (!r) {
						return;
					}
					var yjxh = r.get("YJXH");// 医技序号
					var re = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicalTechnicalSectionService",
								serviceAction : "queryMZZXPB",
								body : yjxh
							});
					if (re.code > 300) {
						this.processReturnMsg(re.code, re.msg,
								this.onBeforeSave);
						return;
					} else {
						var body = re.json.body;
						var zxpb = body.ZXPB;
						if (zxpb == 1) {
							MyMessageTip.msg("提示", "该检查单已被他人执行,不能被修改，将重新刷新列表!",
									true);
							this.tabModule.mzList.refresh();
							return
						}
						var fphm = body.FPHM;
						var exchangeParam = {};
						exchangeParam["YJXH"] = yjxh;
						exchangeParam["ZXPB"] = zxpb;
						exchangeParam["XZPB"] = 0; // 0 不为新增, 1 新增
						if (fphm) {
							exchangeParam["TYPE"] = 1; // 已收过费
						} else {
							exchangeParam["TYPE"] = 0; // 未收费
						}
						var module = this.createModule("mzListProjectManage",
								"phis.application.med.MED/MED/MED01010101");
						module.param = exchangeParam;
						module.on("save", function() {
									this.doRefresh();
								}, this)
						module.getWin().show();
						module.loadData();
					}
				} else if (this.curTable == 'zy') {// 住院
					var r1 = this.tabModule.zyList.getSelectedRecords();
					if (r1.length > 1) {
						MyMessageTip.msg("提示", "每次只能修改一条", true);
						return;
					}

					var r = this.tabModule.zyList.getSelectedRecord();
					if (!r) {
						return;
					}
					var yjxh = r.get("YJXH");// 医技序号
					var re = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicalTechnicalSectionService",
								serviceAction : "queryZYZXPB",
								body : yjxh
							});

					if (re.code > 300) {
						this.processReturnMsg(re.code, re.msg,
								this.onBeforeSave);
						return;
					} else {
						var body = re.json.body;
						var zxpb = body.ZXPB;
						if (zxpb == 1) {
							MyMessageTip.msg("提示", "该检查单已被他人执行,不能被修改，将重新刷新列表!",
									true);
							this.tabModule.zyList.refresh();
							return
						}
						var exchangeParam = {};
						exchangeParam["YJXH"] = yjxh;
						exchangeParam["ZXPB"] = zxpb;
						exchangeParam["XZPB"] = 0; // 0 不为新增, 1 新增
						if (!r.get("YZXH") || r.get("YZXH") == 0) {
							exchangeParam["TYPE"] = 0; // 是从本科室输入的
						} else {
							exchangeParam["TYPE"] = 1; // 是从病区输入的
						}
						if (body.CYPB >= 8) {
							MyMessageTip.msg("提示", "该病人已出院!", true);
							this.tabModule.mzList.refresh();
							return
						}
						var module = this.createModule("zyListProjectManage",
								"phis.application.med.MED/MED/MED01010201");
						module.param = exchangeParam;
						module.on("save", function() {
									this.doRefresh();
								}, this)
						module.getWin().show();
						module.doNew();
						module.loadData();
					}

				}else{
					var r1 = this.tabModule.jcList.getSelectedRecords();
					if (r1.length > 1) {
						MyMessageTip.msg("提示", "每次只能修改一条", true);
						return;
					}

					var r = this.tabModule.jcList.getSelectedRecord();
					if (!r) {
						return;
					}
					var yjxh = r.get("YJXH");// 医技序号
					var re = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicalTechnicalSectionService",
								serviceAction : "queryJCZXPB",
								body : yjxh
							});

					if (re.code > 300) {
						this.processReturnMsg(re.code, re.msg,
								this.onBeforeSave);
						return;
					} else {
						var body = re.json.body;
						var zxpb = body.ZXPB;
						if (zxpb == 1) {
							MyMessageTip.msg("提示", "该检查单已被他人执行,不能被修改，将重新刷新列表!",
									true);
							this.tabModule.jcList.refresh();
							return
						}
						var exchangeParam = {};
						exchangeParam["YJXH"] = yjxh;
						exchangeParam["ZXPB"] = zxpb;
						exchangeParam["XZPB"] = 0; // 0 不为新增, 1 新增
						if (!r.get("YZXH") || r.get("YZXH") == 0) {
							exchangeParam["TYPE"] = 0; // 是从本科室输入的
						} else {
							exchangeParam["TYPE"] = 1; // 是从病区输入的
						}
						if (body.CYPB >= 8) {
							MyMessageTip.msg("提示", "该病人已出院!", true);
							this.tabModule.jcList.refresh();
							return
						}
						var module = this.createModule("jcListProjectManage",
								"phis.application.med.MED/MED/MED01010501");
						module.param = exchangeParam;
						module.on("save", function() {
									this.doRefresh();
								}, this)
						module.getWin().show();
						module.doNew();
						module.loadData();
					}
				}
			},
			doExecute : function() {
				var mzzypb =0;
				if(this.curTable == "mz"){ 
					mzzypb =0; 
				}else if(this.curTable == "zy"){
					mzzypb =1; 
				}else{
					mzzypb =2;
				}
				// 住院tab
				if (this.curTable == "mz") {
					var r1 = this.tabModule.mzList.getSelectedRecords();
					if (!r1 || r1.length < 1) {
						MyMessageTip.msg("提示", "请选择要执行的项目！", true);
						return;
					}
				} else if(this.curTable=="zy"){
					var r1 = this.tabModule.zyList.getSelectedRecords();
					if (!r1 || r1.length < 1) {
						MyMessageTip.msg("提示", "请选择要执行的项目！", true);
						return;
					}
				}else{
					var r1 = this.tabModule.jcList.getSelectedRecords();
					if (!r1 || r1.length < 1) {
						MyMessageTip.msg("提示", "请选择要执行的项目！", true);
						return;
					}
				}

				// 选择诊断结果
				if (this.YJZXQXZD != 1) {
					Ext.MessageBox.confirm('确认', '确定执行医技?',
							function(btn, text) {
								if (btn == "yes") {
									this.onZDDMReturn();
								}
							}, this);
				} else {
					var bodys = {}; // 批量提交
					bodys["params_mz"] = [];
					bodys["params_zy"] = [];
					bodys["params_jc"] = [];
					if (mzzypb == 0) {
						records = this.mzList.getSelectedRecords();
					} else if(mzzypb == 1){
						// add by caijy for 物资接口
						var body = {};
						body["bq"] = this.mainApp['phis'].MedicalId;
						var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : "configLogisticsInventoryControlService",
									serviceAction : "verificationWPJFBZ",
									body : body
								});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg);
							return;
						}
						records = this.zyList.getSelectedRecords();
					}else{
						records = this.jcList.getSelectedRecords();
					}
					var ksdm = this.mainApp['phis'].MedicalId; // 科室代码
					var jgid = this.mainApp['phisApp'].deptId; // 机构id
// var zdid_ = zdid; // 诊断ID
					for (var i = 0; i < records.length; i++) {
						records[i].data.KSDM = ksdm;
						records[i].data.JGID = jgid;
						records[i].data.ZDID = 0;
						bodys["params_mz"].push(records[i].data);
						bodys["params_zy"].push(records[i].data);
						bodys["params_jc"].push(records[i].data);
					}
					bodys["MZZYPB"] = mzzypb;
					// 执行医技单提交
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicalTechnicalSectionService",
								serviceAction : "queryYjProject",
								body : bodys
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.doLogout);
						return
					}
					if (r.json["errorCode"] == 1) {
						Ext.Msg.alert('提示', r.json["errorMsg"]);
						this.tabModule.loadData();
						return;
					}
					if(mzzypb==0){
						var module1 = this.createModule("diagnosisMaintainSelect1",
						"phis.application.med.MED/MED/MED01010104");
						module1.on("zddmReturn", this.onZDDMReturn, this);
						module1.loadData();
						module1.getWin().show();
					}else if(mzzypb==1){
						var module2 = this.createModule("diagnosisMaintainSelect2",
						"phis.application.med.MED/MED/MED01010204");
						module2.on("zddmReturn", this.onZDDMReturn, this);
						module2.loadData();
						module2.getWin().show();
					}else{
						var module3 = this.createModule("diagnosisMaintainSelect3",
							"phis.application.med.MED/MED/MED01010504");
						module3.on("zddmReturn", this.onZDDMReturn, this);
						module3.loadData();
						module3.getWin().show();
					}
				}
			},
			onZDDMReturn : function(zdid) {
				// prepare arguments
				var mzzypb =0;
				if(this.curTable == "mz"){ 
					mzzypb =0; 
				}else if(this.curTable == "zy"){
					mzzypb =1; 
				}else{
					mzzypb =2;
				}
				if (this.YJZXQXZD == 1) {
					if(mzzypb==0){
						this.midiModules["diagnosisMaintainSelect1"].getWin().hide();
					}else if(mzzypb==1){
						this.midiModules["diagnosisMaintainSelect2"].getWin().hide();
					}else{
						this.midiModules["diagnosisMaintainSelect3"].getWin().hide();
					}
				}
				var records;
				var bodys = {}; // 批量提交
				bodys["params_mz"] = [];
				bodys["params_zy"] = [];
				bodys["params_jc"] = [];
				if (mzzypb == 0) {
					records = this.mzList.getSelectedRecords();
				} else if(mzzypb==1){
					// add by caijy for 物资接口
					var body = {};
					body["bq"] = this.mainApp['phis'].MedicalId;
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "configLogisticsInventoryControlService",
								serviceAction : "verificationWPJFBZ",
								body : body
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return;
					}
					records = this.zyList.getSelectedRecords();
				}else{
					records = this.jcList.getSelectedRecords();
				}
				var brid = records[0].data.BRID; // 病人id 诊断结果报表导出用
				var zyh=records[0].data.ZYH;
				var ksdm = this.mainApp['phis'].MedicalId; // 科室代码
				var jgid = this.mainApp['phisApp'].deptId; // 机构id
				var zdid_ = zdid; // 诊断ID

				for (var i = 0; i < records.length; i++) {
					records[i].data.KSDM = ksdm;
					records[i].data.JGID = jgid;
					records[i].data.ZDID = zdid_;
					bodys["params_mz"].push(records[i].data);
					bodys["params_zy"].push(records[i].data);
					bodys["params_jc"].push(records[i].data);
				}
				bodys["MZZYPB"] = mzzypb;
				// 执行医技单提交
				this.tabModule.tab.el.mask("正在执行操作...");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicalTechnicalSectionService",
							serviceAction : "executeYjProject",
							body : bodys
						});
				this.tabModule.tab.el.unmask();
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doLogout);
					return
				}
				if (r.json != null) {
					var body = r.json;
				}
				if (body["errorCode"] == 1) {
					Ext.Msg.alert('提示', body["errorMsg"]);
					this.tabModule.loadData();
					return;
				} else {
					this.tabModule.loadData();
					// 打印诊断结果报告
					if (this.YJZXQXZD == 1) {
						Ext.MessageBox.confirm("提示", "执行成功,是否要打印诊断报告?",
								function(btn, txt) {
									if (btn == "yes") {
										this.doPrint({
													brid : brid,
													zyh : zyh,
													mzzypb : mzzypb,
													zdid : zdid
												});
									}
								}, this);
					}
				}
			},
			doPrint : function(arg) {
				$import("phis.prints.script.PatientYJZDJGPrintView");
				module = new phis.prints.script.PatientYJZDJGPrintView(this.cfg);
				module.initPanel();
				Ext.apply(module, arg);
				module.doPrint();
			},
			doDelete : function() {
				// var mzzypb = this.curTable == "mz" ? 0 : 1; // 0 门诊tab 1
				// 住院tab
				if (this.curTable == "mz") {
					var r1 = this.tabModule.mzList.getSelectedRecords();
					if (!r1 || r1.length < 1) {
						MyMessageTip.msg("提示", "请先勾选需要操作的数据!", true);
						return;
					}
				} else if(this.curTable == "zy"){
					var r1 = this.tabModule.zyList.getSelectedRecords();
					if (!r1 || r1.length < 1) {
						MyMessageTip.msg("提示", "请先勾选需要操作的数据!", true);
						return;
					}
				}else{
					var r1 = this.tabModule.jcList.getSelectedRecords();
					if (!r1 || r1.length < 1) {
						MyMessageTip.msg("提示", "请先勾选需要操作的数据!", true);
						return;
					}
				}
				Ext.MessageBox.confirm("提示", "确认删除吗？", function(btn, txt) {
							if (btn == "yes") {
								this.deleteYJD();
								return;
							}
						}, this);
			},
			deleteYJD : function() {
				// prepare arguments
				var mzzypb =0;
				if(this.curTable == "mz"){ 
					mzzypb =0; 
				}else if(this.curTable == "zy"){
					mzzypb =1; 
				}else{
					mzzypb =2;
				}
				// 住院tab
				var ksdm = this.mainApp['phis'].MedicalId; // 科室代码
				var jgid = this.mainApp['phisApp'].deptId; // 机构id
				var r;
				YZXH_S = [];
				if (this.curTable == "mz") {
					var r1 = this.tabModule.mzList.getSelectedRecords();
					if (r1.length > 1) {
						MyMessageTip.msg("提示", "每次只能删除一条", true);
						return;
					}
					r = this.mzList.getSelectedRecord().data;
				} else if(this.curTable == "zy"){
					var r1 = this.tabModule.zyList.getSelectedRecords();
					if (r1.length > 1) {
						MyMessageTip.msg("提示", "每次只能删除一条", true);
						return;
					}
					r = this.zyList.getSelectedRecord().data;
					this.zyList.dList.store.each(function(record) {
								YZXH_S.push(record.data.YZXH);
							});
				}else{
					var r1 = this.tabModule.jcList.getSelectedRecords();
					if (r1.length > 1) {
						MyMessageTip.msg("提示", "每次只能删除一条", true);
						return;
					}
					r = this.jcList.getSelectedRecord().data;
					this.jcList.dList.store.each(function(record) {
								YZXH_S.push(record.data.YZXH);
							});
				}
				this.tabModule.tab.el.mask("正在执行操作...");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicalTechnicalSectionService",
							serviceAction : "deleteYjProject",
							body : {
								params_mz : Ext.apply(r, {
											KSDM : ksdm,
											JGID : jgid
										}),
								params_zy : Ext.apply(r, {
											KSDM : ksdm,
											JGID : jgid,
											YZXH_S : YZXH_S
										}),
								params_jc : Ext.apply(r, {
											KSDM : ksdm,
											JGID : jgid,
											YZXH_S : YZXH_S
										}),
								MZZYPB : mzzypb
							}
						});
				this.tabModule.tab.el.unmask();
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doLogout);
					return
				}
				if (r.json != null) {
					var body = r.json;
				}
				if (body["errorCode"] == 1) {
					Ext.Msg.alert('提示', body["errorMsg"]);
					this.tabModule.loadData();
					return;
				} else {
					Ext.Msg.alert('提示', "删除成功!");
					this.tabModule.loadData();
				}
			},
			doRefresh : function() {
				this.tabModule.loadData();
			},
			doGoback:function(){
				var r = this.tabModule.jcList.getSelectedRecords();
				if (r.length <=0) {
					MyMessageTip.msg("提示", "选择需要退回的记录", true);
					return;
				}
				var yjxh=[];
				for(var i=0;i<r.length;i++){
					yjxh.push(r[i].data.YJXH);
				}
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicalTechnicalSectionService",
					serviceAction : "deleteJcProject",
					YJXH:yjxh
					});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doLogout);
					return
				}else{
					MyMessageTip.msg("提示", "退回成功！", true);
					this.doRefresh();
				}
			}

		});