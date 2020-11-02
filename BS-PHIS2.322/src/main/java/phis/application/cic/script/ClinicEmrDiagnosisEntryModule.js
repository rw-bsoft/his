$package("phis.application.cic.script")

$import("phis.script.SimpleModule", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cic.script.ClinicEmrDiagnosisEntryModule = function(cfg) {
	cfg.modal = true;
	// cfg.width = 998;
	this.clinicData = {};
	phis.application.cic.script.ClinicEmrDiagnosisEntryModule.superclass.constructor
			.apply(this, [ cfg ])
	this.on("winShow", this.onWinShow, this);
	this.on("beforeclose", this.beforeclose, this);
}

Ext
		.extend(
				phis.application.cic.script.ClinicEmrDiagnosisEntryModule,
				phis.script.SimpleModule,
				{
					keyManageFunc : function(keyCode, keyName) {
						this.list.doAction(this.list.btnAccessKeys[keyCode]);
					},
					initPanel : function() {
						if (this.panel)
							return this.panel;
						var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							// height : 637,
							items : [ {
								layout : "border",
								border : false,
								split : true,
								region : 'center',
								items : [ this.getList() ]
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'east',
								width : 360,
								items : this.getQuickInputTab()
							} ]
						});
						this.panel = panel;
						return panel;
					},
					onWinShow : function() {
						var cliniclist = this.midiModules['cliniclist'];
						var jzxh = this.exContext.ids.clinicId;
						var brid = this.exContext.ids.brid;
						cliniclist.requestData.cnd = [
								'and',
								[
										'and',
										[ 'eq', [ '$', 'BRID' ], [ 'd', brid ] ],
										[ 'eq', [ '$', 'JZXH' ], [ 'd', jzxh ] ] ],
								[ 'eq', [ '$', 'JGID' ],
										[ 's', this.mainApp['phisApp'].deptId ] ] ];
						cliniclist.refresh();
					},
					onTabShow : function() {
						// var resData = phis.script.rmi.miniJsonRequestSync({
						// serviceId : "clinicManageService",
						// serviceAction : "loadClinicInfo",
						// body : {
						// "brid" : this.exContext.ids.brid,
						// "clinicId" : this.exContext.ids.clinicId,
						// "type" : '2'
						// }
						// });
						// if(resData.code>=300){
						// this.processReturnMsg(resData.code, resData.msg);
						// }
						// var brzds = resData.json.ms_brzd;
						// var num = 1;
						// var zdStr = "";
						// for (var i = 0; i < brzds.length; i++) {
						// var row = brzds[i];
						// if(i == 0){
						// zdStr = row.DEEP == 0 ? num + '.' : ' ';
						// zdStr +=row.ZDMC;
						// }else{
						// zdStr += "\n";
						// zdStr += row.DEEP == 0 ? num + '.' : ' ';
						// for(var j = 0 ; j < parseInt(row.DEEP) ; j ++){
						// zdStr += " ";
						// }
						// zdStr += row.ZDMC;
						// }
						// if (row.DEEP == 0)
						// num++;
						// }
						// this.opener.brzds = brzds;
						this.opener.Backfill("MS_BRZD");
						// if(this.midiModules['cliniclist']){
						// var cliniclist = this.midiModules['cliniclist'];
						// var num = 1;
						// var zdStr = "";
						// for (var i = 0; i < cliniclist.store.getCount(); i++)
						// {
						// var row = cliniclist.store.getAt(i);
						// if(i == 0){
						// zdStr = row.data.DEEP == 0 ? num + '.' : ' ';
						// zdStr +=row.data.ZDMC;
						// }else{
						// // zdStr += "\n";
						// zdStr += "\n";
						// zdStr += row.data.DEEP == 0 ? num + '.' : ' '
						// for(var j = 0 ; j < parseInt(row.data.DEEP) ; j ++){
						// zdStr += " ";
						// }
						// zdStr += row.data.ZDMC;
						// }
						// data.push(row.data);
						// if (row.data.DEEP == 0)
						// num++;
						// }
						// this.opener.Backfill("MS_BRZD",zdStr);
						// }
					},
					getList : function() {
						var module = this.createModule("cliniclist",
								this.clinicDiagnosisEntryList);
						module.BRID = this.exContext.ids.brid;// 病人id
						module.JZXH = this.exContext.ids.clinicId; // 就诊序号
						module.on("loadData", function(store) {
							if (store.getCount() == 0) {
								module.doNewClinic();
							}
						}, this);
						module.on("changeTab", this.onChangeTab, this)
						module.expansion = function(cfg) {
							cfg.title = "诊断信息列表"
							cfg.region = "center"
						}
						module.opener = this;
						module.exContext = this.exContext;
						var _ctx = this;
						// 新增诊疗
						module.doNewClinic = function() {
							this.removeEmptyRecord();
							var o = this.getStoreFields(this.schema.items)
							var Record = Ext.data.Record.create(o.fields)
							var factory = util.dictionary.DictionaryLoader
							var data = {};
							data.DEEP = 0;
							for ( var i = 0; i < this.schema.items.length; i++) {
								var it = this.schema.items[i]
								var v = null
								if (it.defaultValue) {
									v = it.defaultValue
									data[it.id] = v
									var dic = it.dic
									if (dic) {
										data[it.id] = v.key;
										var o = factory.load(dic)
										if (o) {
											var di = o.wraper[v.key];
											if (di) {
												data[it.id + "_text"] = di.text
											}
										}
									}
								}
								if (it.type && it.type == "int") {
									data[it.id] = (data[it.id] == "0"
											|| data[it.id] == "" || data[it.id] == undefined) ? 0
											: parseInt(data[it.id]);
								}

							}
							if (this.grid.store.getCount() > 0) {
								var zxlbr = this.grid.store
										.getAt(this.grid.store.getCount() - 1);
								if (zxlbr.get("ZXLB") == 1) {
									data.ZXLB = 1;
									data.ZXLB_text = "西医"
									this.remoteDicStore.baseParams = {
										"ZXLB" : 1
									}
									this.remoteDic.lastQuery = "";
								} else {
									data.ZXLB = 2;
									data.ZXLB_text = "中医"
									this.remoteDicStore.baseParams = {
										"ZXLB" : 2
									}
									this.remoteDic.lastQuery = "";
								}
							}
							var r = new Record(data);
							r.set("FBRQ", new Date().format('Y-m-d'));
							this.store.add([ r ]);
							this.grid.getSelectionModel().select(
									this.store.getCount() - 1, 1);
							var store = this.grid.getStore();
							var n = store.getCount() - 1
							this.grid.startEditing(n, 2);
							// _ctx.tab.activate(0)// 切换到默认tab页
						}
						// 新增子诊疗
						module.doSubClinic = function() {
							// 获得当前选中的诊断信息
							this.removeEmptyRecord();
							var record = this.getSelectedRecord();
							if (!record) {
								MyMessageTip.msg('警告', '请先选中父诊断信息!', true);
								return;
							}
							if (record.get("ZDXH") == null
									|| record.get("ZDXH") == "") {
								MyMessageTip.msg('警告', '父诊断信息不正确，请检查输入!', true);
								return;
							}
							var SJZD = record.get("JLBH") ? record.get("JLBH")
									: record.get("ZDXH");
							var cell = this.grid.getSelectionModel()
									.getSelectedCell();
							// 获得子诊断插入的行号
							var row = cell[0];
							row = module.getSubInsertRow(row, SJZD);
							if (!row) {
								row = 0;
							}
							// 插入一条诊断信息
							var o = this.getStoreFields(this.schema.items)
							var Record = Ext.data.Record.create(o.fields)
							var factory = util.dictionary.DictionaryLoader
							var data = {};
							data.SJZD = SJZD;
							data.DEEP = parseInt(record.get("DEEP")) + 1;
							for ( var i = 0; i < this.schema.items.length; i++) {
								var it = this.schema.items[i]
								var v = null
								if (it.defaultValue) {
									v = it.defaultValue
									data[it.id] = v
									var dic = it.dic
									if (dic) {
										data[it.id] = v.key;
										var o = factory.load(dic)
										if (o) {
											var di = o.wraper[v.key];
											if (di) {
												data[it.id + "_text"] = di.text
											}
										}
									}
								}
								if (it.type && it.type == "int") {
									data[it.id] = (data[it.id] == "0"
											|| data[it.id] == "" || data[it.id] == undefined) ? 0
											: parseInt(data[it.id]);
								}

							}
							if (record.get("ZXLB") == 1) {
								data.ZXLB = 1;
								data.ZXLB_text = "西医"
								this.remoteDicStore.baseParams = {
									"ZXLB" : 1
								}
								this.remoteDic.lastQuery = "";
							} else {
								data.ZXLB = 2;
								data.ZXLB_text = "中医"
								this.remoteDicStore.baseParams = {
									"ZXLB" : 2
								}
								this.remoteDic.lastQuery = "";
							}
							var r = new Record(data);
							this.store.insert(row + 1, [ r ])
							this.grid.getSelectionModel().select(row + 1, 1);
							this.grid.startEditing(row + 1, 2);
							// _ctx.tab.activate(0)// 切换到默认tab页
						}
						module.on("happenGYXYSEvent", this.onGYXYSEvent, this);
						module.on("happenTNBYSEvent", this.onTNBYSEvent, this);
						module
								.on("onDiagnosisSave", this.onDiagnosisSave,
										this);
						module.on("onDiagnosisRemove", this.onDiagnosisRemove,
								this);
						this.list = module;
						return this.list.initPanel();

					},

					onGYXYSEvent : function(gxyys) {
						this.fireEvent("onGYXYSEvent", gxyys);
						// 抛到片面处理业务
					},
					onTNBYSEvent : function(tnbys) {
						this.fireEvent("onTNBYSEvent", tnbys);
						// 抛到片面处理业务
					},

					getQuickInputTab : function() {
						var module = this.createModule("clinictab",
								this.clinicDiagnosisQuickInputTab);
						this.clinicTab = module;
						module.opener = this;
						module.on("quickInput", this.doQuickInput, this);
						module.on("afterTabChange",
								this.afterQuickInputTabChange, this);
						module.on("tabchange", this.onTabchangeZXLB, this);
						this.tab = module.initPanel();
						return this.tab;
					},
					afterQuickInputTabChange : function(module) {
						var height = this.panel.getHeight();
						module.grid.setHeight(height - 40);
					},
					onTabchangeZXLB : function(tabPanel, newTab, curTab) {
						this.list.setZXLB(newTab);
					},
					doQuickInput : function(tabName, record) {
						// 当前选中行
						var r = this.list.getSelectedRecord();
						if (!r) {
							MyMessageTip.msg('提示', '请选中需要操作的数据行!', true);
							return;
						}
						if (tabName == "clinicCommonZD" || tabName == "zdmcCY") {// 常用诊断
							if (this.checkDiagnosis(record.get("ZDXH"))) {
								r.set("ZDXH", record.get("ZDXH"));
								r.set("ZDMC", record.get("ZDMC"));
								r.set("ICD10", record.get("ICD10"));
								r.set("ZDBW", "");
								r.set("ZDBW_text", "");
								r.set("ZXLB", record.get("CFLX"));
								if (record.get("CFLX") == 1) {
									r.set("ZXLB_text", "西医");
								} else {
									r.set("ZXLB_text", "中西");
								}
								r.set("JBPB", record.get("JBPB"));
								r.set("JBPB_text", record.get("JBPB_text"));
							}
						} else if (tabName == "clinicPositionZD") {// 部位
							if (r.get("ZXLB") == 2) {
								r.set("ZDXH", "");
								r.set("ZDMC", "");
								r.set("ICD10", "")
							}
							r.set("ZDBW", record.get("ZDBW"));
							r.set("ZDBW_text", record.get("BWMC"));
							r.set("ZXLB", 1);
							r.set("JBPB", record.get("JBPB"));
							r.set("ZXLB_text", "西医");
						} else if (tabName == "clinicAllZD") {
							if (this.checkDiagnosis(record.get("JBXH"))) {
								r.set("ZDXH", record.get("JBXH"));
								r.set("ZDMC", record.get("JBMC"));
								r.set("ICD10", record.get("ICD10"))
								r.set("ZDBW", "");
								r.set("ZDBW_text", "");
								r.set("ZXLB", 1);
								r.set("ZXLB_text", "西医");
								r.set("JBPB", record.get("JBPB"));
								r.set("JBPB_text", record.get("JBPB_text"));
							}

						} else if (tabName == "zdmc") {
							if (this.checkDiagnosis(record.get("JBBS"))) {
								r.set("ZDBW", "");
								r.set("ZDBW_text", "");
								r.set("ZDXH", record.get("JBBS"));
								r.set("ZDMC", record.get("JBMC"));
								r.set("ICD10", record.get("JBDM"))
								r.set("ZXLB", 2);
								r.set("ZXLB_text", "中医");
								r.set("JBPB", record.get("JBPB"));
								r.set("JBPB_text", record.get("JBPB_text"));
							}
						} else if (tabName == "zh" || tabName == "zhCY") {
							if (r.get("ZXLB") == 1) {
								r.set("ZDXH", "");
								r.set("ZDMC", "");
								r.set("ICD10", "")
							}
							r.set("ZDBW", record.get("ZHBS"));
							r.set("ZDBW_text", record.get("ZHMC"));
							r.set("ZXLB", 2);
							r.set("ZXLB_text", "中医");
							r.set("JBPB", record.get("JBPB"));
							r.set("JBPB_text", record.get("JBPB_text"));
						}
					},
					checkDiagnosis : function(ZDXH) {
						var cell = this.list.grid.getSelectionModel()
								.getSelectedCell();
						var modifyIndex = cell[0];
						for ( var i = 0; i < this.list.grid.store.getCount(); i++) {
							if (this.modifyIndex == i)
								continue;
							var record = this.list.grid.store.getAt(i);
							if (record.get("ZDXH") == ZDXH) {
								MyMessageTip.msg('提示', '您选择的诊断已经添加，请勿重复添加!',
										true);
								return false;
							}
						}
						return true;
					},
					addClinicInfo : function() {
						if (!this.isModify && this.clinicData.DEEP == null) {
							MyMessageTip.msg('提示', '请选择诊断级别.', true);
							return;
						}
						var clinicForm = this.midiModules['clinicform'];
						var form = this.form.getForm();
						var store = this.list.grid.getStore();
						// 判断ICD10码是否已经添加,主诊断是否多个
						var data = {};
						data.ZDXH = this.form.ZDXH;
						data.ICD10 = form.findField("ICD10").getValue();
						data.ZDMC = form.findField("ZDMC").getValue();
						data.ZZBZ = form.findField("ZZBZ_Form").getValue() ? 1
								: 0;
						data.ZDBW = form.findField("ZDBW").getValue();
						if (!data.ZDXH) {
							MyMessageTip.msg('提示', '诊断名称和编码不能为空.', true);
							return;
						}
						if (!data.ZDBW) {
							MyMessageTip.msg('提示', '诊断部位不能为空.', true);
							return;
						}
						for ( var i = 0; i < store.getCount(); i++) {
							if (this.isModify) {
								if (this.modifyIndex == i)
									continue;
							}
							var record = store.getAt(i);
							if (record.get("ZDXH") == data.ZDXH) {
								MyMessageTip.msg('提示', '您选择的诊断已经添加，请勿重复添加!',
										true);
								return;
							}
							if (form.findField("ZZBZ_Form").getValue()
									&& record.get("ZZBZ") == 1) {
								MyMessageTip.msg('提示', '不能添加多个主诊断!', true);
								return;
							}
						}
						data.ZDYS = form.findField("ZDYS").getValue();
						data.ZDSJ = form.findField("ZDSJ").getRawValue();
						data.ZDLB = form.findField("ZDLB").getValue();
						data.ZDLB_text = form.findField("ZDLB").getRawValue();

						data.ZDMC = '(' + form.findField("ZDBW").getRawValue()
								+ ')' + data.ZDMC;
						if (this.isModify) {
							var m_record = store.getAt(this.modifyIndex);
							m_record.set("ZDXH", data.ZDXH);
							m_record.set("ICD10", data.ICD10);
							m_record.set("ZDMC", data.ZDMC);
							m_record.set("ZZBZ", data.ZZBZ);
							m_record.set("ZDYS", data.ZDYS);
							m_record.set("ZDSJ", data.ZDSJ);
							m_record.set("ZDLB", data.ZDLB);
							m_record.set("ZDLB_text", data.ZDLB_text);
							m_record.set("ZDBW", data.ZDBW);
							clinicForm.doNew()
							this.isModify = false;
							this.modifyIndex = null;
							this.isChanged = true;
							return;
						}
						var cliniclist = this.midiModules['cliniclist'];
						var o = cliniclist
								.getStoreFields(cliniclist.schema.items)
						var Record = Ext.data.Record.create(o.fields)
						Ext.apply(data, this.clinicData);
						var r = new Record(data);
						if (this.clinicData.DEEP == 0) {
							store.add([ r ]);
						} else {
							store.insert(this.insertRow, [ r ])
							this.insertRow++;// 默认上次操作
						}
						clinicForm.doNew();
						this.isModify = false;
						this.isChanged = true;
						this.modifyIndex = null;
						this.form.ZDXH = null;
						this.tab.activate(0)// 切换到默认tab页
					},
					onDiagnosisSave : function() {
						this.isChanged = false;
						// this.getWin().hide();
						this.list.store.rejectChanges();
						this.list.loadData();
						// this.fireEvent("doSave", "ms",zdStr);
						// this.opener.Backfill("MS_BRZD",zdStr);
						this.onTabShow();
					},
					onDiagnosisRemove : function() {
						this.onTabShow();
						// this.opener.Backfill("MS_BRZD",zdStr);
					},
					beforeClose : function() {
						return this.beforeclose();
					},
					beforeclose : function() {
						// 判断grid中是否有修改的数据没有保存
						if (this.needSave) {
							this.needSave = false;
							if (confirm('当前页面数据已经修改，是否保存?')) {
								if (!this.list.doSave()) {
									return false;
								} else {
									this.list.store.rejectChanges();
									return true;
								}
							} else {
								this.list.store.rejectChanges();
								return true;
							}
						}
						if (this.list.store.getModifiedRecords().length > 0) {
							for ( var i = 0; i < this.list.store.getCount(); i++) {
								if (this.list.store.getAt(i).get("ZDXH")) {
									if (confirm('当前页面数据已经修改，是否保存?')) {
										if (!this.list.doSave()) {
											return false;
										} else {
											break;
										}
									} else {
										break;
									}
								}
							}
							this.list.store.rejectChanges();
						}
						return true;
					},
					onChangeTab : function(zxlb) {
						var recordZDBW = this.list.getSelectedRecord();
						recordZDBW.set("ZDBW", "");
						recordZDBW.set("ZDBW_text", "");
						if (zxlb == 1) {
							this.tab.activate(0);
						} else {
							this.tab.activate(1);
						}

					}
				});