$package("chis.application.def.script.brain")
$import("util.Accredit", "chis.script.BizTableFormView", "chis.script.util.helper.Helper",
		"chis.script.ICCardField", "util.widgets.LookUpField", "chis.script.util.Vtype")
chis.application.def.script.brain.BrainDeformityRecordForm = function(cfg) {
	this.entryName = 'chis.application.def.schemas.DEF_BrainDeformityRecord'
	chis.application.def.script.brain.BrainDeformityRecordForm.superclass.constructor.apply(this,
			[cfg])
	this.nowDate = this.mainApp.serverDate
//	this.loadServiceId = "chis.defBrainService"
//	this.loadAction = "loadBrainDeformityRecordData"
	this.saveServiceId = "chis.defBrainService"
	this.saveAction = "saveBrainDeformityRecord"
}
Ext.extend(chis.application.def.script.brain.BrainDeformityRecordForm, chis.script.BizTableFormView,
		{
			loadData : function() {
				if (!this.exContext.r) {
					return
				}
				this.initDataId = this.exContext.r.get("id")
				this.op == "update"
				var data = this.castListDataToForm(this.exContext.r.data,
						this.schema)
				this.initFormData(data)
			},
			doInitialize : function() {
				this.doNew()
				var rst = util.rmi.miniJsonRequestSync({
							body : {
								ids : this.exContext.ids
							},
							method:"execute",
							serviceId : "chis.defBrainService",
							serviceAction : "initializeBrainDeformityRecordData"
						});
				if (rst.code > 300) {
					this.processReturnMsg(rst.code, rst.msg, this.doNew);
					return
				}
				this.initFormData(rst.json.body)
			},
			getLoadRequest : function() {
				return {
					ids : this.exContext.ids,
					r : this.exContext.r.data
				}
			},
			onReady : function() {
				chis.application.def.script.brain.BrainDeformityRecordForm.superclass.onReady
						.call(this)

				var manaDoctorId = this.form.getForm()
						.findField("manaDoctorId");
				manaDoctorId.on("select", this.changeManaUnit, this);

				var withOtherDeformity = this.form.getForm()
						.findField("withOtherDeformity");
				withOtherDeformity
						.on("select", this.onWithOtherDeformity, this);

			},
			onWithOtherDeformity : function(combo, record, index) {
				var value = combo.value

				var valueArray = value.split(",");
				if (valueArray.indexOf("9") != -1) {
					combo.clearValue();
					if (record.data.key == 0301) {
						combo.setValue({
									key : 9,
									text : "无"
								});
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue({
								key : 9,
								text : "无"
							});
				}
			},
			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method:"execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				var manaUnitIdCombo = this.form.getForm()
						.findField("manaUnitId");
				if (manaUnitIdCombo) {
					manaUnitIdCombo.setValue(result.json.manageUnit)
				}
			},
			doCreate : function() {
				this.doInitialize()
				this.fireEvent("create")
				if (this.form.getTopToolbar()) {
					this.form.getTopToolbar().items.item(0).enable()
				}
			},
			doClose : function() {
				var res = util.rmi.miniJsonRequestSync({
							serviceId : "chis.defBrainService",
							serviceAction : "whetherCanClose",
							method:"execute",
							schema : this.entryName,
							prefix : "Brain",
							id : this.exContext.r.get("id")
						})

				if (res.json.body.totalCount == 0) {
					Ext.Msg.alert("消息", "该档案未完成康复训练流程，不允许结案")
					return
				}
				var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF02_1_1_8")
				cfg.title = "脑瘫残疾结案"
				cfg.autoLoadSchema = false
				cfg.autoLoadData = true
				cfg.isCombined = true
				cfg.showButtonOnTop = true
				cfg.colCount = 3
				cfg.autoFieldWidth = false
				cfg.fldDefaultWidth = 145
				cfg.labelWidth = 110
				cfg.mainApp = this.mainApp
				cfg.lastScore = res.json.body.lastScore
				cfg.firstScore = res.json.body.firstScore
				cfg.exContext = this.exContext
				cfg.defId = this.exContext.r.get("defId")
				cfg.result = res
				var module = this.midiModules["BrainTerminalEvaluateForm"]
				if (!module) {
					$import("chis.application.def.script.brain.BrainTerminalEvaluateForm")
					module = new chis.application.def.script.brain.BrainTerminalEvaluateForm(cfg)
					module.on("save", this.onSave, this)
					this.midiModules["BrainTerminalEvaluateForm"] = module
				} else {
					Ext.apply(module, cfg)
				}
				module.getWin().show()
			},
			onSave : function(entryName, op, json, data) {
				this.fireEvent("close", entryName, op, json, data)
			},
			saveToServer : function(saveData) {
				saveData.phrId = this.exContext.ids.phrId
				saveData.empiId = this.exContext.ids.empiId
				chis.application.def.script.brain.BrainDeformityRecordForm.superclass.saveToServer
						.call(this, saveData)
			}
		});