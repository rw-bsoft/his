$package("chis.application.idr.script")

$import("chis.script.BizCombinedModule2")

chis.application.idr.script.IDR_ReportPhisRightModule = function(cfg){
	cfg.layOutRegion="north";
	cfg.itemHeight = 200;
	chis.application.idr.script.IDR_ReportPhisRightModule.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.idr.script.IDR_ReportPhisRightModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		var panel = chis.application.idr.script.IDR_ReportPhisRightModule.superclass.initPanel.call(this);
		this.list=this.midiModules[this.actions[0].id];
		this.list.on("loadData", this.onLoadGridData, this);
		this.grid = this.list.grid;
		this.grid.on("rowclick", this.onRowClick, this);
		this.form = this.midiModules[this.actions[1].id];
		this.form.on("IDRFormCreate",this.onIDRFormCreate,this);
		this.form.on("save",this.IDRFormSave,this);
		this.panel = panel;
		return panel
	},
	onLoadGridData : function(store){
		
	},
	onRowClick : function(grid, index, e){
		debugger;
		var r = grid.store.getAt(index);
		var MS_BRZD_JLBH = r.get("JLBH");
		var ICD10 = r.get("ICD10");
		this.fireEvent("ZDListRowClick",MS_BRZD_JLBH,ICD10);
	},
	loadData : function(){
		Ext.apply(this.list.exContext, this.exContext);
		if(!this.form.exContext){
			this.form.exContext = {};
		}
		Ext.apply(this.form.exContext, this.exContext);
		this.list.loadData();
	},
	doNew : function(MS_BRZD_JLBH,ICD10){
		this.form.initDataId = null;
		this.form.doNew();
		if(!this.form.exContext){
			this.form.exContext = {};
		}
		if(this.form.exContext.args){
			this.form.exContext.args = {};
		}
		this.form.exContext.args.MS_BRZD_JLBH = MS_BRZD_JLBH;
		this.form.exContext.args.ICD10 = ICD10;
		var form = this.form.form.getForm();
		var MS_BRZD_JLBH = form.findField("MS_BRZD_JLBH");
		if (MS_BRZD_JLBH) {
			MS_BRZD_JLBH.setValue(this.exContext.args.MS_BRZD_JLBH || '');
		}
		var icd10 = form.findField("icd10");
		if (icd10) {
			icd10.setValue(this.exContext.args.ICD10 || '');
		}
		var personName = form.findField("personName");
		if (personName) {
			personName.setValue(this.exContext.empiData.personName || '');
		}
		var sexCode = form.findField("sexCode");
		if (sexCode) {
			var xbStr = this.exContext.empiData.sexCode || '';
			if(xbStr){//性别
				var sexCodeStr = xbStr.replace(/0/, "");
				var dicName = {
            		id : "phis.dictionary.gender"
          		};
				var gender=util.dictionary.DictionaryLoader.load(dicName);
				var di = gender.wraper[sexCodeStr];
				var sexNameStr=""
				if (di) {
					sexNameStr = di.text;
				}
				sexCode.setValue({
					key : sexCodeStr,
					text : sexNameStr
				});
			}
		}
		var birthday = form.findField("birthday");
		if (birthday) {
			birthday.setValue(this.exContext.empiData.birthday || '');
		}
		if (this.exContext.empiData.birthday) { // 设置实足年龄默认值
			var birthdayStr=this.exContext.empiData.birthday;
			if(typeof birthdayStr=="string"){
				birthdayStr=new Date(Date.parse(birthdayStr));
			}
			var diffTime = chis.script.util.helper.Helper
					.getAgeBetween(birthdayStr,
							new Date());
			var fullAge = form.findField("fullAge");
			fullAge.setValue(diffTime);
		}
		var idCard = form.findField("idCard");
		if (idCard) {
			idCard.setValue(this.exContext.empiData.idCard || '');
		}
		var workPlace = form.findField("workPlace");
		if (workPlace) {
			workPlace.setValue(this.exContext.empiData.workPlace || '');
		}
		var mobileNumber = form.findField("mobileNumber");
		if (mobileNumber) {
			mobileNumber.setValue(this.exContext.empiData.mobileNumber || '');
		}
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
	cancelZDListSelect : function(){
		this.list.grid.getSelectionModel().clearSelections();
	},
	selectZDRecord : function(ZDJLBH){
		if (ZDJLBH) {
			for (var i = 0,len=this.list.store.getCount(); i < len; i++) {
				var r = this.list.store.getAt(i);
				if (r.get("JLBH") == ZDJLBH) {
					this.grid.getSelectionModel().selectRecords([r]);
					var n = this.list.store.indexOf(r);
					if (n > -1) {
						this.list.selectedIndex = n;
					}
					break;
				}
			}
		}
	},
	onIDRFormCreate : function(){
		this.fireEvent("IDRFormNew");
	},
	IDRFormSave : function(entryName,op,json,data){
		this.fireEvent("IDRFormSave",entryName,op,json,data);
	}
});