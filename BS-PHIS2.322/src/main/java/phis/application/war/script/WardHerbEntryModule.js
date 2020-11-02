$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.WardHerbEntryModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.modal = this.modal = true;
	cfg.listIwardate = true;
	this.removeRecords = [];
	phis.application.war.script.WardHerbEntryModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('winShow', this.onWinShow, this);
}
Ext.extend(phis.application.war.script.WardHerbEntryModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var tab = this.opener.tabModule.tab.getActiveTab();
				if (this.panel) {
					if(tab.id!="longAdviceTab"&&tab.id!="d_longAdviceTab"){
						this.list.grid.getColumnModel().setHidden(
										this.list.grid.getColumnModel()
												.getIndexById("SRCS"), true);
					}else{
						this.list.grid.getColumnModel().setHidden(
									this.list.grid.getColumnModel()
											.getIndexById("SRCS"), false);
					}
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										width : 180,
										height : 45,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 180,
										items : this.getList()
									}
							// , {
							// layout : "fit",
							// border : false,
							// split : true,
							// title : '',
							// region : 'east',
							// width : 480,
							// items : this.getPrintView()
							// }
							],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				if(tab.id!="longAdviceTab"&&tab.id!="d_longAdviceTab"){
						this.list.grid.getColumnModel().setHidden(
										this.list.grid.getColumnModel()
												.getIndexById("SRCS"), true);
				}else{
					this.list.grid.getColumnModel().setHidden(
								this.list.grid.getColumnModel()
										.getIndexById("SRCS"), false);
				}
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("HerbEntryForm",
						this.refHerbEntryForm);
				this.form.opener = this;
				var formModule = this.form.initPanel();
				var form = this.form.form;
				var cfts = form.getForm().findField("CFTS");
				if (cfts) {
					cfts.un("specialkey", this.form.onFieldSpecialkey, this.form);
					cfts.on("specialkey", function(cfts, e) {
								var key = e.getKey();
								if (key == e.ENTER) {
									this.doCreate();
								}
							}, this);
					cfts.on("change",function(){
						this.list.setCFTS(cfts.getValue());
						this.needSave = true;
					},this);
				}
				var sypc = form.getForm().findField("SYPC");
				if (sypc) {
					sypc.on("select",function(){
						if(!this.loading){
							this.list.setMRCS(sypc.getValue());
						}
						this.needSave = true;
					},this);
// sypc.on("change",function(){
// this.needSave = true;
// alert(this.needSave+"###");
// },this);
				}
