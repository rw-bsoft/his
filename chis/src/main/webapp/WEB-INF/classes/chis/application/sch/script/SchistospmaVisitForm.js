$package("chis.application.sch.script");

$import("chis.script.BizTableFormView");

chis.application.sch.script.SchistospmaVisitForm = function(cfg) {
	cfg.fldDefaultWidth = 170;
	cfg.labelWidth = 100;
	cfg.colCount = 2;
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	chis.application.sch.script.SchistospmaVisitForm.superclass.constructor.apply(this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadDataByLocal", this.onLoadData, this);
};

Ext.extend(chis.application.sch.script.SchistospmaVisitForm, chis.script.BizTableFormView, {
			doAdd : function() {
				this.initDataId = null;
				this.doNew();
				this.setButton([0, 1], true);
			},

			doSave : function() {
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				if (this.op == "create") {
					var date = this.form.getForm().findField("visitDate")
							.getValue();
					if (!this.fireEvent("beforeSaveRecord", date)) {
						alert('同一天不允许新建两条随访记录');
						return;
					}
				}
				chis.application.sch.script.SchistospmaVisitForm.superclass.doSave.call(this);
			},

			loadFormData : function(data) {
				this.initDataId = null;
				this.doNew();
				if (data) {
					this.initFormData(data);
				}
				this.onLoadData();
			},

			onLoadData : function() {
				var closeFlag = this.exContext.args.closeFlag;
				var status = this.exContext.args.status;
				if (closeFlag == "1" || status == "1"
						|| !this.exContext.args.doFlag) {
					this.setButton([0, 1], false);
				} else {
					this.setButton([0, 1], true);
				}
			},

			onBeforeSave : function(entryName, op, saveData) {
				saveData.schisRecordId = this.exContext.args.schisRecordId;
				saveData.empiId = this.exContext.ids.empiId;
				return saveData;
			},

			setButton : function(m, flag) {
				var btns;
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					btns = this.form.getTopToolbar().items;
				} else {
					btns = this.form.buttons;
				}

				if (btns) {
					var n = btns.getCount();
					for (var i = 0; i < m.length; i++) {
						var btn = btns.item(m[i]);
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				}
			},

			onReady : function() {
				chis.application.sch.script.SchistospmaVisitForm.superclass.onReady.call(this);
				var visitDoctor = this.form.getForm().findField("visitDoctor");
				if (visitDoctor) {
					visitDoctor.on("select", this.onVisitDoctorSelect, this);
				}
			},

			onVisitDoctorSelect : function(combo, node) {
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
				var manageUnit = result.json.manageUnit;
				this.data.visitUnit = manageUnit.key;
			}
		});