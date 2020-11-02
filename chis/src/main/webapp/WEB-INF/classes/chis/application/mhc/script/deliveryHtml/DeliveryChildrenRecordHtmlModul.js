/**
 * 新生儿访视整合模块页面
 * 
 * @author : zhouw
 */
$package("chis.application.mhc.script.deliveryHtml")
$import("chis.script.BizCombinedTabModule")
chis.application.mhc.script.deliveryHtml.DeliveryChildrenRecordHtmlModul = function(
		cfg) {
	cfg.autoLoadData = false
	cfg.itemWidth = 200;
	cfg.isAutoScroll = true;
	chis.application.mhc.script.deliveryHtml.DeliveryChildrenRecordHtmlModul.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
	this.on("loadModule", this.onLoadModule, this)
	this.flag = false;// 防止点击保存的时候，循环
}
Ext
		.extend(
				chis.application.mhc.script.deliveryHtml.DeliveryChildrenRecordHtmlModul,
				chis.script.BizCombinedTabModule, {
					initPanel : function() {
						var panel = chis.application.mhc.script.deliveryHtml.DeliveryChildrenRecordHtmlModul.superclass.initPanel
								.call(this)
						this.panel = panel;
						this.list = this.midiModules[this.otherActions.id];
						this.grid = this.list.grid;
						this.list.on("loadData", this.onLoadGridData, this)
						this.list.grid.on("rowClick", this.onRowClick, this)
						return panel;
					},
					onLoadGridData : function(store) {
						if (this.flag) {
							return;
						}
						if (store.getCount() == 0) {
							this
									.changeSubItemDisabled(true,
											this.actions[0].id);
							this.activeModule(0);
							var data = {};
							data["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
							data["empiId"] = this.exContext.empiData.empiId;
							this.data = data;
							this.proccess(data);
							return;
						}

						var r = store.getAt(0).data;
						var exc = {
							babyId : r.babyId,
							weightx : r.weight = null ? "" : r.weight,
							lengthx : r.length = null ? "" : r.length
						}
						this.changeSubItemDisabled(true,this.actions[0].id);
							this.activeModule(0);
						this.exContext.args = exc;
						this.proccess(r);

					},
					onRowClick : function() {
						// 判断第二个tab是否激活
//						if (!this.data) {
//							return;
//						}
						var r = this.grid.getSelectionModel().getSelected().data;
						var exc = {
							babyId : r.babyId,
							weightx : r.weight = null ? "" : r.weight,
							lengthx : r.length = null ? "" : r.length
						}
						this.exContext.args = exc;
						this.proccess(r);

					},
					proccess : function(r) {
						// tabs.getActiveTab();// 得到当前选项卡
						if (!r) {
							return;
						}
						Ext.apply(this.recordModule.exContext, this.exContext);
						if (this.visitModule) {
							Ext.apply(this.visitModule.exContext,
									this.exContext);
							this.visitModule.refresh();
						}
						if (r.babyId) {
							var v = {};
							v["babyId"] = r.babyId;
							// this.activeModule(0);
							var resDatas = this.selectBabyVisitInfos(v);
							var data={};
							data["motherBirth"]=resDatas["motherBirth"];
							data["motherName"]=resDatas["motherName"];
							data["motherJob"]=resDatas["motherJob"];
							data["motherPhone"]=resDatas["motherPhone"];
							//console.log(this.exContext)
							data["motherCardNo"]=this.exContext.empiData.idCard;
							data["fatherJob"]=resDatas["fatherJob"];
							data["fatherBirth"]=resDatas["fatherBirth"];
							data["fatherPhone"]=resDatas["fatherPhone"];
							data["fatherName"]=resDatas["fatherName"];
							//resDatas["motherCardNo"]=this.exContext.empiData.idCard;
                            this.recordModule.infoValue = data;//缓存
							this.recordModule.doCreate();
							this.recordModule.initFormData(resDatas);
							this.changeSubItemDisabled(false,
									this.actions[0].id);// 点击左边的列表的某行数据，激活随访的tab
							return;
						} else {
							var resData = this.selectBabyVisitInfo(r);
							var data = {};
							Ext.apply(data, resData);
							data["pregnantId"] = this.data["pregnantId"];
							this.recordModule.infoValue = data;// 缓存，用于第一个tab页，新建
							this.recordModule
									.doNew(this.recordModule.idPostfix);
							this.recordModule.initFormData(data);
							//
						}

					},
					selectBabyVisitInfo : function(data) {
						if (!data) {
							return;
						}
						var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.pregnantRecordService",
									serviceAction : "initBabyVisitInfo",
									method : "execute",
									body : data
								});
						var info = result.json.body;
						if (info) {
							return info;
						}
					},
					selectBabyVisitInfos : function(data) {
						if (!data) {
							return;
						}
						var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.pregnantRecordService",
									serviceAction : "initBabyVisitInfoHtml",
									method : "execute",
									body : data
								});
						var info = result.json.body;
						if (info) {
							return info;
						}
					},
					onLoadModule : function(moduleId, module) {

						if (moduleId == this.actions[0].id) {
							this.recordModule = module;
							module.on("doCreate", this.onDoCreate, this);
							module.on("save", this.onSave, this);
							module.on("close", this.onClose, this);
							module.on("closeFlag", this.onCloseFlag, this);
							module
									.on("processButton", this.processButton,
											this);
						} else {
							this.visitModule = module;
							Ext.apply(this.visitModule.exContext,
									this.exContext);

							this.visitModule.onTabChangeTo();

						}
					},
					controlVisitModuleButton : function() {
						if (this.recordModule.form.getTopToolbar().items
								.item(0).disabled == true) {
							this.visitModule.midiModules[this.visitModule.actions[0].id].form
									.getTopToolbar().items.item(0).disable()
						}
						if (this.recordModule.form.getTopToolbar().items
								.item(0).disabled == false) {
							this.visitModule.midiModules[this.visitModule.actions[0].id].form
									.getTopToolbar().items.item(0).enable()
						}
					},
					processButton : function() {
						if (!this.recordModule.form.getTopToolbar()) {
							return;
						}

						var btns = this.recordModule.form.getTopToolbar().items;
						if (btns) {
							var n = btns.getCount()
							for (var i = 0; i < n; i++) {
								if (i != 1) {
									var btn = btns.item(i)
									btn.disable()
								}
							}
						}
					},
					onSave : function(entryName, op, json, data) {
						if (!data) {
							return
						}

						var exc = {
							babyId : data.babyId,
							weightx : data.weight = null ? "" : data.weight,
							lengthx : data.length = null ? "" : data.length
						}
						this.exContext.args = exc;
						Ext.apply(this.recordModule.exContext, this.exContext);
						if (this.visitModule) {// 点击保存的时候（第二次以后），传值到随访记录页
							Ext.apply(this.visitModule.exContext,
									this.exContext);
							this.visitModule.refresh();
						}
						this.list.loadData();
						this.flag = true;
						this.changeSubItemDisabled(false, this.actions[0].id);// 保存的时候打开第二个tab
					},
					onDoCreate : function(entryName, op, json, data) {
						this.flag = false;
						this.changeSubItemDisabled(true, this.actions[0].id);// 关闭第二个tab
					}

				});