$package("chis.application.per.script.dictionary");

$import("chis.script.BizTableFormView");

chis.application.per.script.dictionary.CheckupDictionaryForm = function(cfg){
	
	chis.application.per.script.dictionary.CheckupDictionaryForm.superclass.constructor.apply(this,[cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("save",this.onSave,this);
};

Ext.extend(chis.application.per.script.dictionary.CheckupDictionaryForm,chis.script.BizTableFormView,{
	doNew : function(){
		if(this.op == "create"){
			this.initDataId = null;
		}
		this.validate();
		chis.application.per.script.dictionary.CheckupDictionaryForm.superclass.doNew.call(this);
	},
	onLoadData : function(entryName, body) {
		this.validate();
	},
	doSave : function(){
		this.saveServiceId="chis.checkupDictionaryService";
		this.saveAction="saveDictionaryItem";
		chis.application.per.script.dictionary.CheckupDictionaryForm.superclass.doSave.call(this);
	},
	getSaveRequest : function(saveData){
		var form = this.form.getForm();
		var checkupProject = this.form.getForm().findField("checkupProjectName");
		if (checkupProject) {
			var checkupProjectName = checkupProject.getValue();
			var projectType = form.findField("projectType").getValue();
			var cnd = [
							'and',
							['eq', ['$', 'projectType'], ['s', projectType]],
							['eq', ['$', 'checkupProjectName'],
									['s', checkupProjectName]]];
			var secCnd;
			if (this.initDataId) {
				secCnd = ['ne', ['$', 'checkupProjectId'],
								['s', this.initDataId]];
			}
			var cnds;
			if (secCnd){
				cnds = ['and', secCnd, cnd];
			}else{
				cnds = cnd;
			}
		}
		saveData.cnds = cnds;
		return saveData;
	},
	onSave : function(entryName, op, json, data){
		if(!this.initDataId){
			this.initDataId = json.body.checkupProjectId
		}
	}
});