// var ypzs = form.getForm().findField("YPZS");
// if (ypzs) {
// ypzs.on("select",function(){
// this.needSave = true;
// },this);
// }
				var ypyf = form.getForm().findField("YPYF");
				if (ypyf) {
					ypyf.on("select",function(){
						this.needSave = true;
					},this);
				}
				
				
				return formModule;
			},
			getList : function() {
				this.list = this.createModule("refHerbEntryList",
						this.refHerbEntryList)
				this.list.opener = this;
				this.list.exContext = this.exContext;
				this.list.grid = this.list.initPanel();
				// this.list.doCreate();
				return this.list.grid;
			},
			getPrintView : function() {
				this.printView = this.createModule("refHerbEntryPrint",
						this.refHerbEntryPrint)
				return this.printView.initPanel();
			},
			doCreate : function() {
				this.list.doCreate();
			},
			doNew : function() {
				this.opener.doNewGroup();
				if(this.needSave){
					MyMessageTip.msg("提示", "当前明细已发生变化,请先保存", true);
					return;
				}
				// this.setButtonsState(["create", "remove", "commit", "new"],
				// true);
				this.form.op = "create";
				this.form.doNew();
				this.list.op = "create";
				this.list.clear();
				this.list.editRecords = [];
				this.type="create";
				this.removeRecords = [];
				
				
				var tab = this.opener.tabModule.tab.getActiveTab();
				if (!tab) {
					return;
				}
				var list = this.opener.tabModule.midiModules[tab.id];
                this.data = list.store.getAt(list.store.getCount()-1).data;
			},
			doModuleNew : function(){
				this.form.op = "create";
				this.form.doNew();
				this.list.op = "create";
				this.list.clear();
				this.list.editRecords = [];
				this.type="create";
				this.removeRecords = [];
			},
			onWinShow : function(){
				this.loading = true;
				var herbList = this.herbList;
				var tab = this.opener.tabModule.tab.getActiveTab();
				if (!tab) {
					return;
				}
				var openerList = this.opener.tabModule.midiModules[tab.id];
				var dic_sypc;
				// ksdm.setDisabled(false);
				var items = openerList.schema.items;
				for ( var i = 0; i < items.length; i++) {
					var it = items[i]
					if (it.id == 'SYPC') {
						dic_sypc = it.dic;
					}
				}
				var sypc = this.form.form.getForm().findField("SYPC");
				sypc.store.proxy = new util.dictionary.HttpProxy(
						{
							method : "GET",
							url : util.dictionary.SimpleDicFactory
									.getUrl(dic_sypc)
						})
				sypc.store
						.load();
// alert(11);
				if(herbList.length==0)return;
				for (var i = 0; i < herbList.length; i++) {
					var herb = herbList[i];
					if (herb.JZ == 1) {
						herb["JZ_text"] = "包"
					} else if (herb.JZ == 2) {
						herb["JZ_text"] = "冲"
					} else if (herb.JZ == 3) {
						herb["JZ_text"] = "煎"
					} else if (herb.JZ == 4) {
						herb["JZ_text"] = "后下"
					}
				}
				
				
				this.list.loadData();
				var _this = this;
				var doInputInfo = function() {
					_this.form.form.getForm().findField("SYPC")
							.setValue(herbList[0].SYPC);
// _this.form.form.getForm().findField("YPZS")
// .setValue(herbList[0].YPZS);
					if (herbList[0].YPYF) {
						_this.form.form.getForm().findField("YPYF")
								.setValue(herbList[0].YPYF);
					}
					if (herbList[0].CFTS) {
						_this.form.form.getForm().findField("CFTS")
								.setValue(herbList[0].CFTS);
					}
					_this.needSave = false;
					_this.loading = false;
				}
				doInputInfo.defer(1000);
			},
			doUpdate : function(herbList) {
				this.herbList = herbList;
			},
			doRemove : function(item, e) {
				var cm = this.list.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.list.getSelectedRecord()
				if (r == null) {
					return
				}
				this.needSave = true;
				if (r.data.JLXH) {
					// 记录后台需要删除的处方识别
					this.removeRecords.push({
								_opStatus : "remove",
								JLXH : r.data.JLXH
							});
				}
				this.list.store.remove(r);
				this.list.grid.getView().refresh();
				// 移除之后焦点定位
				var count = this.list.store.getCount();
				if (count > 0) {
					cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
				}
			},
			doSave : function() {
				this.list.grid.stopEditing();
				var tab = this.opener.tabModule.tab.getActiveTab();
				if (!tab) {
					return;
				}
				this.patinetData = {
						BRKS : this.opener.form.form.getForm().findField("BRKS")
								.getValue(),
						BRBQ : (this.openBy == 'doctor' ? this.exContext.brxx
								.get("BRBQ") : this.mainApp['phis'].wardId),
						BRCH : this.opener.form.form.getForm().findField("BRCH")
								.getValue(),
						ZYH : this.initDataId
					}
				// console.debug(this.form.form.getForm().findField("SYPC"))
				if (this.form.getFormData()) {
					var listData = [];
					if (!this.form.form.getForm().findField("SYPC").getValue()) {
						MyMessageTip.msg("提示", "频次不能为空!", true);
						 
						return;
					}
// if (!this.form.form.getForm().findField("YPZS").getValue()) {
// MyMessageTip.msg("提示", "煎法不能为空!", true);
//						 
// return;
// }
					if (!this.form.form.getForm().findField("YPYF").getValue()) {
						MyMessageTip.msg("提示", "服法不能为空!", true);
						 
						return;
					}
					if (!this.form.form.getForm().findField("CFTS").getValue()) {
						MyMessageTip.msg("提示", "贴数不能为空!", true);
						 
						return;
					}

					var count = this.list.store.getCount();
					if (count == 0&&this.removeRecords.length==0) {
						MyMessageTip.msg("提示", "明细不能为空!", true);
						 
						return;
					}
					if (count == 1
							&& ((!this.list.store.getAt(0).data["YPXH"])||(!this.list.store.getAt(0).data["YZMC"]))&&this.removeRecords.length==0) {
						MyMessageTip.msg("提示", "明细不能为空!", true);
						 
						return;
					}
					for (var i = 0; i < count; i++) {
						var data = this.list.store.getAt(i).data;
						if ((!data["YPXH"]) || (!data["YZMC"])) {
							continue;
						}
						if(data["YCJL"] == undefined
								|| data["YCJL"] == 0){
							MyMessageTip.msg("提示", "第" + (i + 1)
									+ "行剂量不能为空!", true);
							return;
						}
						if (data["LSYZ"] != 1 && !data["SRCS"]) {
							MyMessageTip.msg("提示", "首日次数不能为空!错误行 " + (i + 1)
											+ " 。", true);
							return false;
						}
						if (data["LSYZ"] != 1 && data["SRCS"] > data["MRCS"]) {
							MyMessageTip.msg("提示", "首日次数不能大于频次的每日次数!错误行 "
											+ (i + 1) + " 。", true);
							return false;
						}
						if(data["msg"]){
							MyMessageTip.msg("提示",data["msg"], true);
							return;
						}
						data["SYPC"] = this.form.form.getForm()
								.findField("SYPC").getValue();
						data["SYPC_text"] = this.form.form.getForm()
								.findField("SYPC").lastSelectionText;
// data["YPZS"] = this.form.form.getForm()
// .findField("YPZS").getValue();
// data["YPZS_text"] = this.form.form.getForm()
// .findField("YPZS").lastSelectionText;
						data["YPYF"] = this.form.form.getForm()
								.findField("YPYF").getValue();
						data["YPYF_text"] = this.form.form.getForm()
								.findField("YPYF").lastSelectionText;
						data["CFTS"] = this.form.form.getForm()
								.findField("CFTS").getValue();
						if (data.JLXH > 0) {
							data['_opStatus'] = 'update';
						} else {
							if(tab.id=="EmergencyMedicationTab"||tab.id=="d_EmergencyMedicationTab"){
								data["XMLX"] = 2;
							}else if(tab.id=="DischargeMedicationTab"||tab.id=="d_DischargeMedicationTab"){
								data["XMLX"] = 3;
							}else{
								data["XMLX"] = 1;
							}
							data['_opStatus'] = 'create';
						}
						Ext.apply(this.data, data);
						if(!this.data.TPN||this.data.TPN==null){
							this.data.TPN = 0;
						}
						if(!this.data.YJXH||this.data.YJXH==null){
							this.data.YJXH = 0;
						}
						Ext.apply(data, this.data);
						if(data.ZFYP){
							data.ZFYP = 1;
						}else{
							data.ZFYP = 0;
						}
						listData.push(data);
					}
					this.panel.el.mask("正在保存数据...", "x-mask-loading")
					for (var i = 0; i < this.removeRecords.length; i++) {
						listData.push(this.removeRecords[i]);
					}
					var resData = phis.script.rmi.miniJsonRequestSync({
						serviceId : "wardPatientManageService",
						serviceAction : "saveWardPatientInfo",
						body : {
							brxx : this.patinetData,
							yzxx : listData,
							fjxx : []
						}
					});
					this.panel.el.unmask();
					this.needSave = false;
					var code = resData.code;
					var msg = resData.msg;
					var json = resData.json;
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return false;
					}
					this.removeRecords = [];
					this.list.store.rejectChanges();
					if(json.YZZH){
						this.data.YZZH = json.YZZH;
					}
					this.list.refresh();
					var openerList = this.opener.tabModule.midiModules[tab.id];
					openerList.refresh();
				}
			},
