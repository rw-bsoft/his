$package("chis.application.pub.script");

$import("app.modules.form.TableFormView");

chis.application.pub.script.FamilyTeamForm = function(cfg) {
	cfg.saveServiceId="chis.publicService";
	cfg.loadServiceId="chis.simpleLoad";
	cfg.saveMethod="saveDicRecord";
	cfg.actions = [{
				id : "newadd",
				name : "新建",
				iconCls:"new"
			}, {
				id : "save",
				name : "保存"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.pub.script.FamilyTeamForm.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.pub.script.FamilyTeamForm,
		app.modules.form.TableFormView, {
		doNewadd:function(saveData){
			var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getFamilyteamId",
							method : "execute"
						});
			if(result.json && result.json.familyteamId){
				this.form.getForm().findField("familyTeamId").setValue(result.json.familyteamId);
			}else{
				alert("未找到本机构的编码生成规则，请联系管理员！");
			}
			this.form.getForm().findField("familyTeamName").setValue("");
			this.form.getForm().findField("status").setValue("");
			this.op="create";
		},
		saveToServer:function(saveData){
		if(!this.fireEvent("beforeSave",this.entryName,this.op,saveData)){
			return;
		}
		if (!this.initDataId) {
			this.op = "create";
		} else {
			this.op = "update";
		}
		
		var saveRequest = this.getSaveRequest(saveData);
		saveRequest.domain="chis";
		var familyTeamId=saveRequest.familyTeamId;
		if(familyTeamId.length !=13 ){
			alert("请按照规则维护团队编码,家庭医生团队编号以2位数，从01开始顺序编写！");
			return;
		}
		var dicName = {id : "chis.dictionary.familyteamcoding"};
		var dic=util.dictionary.DictionaryLoader.load(dicName);
		var di = dic.wraper[this.mainApp.deptId];
		var text=""
		if (di) {
			text = di.text;
		}
		if(familyTeamId.substring(0,familyTeamId.length-2)!=text){
			alert("请按照规则维护团队编码,家庭医生团队编号以2位数，从01开始顺序编写！");
			return;
		}
		//校验团队名称
		var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "checkfamilyTeamName",
							method : "execute",
							familyTeamName:saveRequest.familyTeamName,
							familyTeamId:saveRequest.familyTeamId
						});
		if(result.code>300){
			alert(result.msg);
			return;
		}
		this.saving = true
		this.form.el.mask("正在保存数据...","x-mask-loading")
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
				this.win.hide();
				this.fireEvent("save",this.entryName,this.op,json,this.data);
				this.op = "update"
			},
			this)
	}
		});