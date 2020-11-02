$package("chis.application.conf.script.admin")


$import('chis.script.app.modules.form.TableFormView')

chis.application.conf.script.admin.ManageUnitIntroduce = function(cfg){
	chis.application.conf.script.admin.ManageUnitIntroduce.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.conf.script.admin.ManageUnitIntroduce,chis.script.app.modules.form.TableFormView,{
	
	loadData : function() {
		this.initDataId = this.mainApp.deptId; 
		if (this.loading) {
			return
		}
		
		if (!this.schema) {
			return
		}
		if (!this.initDataId) {
			return;
		}

		if (!this.fireEvent("beforeLoadData", this.entryName, this.initDataId)) {
			return
		}
		if (this.form && this.form.el) {
			this.form.el.mask("正在载入数据...", "x-mask-loading")
		}
		this.loading = true
		util.rmi.jsonRequest({
					serviceId : this.loadServiceId,
					schema : this.entryName,
					pkey : this.initDataId
				}, function(code, msg, json) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					this.loading = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadData);
						this.op ='create';
						return
					}
					if (json.body) {
						this.doNew()
						this.initFormData(json.body)
						this.fireEvent("loadData", this.entryName, json.body);
						var introduction = json.body.introduction;
						if(introduction){
							var form = this.form.getForm();
							var zipCodeVir = form.findField("zipCodeVir");
							var addressVir = form.findField("addressVir");
							var phoneVir = form.findField("phoneVir");
							var emailVir = form.findField("emailVir");
							var webVir = form.findField("webVir");
							var introductionField = form.findField("introduction");
							zipCodeVir.setValue(this.getIntroductionValue(introduction,"zipCodeVir"));
							addressVir.setValue(this.getIntroductionValue(introduction,"addressVir"));
							phoneVir.setValue(this.getIntroductionValue(introduction,"phoneVir"));
							emailVir.setValue(this.getIntroductionValue(introduction,"emailVir"));
							webVir.setValue(this.getIntroductionValue(introduction,"webVir"));
							introductionField.setValue(this.getIntroductionValue(introduction,"introduction"));
						}
					}
					if (this.op == 'create') {
						this.op = "update"
					}
				}, this)// jsonRequest
		},
	//<introduction>机构简介</introduction><zipCodeVir>邮编</zipCodeVir><addressVir>地址</addressVir><phoneVir>联系电话</phoneVir><emailVir>E-mail</emailVir><webVir>网址</webVir>
	    getIntroductionValue : function(introduction,key){
	    	var key1= '<'+key+'>';
	    	var key2='</'+key+'>';
	    	if(introduction.indexOf(key1)!= -1){
	    		return introduction.substring(introduction.indexOf(key1)+key1.length,introduction.indexOf(key2)); 
	    	}
	    	return "";
	    },
		doSave : function() {
			var ac = util.Accredit;
			var form = this.form.getForm()
				if (!this.schema) {
					return
				}
			var values = {};
			var items = this.schema.items

			Ext.apply(this.data, this.exContext)
			var form = this.form.getForm();
			var zipCodeVir = form.findField("zipCodeVir");
			var addressVir = form.findField("addressVir");
			var phoneVir = form.findField("phoneVir");
			var emailVir = form.findField("emailVir");
			var webVir = form.findField("webVir");
			var introductionField = form.findField("introduction");
			values['introduction'] = '<introduction>'+introductionField.getValue()+'</introduction><zipCodeVir>'+zipCodeVir.getValue()+'</zipCodeVir><addressVir>'+addressVir.getValue()+'</addressVir><phoneVir>'+phoneVir.getValue()+'</phoneVir><emailVir>'+emailVir.getValue()+'</emailVir><webVir>'+webVir.getValue()+'</webVir>';
			values['manaUnitId'] =  this.mainApp.deptId;
			if(!(this.op=='update')){
				delete this.initDataId;
			}
			Ext.apply(this.data, values);
			this.saveToServer(values);
		}
})