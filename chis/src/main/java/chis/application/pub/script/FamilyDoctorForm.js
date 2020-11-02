$package("chis.application.pub.script");

$import("app.modules.form.TableFormView");

chis.application.pub.script.FamilyDoctorForm = function(cfg) {
	cfg.saveServiceId="chis.publicService";
	cfg.loadServiceId="chis.simpleLoad";
	cfg.saveMethod="saveDicRecord";
	cfg.actions = [{
				id : "save",
				name : "保存"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.pub.script.FamilyDoctorForm.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.pub.script.FamilyDoctorForm,
		app.modules.form.TableFormView, {
			saveToServer:function(saveData){
		if(!this.fireEvent("beforeSave",this.entryName,this.op,saveData)){
			return;
		}
		if (!this.initDataId) {
			this.op = "create";
		} else {
			this.op = "update";
		}
		this.saving = true
		this.form.el.mask("正在保存数据...","x-mask-loading")
		var saveRequest = this.getSaveRequest(saveData);
		var saveCfg = {
			serviceId:this.saveServiceId,
			serviceAction:this.saveMethod,
			method : "execute",
			op:this.op,
			schema:this.entryName,
			body:saveRequest,
			dcid:"chis.dictionary.familyteam"
		}
		this.fixSaveCfg(saveCfg);
		util.rmi.jsonRequest(saveCfg,
			function(code,msg,json){
				this.form.el.unmask()
				this.saving = false
				if(code > 300){
					this.processReturnMsg(code,msg,this.saveToServer,[saveRequest],json.body);
					this.fireEvent("exception", code, msg, saveData); // **进行异常处理
					return
				}
				Ext.apply(this.data,saveData);
				if(json.body){
					this.initFormData(json.body);
				}
				this.fireEvent("save",this.entryName,this.op,json,this.data);
				this.afterSaveData(this.entryName, this.op, json,this.data);
				this.op = "update"
			},
			this)//jsonRequest
	},
		loadData:function(){
			chis.application.pub.script.FamilyDoctorForm.superclass.loadData.call(this);
		}
		});