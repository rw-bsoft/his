//妻子检查表单
$package("chis.application.hc.script");

$import("chis.script.BizFieldSetFormView"
);

chis.application.hc.script.WProgestationCheckForm = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.labelAlign = "left";
	cfg.labelWidth = 140;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	cfg.entryNames = "chis.application.hc.schemas.hc_w_progestationcheck";
	chis.application.hc.script.WProgestationCheckForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("aboutToSave", this.onAboutToSave, this);
	this.on("afterCreate", this.onAfterCreate, this);
	this.on("doNew", this.onDoNew, this);
	this.sex = this.exContext.empiData.sexCode;
	this.serviceId="chis.healthCheckService";
};

Ext.extend(chis.application.hc.script.WProgestationCheckForm,
		chis.script.BizFieldSetFormView, {
			onDoNew : function() {
				
				this.data.empiId = this.exContext.ids.empiId;
				this.data.phrId = this.exContext.ids.phrId;
			},

			doAdd : function() {
				this.fireEvent("add", this);
			},

			loadInitData : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "initializeCreateGroup",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId,
								autoCreate : this.autoCreate
							}
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.loadInitData);
					return;
				}
				if (this.form.getTopToolbar()) {
					var bts = this.form.getTopToolbar().items;
					bts.items[0].enable();
				}
				this.doNew();
				this.initFormData(result.json.body);
				this.validate();
				this.sn = result.json.body.sn;
				return result.json.body;
			},
			doSave : function() {
				this.data.phrId = this.exContext.ids.phrId;
				this.data.empiId = this.exContext.ids.empiId;
				chis.application.hc.script.WProgestationCheckForm.superclass.doSave
						.call(this);
			},
			saveToServer : function(saveData) {
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
					this.save(saveData);
			},

			save : function(saveData) {
				this.fireEvent("aboutToSave", this.entryName, this.op,
								saveData);
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : 'saveWProgestationCheck',
							method : "execute",
							schema : this.entryName,
							body : saveData,
							op : this.op
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg, this.save,
										[saveData]);
								return;
							}
							this.data.recordId = json.body.recordId;
							this.op = "update";
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.fireEvent("afterCreate", this.entryName,
									this.op, json, this.data);
						}, this);
			},

			onAfterCreate : function(entryName, op, json) {
				this.op = "update";
				this.autoCreate = false;
			},

			onAboutToSave : function(entryName, op, saveData) {

			},

			onReady : function() {
				chis.application.hc.script.WProgestationCheckForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var sgform = form.findField("SG");
				sgform.on("blur", this.onsgtzChange, this);
				var tzform = form.findField("TZ");
				tzform.on("blur", this.onsgtzChange, this);
			},
			onsgtzChange:function(field){
				if (!field.validate()) {
					return ;
				}
				var sg =this.form.getForm().findField("SG").value;;
				var tz =this.form.getForm().findField("TZ").value;
				if (sg == "" || tz == "") {
					return
				}
				var b = (tz / (sg * sg / 10000)).toFixed(2);
				this.form.getForm().findField("TZZS").setValue(b);
			}
		});