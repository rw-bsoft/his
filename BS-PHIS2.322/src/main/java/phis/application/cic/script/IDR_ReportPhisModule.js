$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.IDR_ReportPhisModule = function(cfg){
//	cfg.layOutRegion="north";
//	cfg.itemHeight = 200;
	phis.application.cic.script.IDR_ReportPhisModule.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(phis.application.cic.script.IDR_ReportPhisModule,phis.script.SimpleModule,{
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
					items : [{
								layout : "fit",
								border : false,
								split : true,
								region : 'center',
								items : this.getCrbForm()
							}]
				});
		this.panel = panel;
		return panel;
	},
	getCrbForm : function(){
		var module = this.createModule("crbBgkForm",
						this.crbBgkForm);
		this.CrbForm = module;
		module.on("save", this.IDRFormSave, this);
		module.opener = this;
		this.form = module.initPanel();
		Ext.apply(this.CrbForm.exContext, this.exContext);
		return this.form;
	},
	loadData : function(){
		if(!this.form.exContext){
			this.form.exContext = {};
		}
		Ext.apply(this.form.exContext, this.exContext);
	},
	doNew : function(MS_BRZD_JLBH){
		this.form.initDataId = null;
		this.form.doNew();
		if(!this.form.exContext){
			this.form.exContext = {};
		}
		if(this.form.exContext.args){
			this.form.exContext.args = {};
		}
		this.form.exContext.args.MS_BRZD_JLBH = MS_BRZD_JLBH;
	},
	doCreate : function(){
		this.form.doCreate();
	},
	initFormData : function(data){
		this.form.initDataId = this.initDataId;
		this.form.initFormData(data);
	},
	validate : function(){
		this.form.validate();
	},
	onLoadData : function(entryName,data){
		this.form.onLoadData(entryName,data);
	},
	IDRFormSave : function(entryName,op,json,data){
		if(op == "create"){
			this.exContext.args.recordId = json.body.RecordID;
			this.recordId = json.body.RecordID;
		}
	}
});