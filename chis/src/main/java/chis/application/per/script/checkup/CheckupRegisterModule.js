$package("chis.application.per.script.checkup");

$import("chis.script.BizCombinedModule2");

chis.application.per.script.checkup.CheckupRegisterModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.split = false
	chis.application.per.script.checkup.CheckupRegisterModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.height = 455;
	this.itemHeight = 215;
	this.itemCollapsible = false;
	// this.saveServiceId = "chis.checkupRecordService";
	// this.serviceAction = "saveCheckupRecord";
};

Ext.extend(chis.application.per.script.checkup.CheckupRegisterModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.per.script.checkup.CheckupRegisterModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("doNew", this.onFormDoNew, this);
				this.form.on("create", this.onCreate, this);
				this.form.on("revokeSave", this.onRevokeSave, this);
				this.form.on("finalCheck", this.onFinalCheck, this);
				this.form.on("bady", function(op, readonly) {
							this.fireEvent("bady", op, readonly);
						}, this);
				this.list = this.midiModules[this.actions[1].id];
				return panel;
			},
			loadData : function() {
				this.form.ehrStatus = this.ehrStatus;
				this.list.ehrStatus = this.ehrStatus;
				if (!this.exContext.args.empiId) {
					this.exContext.args.empiId = this.exContext.ids.empiId;
					this.exContext.args.phrId = this.exContext.ids.phrId;
				}
				// get btn control
				var actions = this.getCheckupRegisterControl();

				if (this.form) {
					this.list.ready = this.exContext.args.ready;
					this.form.initDataId = this.exContext.args.initDataId;
					Ext.apply(this.form.exContext, this.exContext);
					this.form.exContext.control = actions.PER_CheckupRegister_control;

					if (!this.form.initDataId) {
						this.form.doCreate();
					} else {
						this.form.loadData();
					}
				}
				if (this.list) {
					this.list.ready = this.exContext.args.ready;
					this.list.initDataId = this.exContext.args.initDataId;
					if (!this.list.initDataId) {
						this.exContext.args.totalCheckupDate = undefined;
						this.exContext.args.status = undefined;
					}
					Ext.apply(this.list.exContext, this.exContext);
					this.list.exContext.control = actions.PER_ICD_control;

					this.list.loadData();
				}
			},
			doNew : function() {
				this.exContext.args = "";
				this.form.doNew();
				this.list.clear();
				this.list.resetButtons();
			},
			onFormDoNew : function() {
				this.fireEvent("doNew");
			},
			onFormSave : function(entryName, op, saveData) {
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				var checkUp = saveData;
				var icd = this.list.getSaveData();
				var saveDataBody = {
					"checkUp" : checkUp,
					"icd" : icd
				};
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							method : "execute",
							checkupSchema : this.checkupSchema,
							icdSchema : this.icdSchema,
							op : op,
							body : saveDataBody
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body) {
								this.form.initFormData(body);
								this.fireEvent("save", this.checkupSchema, op,
										json, saveDataBody, this);
							}
						}, this);
			},
			onCreate : function() {
				this.list.clear();
				this.list.setTopBtnStatus();
				this.fireEvent("create");
			},
			onRevokeSave : function(entryName, op, json, data) {
				this.fireEvent("save", entryName, op, json, data);
			},
			onFinalCheck : function(entryName, op, json, data) {
				this.fireEvent("finalCheck", op, json, data, this);
			},

			getCheckupRegisterControl : function() {
				var body = {};
				body.empiId = this.exContext.args.empiId;
				body.phrId = this.exContext.args.phrId;
				body.checkupNo = this.exContext.args.checkupNo;
				body.recordNum = this.list.store.getCount();
				body.status = this.exContext.args.status;
				body.checkupOrganization = this.exContext.args.checkupOrganization;
				body.totalCheckupDate = this.exContext.args.totalCheckupDate;

				this.panel.el.mask("正在获取操作权限...", "x-mask-loading");
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.checkupRecordService",
							serviceAction : "getPerReegisterControl",
							method : "execute",
							body : body
						});
				this.panel.el.unmask();
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				return result.json.body;
			}
		});