// doCommitUpdate : function(body) {
// var tab = this.opener.tabModule.tab.getActiveTab();
// if (!tab) {
// return;
// }
// var openerList = this.opener.tabModule.midiModules[tab.id];
// openerList.removeEmptyRecord();
// var store = openerList.grid.getStore();
// var storeData = store.data;
// var maxIndex = store.getCount();
// var yzzh = 1;
// var upRowItem = storeData.itemAt(maxIndex - 1);
// if (maxIndex > 0) {
// yzzh = upRowItem.get("YZZH_SHOW") + 1;
// }
// for (var i = 0; i < body.length; i++) {
// if (body[i].YZZH) {
// yzzh=body[i].YZZH;
// }
// }
// for (var i = 0; i < body.length; i++) {
// openerList.doCreate();
// body[i].YZZH = yzzh;
// if (this.opener.openBy == "nurse"
// && this.exContext.brxx.get("ZSYS")) {
// body[i].YSGH = this.exContext.brxx.get("ZSYS");
// body[i].YSGH_text = this.exContext.brxx
// .get("ZSYS_text");
// } else {
// if (this.exContext.docPermissions.KCFQ == 1) {
// body[i].YSGH = this.mainApp.uid;
// body[i].YSGH_text = this.mainApp.uname;
// }
// }
//
// // 设置公共属性
// body[i].JGID = this.mainApp['phisApp'].deptId;
// body[i].ZYH = this.initDataId;
// body[i].YSBZ = (this.openBy == "nurse" ? 0 : 1);
// body[i].YPLX = 3;// 判断药品项目依据
// body[i].CZGH = this.mainApp.uid;
// // body[i].CZGH_text", this.mainApp.uname);
// body[i].KSSJ = new Date();
// // body[i].JFBZ", 3);
// body[i].LSYZ = openerList.adviceType == 'longtime' ? 0 : 1;
// body[i].KSBZ = 0;
// body[i].YJXH = 0;
// body[i].XMLX = 1;
//
// body[i].YPXH = body[i].YPXH;
// body[i].YZMC = body[i].YZMC;
// body[i].YCJL = parseFloat(body[i].YPJL);
// body[i].JLDW = body[i].JLDW;
// body[i].YCSL = parseFloat(body[i].YCSL);
// body[i].YFDW = body[i].YFDW;
// body[i].YPYF = body[i].YPYF;
// body[i].YPYF_text = body[i].YPYF_text;
// body[i].SYPC = body[i].SYPC;
// body[i].SYPC_text = body[i].SYPC_text;
// body[i].YFBZ = body[i].YFBZ;
// body[i].YPDJ = parseFloat(body[i].YPDJ);
// body[i].YPCD = body[i].YPCD;
// // body[i].set('YPCD_text', body[i].YPCD_text;
// body[i].FYFS = body[i].FYFS;
// // body[i].FYFS_text=body[i].FYFS_text)
// var yfxx = {};
// yfxx.YFSB = this.exContext.yfxx.bqxyf;
// yfxx.YFMC = this.exContext.yfxx.bqxyf_text;
// body[i].YFSB = yfxx.YFSB;
// body[i].YFSB_text = yfxx.YFMC;
// var sypc = openerList.grid.getColumnModel()
// .getColumnById("SYPC").editor;
// var sypc_rec = sypc.findRecord("key", body[i].SYPC);
// body[i].MRCS = sypc_rec.get("MRCS");
// body[i].SRCS = sypc_rec.get("MRCS");
// body[i].YZZXSJ = sypc_rec.get("ZXSJ");
//
// body[i].CFTS = body[i].CFTS;
// body[i].YPZS = body[i].YPZS;
// body[i].YPZS_text = body[i].YPZS_text;
// body[i].JZ = body[i].JZ;
// body[i].JZ_text = body[i].JZ_text;
// }
// // openerList.grid.getView().refresh()// 刷新行号
// // this.grid.startEditing(row, 3);
// // this.onRowClick();
// // this.doClose();
// // console.debug(body);
// var r = phis.script.rmi.miniJsonRequestSync({
// serviceId : "wardPatientManageService",
// serviceAction : "saveHerbInfo",
// body : body
// });
// if (r.code > 300) {
// this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
// return false;
// } else {
// var tab = this.opener.tabModule.tab.getActiveTab();
// if (!tab) {
// return;
// }
// // var openerList = this.opener.tabModule.midiModules[tab.id];
// // openerList.grid.getView().refresh()// 刷新行号
// // this.grid.startEditing(row, 3);
// // this.onRowClick();
// this.opener.tabModule.moduleLoadData(tab.id);
// this.doClose();
// }
//
// },
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doClose : function() {
				if(this.needSave){
					Ext.Msg.confirm("确认", "当前明细已发生变化,是否保存当前信息?",function(btn) {
						if (btn == 'yes') {
							this.doSave();
						}else{
							this.needSave = false;
							this.doClose();
						}
					},this);
					return;
				}
// this.beforeClose();
				this.removeRecords = [];
				this.getWin().hide();
// return true;
			},
