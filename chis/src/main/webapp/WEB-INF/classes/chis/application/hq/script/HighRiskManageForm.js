$package("chis.application.mov.script.batch");

$import("chis.script.BizTableFormView",
		"util.widgets.LookUpField");

chis.application.hq.script.HighRiskManageForm = function(cfg) {
	cfg.autoLoadSchema = false
	chis.application.hq.script.HighRiskManageForm.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.hq.script.HighRiskManageForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				var form = this.form.getForm();
				var manaDoctorId = form.findField("manaDoctorId");
				if(manaDoctorId){
					manaDoctorId.on("select", this.changeManaUnit, this);
				}
				chis.application.hq.script.HighRiskManageForm.superclass.onReady.call(this);
			},
			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						});
				this.setManaUnit(result.json.manageUnit)
			},
			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("manaUnitId");
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.setValue({
								key : "",
								text : ""
							});
					return;
				}

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit)
					combox.disable();
				}
			}
			,doSavedata: function(){
				var savedata=this.getFormData();
				if (!savedata) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.hqQueryService",
					serviceAction : "saveHighRiskRecord",
					method:"execute",
					savedata :savedata
				})
				if(result.code>300){
					MyMessageTip.msg("提示", result.msg, true);
				}else{
					this.fireEvent("savehighrisk");
					this.win.hide();
				}
			}
			,	initFormData:function(data){
		this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
		Ext.apply(this.data,data)
		this.initDataId = this.data[this.schema.pkey]
		var form = this.form.getForm()
		var items = this.schema.items
		var n = items.length
		for(var i = 0; i < n; i ++){
			var it = items[i]
			var f = form.findField(it.id)
			if(f){
				var v = data[it.id]
				if (v != undefined) {
					if((it.type == 'date' || it.xtype == 'datefield') && typeof v == 'string' && v.length > 10){
						v = v.substring(0,10)
					}
					if((it.type == 'datetime' || it.type == 'timestamp' || it.xtype == 'datetimefield') && typeof v == 'string' && v.length > 19){
						v = v.substring(0,19)
					}
					f.setValue(v)
				}
				if (this.initDataId) {
					if (it.update == false || it.update == "false") {
						f.disable();
					}
				}
			}
		}
		this.setKeyReadOnly(true)	
		this.startValues = form.getValues(true);
		this.resetButtons(); // ** 用于页面按钮权限控制
		this.focusFieldAfter(-1, 800);
	}
		});