// beforeClose : function() {
// if (this.list.editRecords && this.list.editRecords.length > 0) {
// if (confirm('数据已经修改，是否保存?')) {
// return this.doSave()
// } else {
// return true;
// }
// }
// return true;
// },
			doPrint : function() {
				if(!this.data.YZZH){
					MyMessageTip.msg("提示", "当前信息未保存，请保存后再打印!", true);
					return;
				}
				if(this.needSave){
					Ext.Msg.confirm("确认", "当前明细已发生变化,是否保存当前信息?",function(btn) {
						if (btn == 'yes') {
							this.doSave();
						}else{
							this.needSave = false;
							this.doPrint();
						}
					},this);
					return;
				}
				var module = this.createModule("herbEntryPrint",
						this.refHerbEntryPrint)
				var formData = this.form.getFormData();
				var listData = [];
				var count = this.list.store.getCount();
				if (count == 0) {
					MyMessageTip.msg("提示", "明细不能为空!", true);
					 
					return;
				}
				if (count == 1
						&& this.list.store.getAt(0).data["YPXH"] == undefined) {
					MyMessageTip.msg("提示", "明细不能为空!", true);
					 
					return;
				}
				for (var i = 0; i < count; i++) {
					if (this.list.store.getAt(i).data["YPXH"]) {
						if (this.list.store.getAt(i).data["YPXH"] == undefined
								|| this.list.store.getAt(i).data["YCSL"] == undefined
								|| this.list.store.getAt(i).data["YCSL"] == 0) {
							MyMessageTip.msg("提示", "第" + (i + 1)
											+ "行名称、数量不能为空!", true);
							 
							return;
						}
					}
					listData.push(this.list.store.getAt(i).data);
				}
				module.formData = formData;
				module.listData = listData;
				module.exContext = this.exContext;
				module.data = this.data;
				module.initPanel();
				module.doPrint();
			},
			// 设置按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.panel.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			}
		});