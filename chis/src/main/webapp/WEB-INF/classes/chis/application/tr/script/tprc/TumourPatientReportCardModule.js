$package("chis.application.tr.script.tprc");

$import("chis.script.BizTabModule");

chis.application.tr.script.tprc.TumourPatientReportCardModule = function(cfg){
	cfg.autoLoadData = false;
	chis.application.tr.script.tprc.TumourPatientReportCardModule.superclass.constructor.apply(this,[cfg]);
	this.on("loadModule", this.onLoadModule, this);
	this.saveMethod="execute";
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.tr.script.tprc.TumourPatientReportCardModule,chis.script.BizTabModule,{
	onSave:function(){
		this.refreshEhrTopIcon();
	},
	loadData : function() {
		this.activeModule(0);
		if(this.tprcForm){
			Ext.apply(this.tprcForm.exContext,this.exContext)
		}
		if(this.tpbcForm){
			Ext.apply(this.tpbcForm.exContext,this.exContext)
		}
		if(this.tpfvForm){
			Ext.apply(this.tpfvForm.exContext,this.exContext)
		}
	},
	onLoadModule : function(moduleName,module){
		Ext.apply(module.exContext,this.exContext)
		if(moduleName == this.actions[0].id){ 
			this.tprcForm = module;
			this.tprcForm.on("TPRCFormLoad",this.onTPRCFormLoad,this);
			this.tprcForm.on("save", this.onTPRCSave, this);
		}
		if(moduleName == this.actions[1].id){
			this.tpbcForm = module;
			this.tpbcForm.on("save", this.onTPFVBSave, this);
		}
		if(moduleName == this.actions[2].id){
			this.tpfvForm = module;
			this.tpfvForm.on("save", this.onTPFVSave, this);
		}
	},
	onTPRCFormLoad : function(entryName,body){
		var op = body.op;
		if(op=="create"){
			if(this.tpbcForm){
				this.tpbcForm.doNew();
			}
			if(this.tpfvForm){
				this.tpfvForm.doNew();
			}
		}
	},
	onTPRCSave : function(tprcData){
		this.collectSaveData(tprcData,"tprcForm");
	},
	onTPFVBSave : function(tpfvbData){
		this.collectSaveData(tpfvbData,"tpbcForm");
	},
	onTPFVSave : function(tpfvData){
		this.collectSaveData(tpfvData,"tpfvForm");
	},
	collectSaveData : function(frmData,frmPanel){
		var TPRCID = this.exContext.ids["MDC_TumourPatientReportCard.TPRCID"];
		var saveData = {};
		if(frmPanel == "tprcForm"){
			saveData.tprcData = frmData;
			if(!this.tpbcForm || !this.tpfvForm){
				if(!this.tpbcForm){
					if(!TPRCID){
						this.tab.activate(1);
						Ext.Msg.alert("提示", "请填写基本信息核实的信息");
						return;
					}
				}else{
					if (!this.tpbcForm.validate()) {
						Ext.Msg.alert("提示", "基本信息核实填写不完整");
						this.tab.activate(1);
						return
					}
					saveData.tpfvbData = this.tpbcForm.getFormData();
				}
				if(!this.tpfvForm){
					if(!TPRCID){
						this.tab.activate(2);
						Ext.Msg.alert("提示", "请填写首次随访的信息");
						return;
					}
				}else{
					if (!this.tpfvForm.validate()) {
						Ext.Msg.alert("提示", "首次随访的信息填写不完整");
						this.tab.activate(2);
						return
					}
					saveData.tpfvData = this.tpfvForm.getFormData();
				}
			}else{
				if (this.tpbcForm && !this.tpbcForm.validate()) {
					Ext.Msg.alert("提示", "基本信息核实填写不完整");
					this.tab.activate(1);
					return
				}
				if (this.tpfvForm && !this.tpfvForm.validate()) {
					Ext.Msg.alert("提示", "首次随访的信息填写不完整");
					this.tab.activate(2);
					return
				}
				saveData.tpfvbData = this.tpbcForm.getFormData();
				saveData.tpfvData = this.tpfvForm.getFormData();
			}
		}
		if(frmPanel == "tpbcForm"){
			saveData.tpfvbData = frmData;
			if(!this.tpfvForm || !this.tprcForm){
				if(!this.tpfvForm){
					if(!TPRCID){
						this.tab.activate(2);
						Ext.Msg.alert("提示", "请填写首次随访的信息");
						return;
					}
				}else{
					if (!this.tpfvForm.validate()) {
						Ext.Msg.alert("提示", "首次随访的信息填写不完整");
						this.tab.activate(2);
						return
					}
					saveData.tpfvData = this.tpfvForm.getFormData();
				}
				if(!this.tprcForm){
					if(!TPRCID){
						this.tab.activate(0);
						Ext.Msg.alert("提示", "请填写患者报告卡的信息");
						return;
					}
				}else{
					if(!this.tprcForm.validate()){
						Ext.Msg.alert("提示", "肿瘤患者报告卡信息填写不完整");
						this.tab.activate(0);
						return
					}
					saveData.tprcData = this.tprcForm.getFormData();
					if(!saveData.tprcData.empiId){
						saveData.tprcData.empiId = this.exContext.ids.empiId;
					}
				}
			}else{
				if(this.tprcForm && !this.tprcForm.validate()){
					Ext.Msg.alert("提示", "肿瘤患者报告卡信息填写不完整");
					this.tab.activate(0);
					return
				}
				if (this.tpfvForm && !this.tpfvForm.validate()) {
					Ext.Msg.alert("提示", "首次随访的信息填写不完整");
					this.tab.activate(2);
					return
				}
				saveData.tprcData = this.tprcForm.getFormData();
				if(!saveData.tprcData.empiId){
					saveData.tprcData.empiId = this.exContext.ids.empiId;
				}
				saveData.tpfvData = this.tpfvForm.getFormData();
			}
		}
		if(frmPanel == "tpfvForm"){
			saveData.tpfvData = frmData;
			if(!this.tpbcForm || !this.tprcForm){
				if(!this.tpbcForm){
					if(!TPRCID){
						this.tab.activate(1);
						Ext.Msg.alert("提示", "请填写基本信息核实的信息");
						return;
					}
				}else{
					if (!this.tpbcForm.validate()) {
						Ext.Msg.alert("提示", "基本信息核实填写不完整");
						this.tab.activate(1);
						return
					}saveData.tpfvbData = this.tpbcForm.getFormData();
					
				}
				if(!this.tprcForm){
					if(!TPRCID){
						this.tab.activate(0);
						Ext.Msg.alert("提示", "请填写患者报告卡的信息");
						return;
					}
				}else{
					if(!this.tprcForm.validate()){
						Ext.Msg.alert("提示", "肿瘤患者报告卡信息填写不完整");
						this.tab.activate(0);
						return
					}
					saveData.tprcData = this.tprcForm.getFormData();
					if(!saveData.tprcData.empiId){
						saveData.tprcData.empiId = this.exContext.ids.empiId;
					}
				}
			}else{
				if(this.tprcForm && !this.tprcForm.validate()){
					Ext.Msg.alert("提示", "肿瘤患者报告卡信息填写不完整");
					this.tab.activate(0);
					return
				}
				if (this.tpbcForm && !this.tpbcForm.validate()) {
					Ext.Msg.alert("提示", "基本信息核实填写不完整");
					this.tab.activate(1);
					return
				}
				saveData.tprcData = this.tprcForm.getFormData();
				if(!saveData.tprcData.empiId){
					saveData.tprcData.empiId = this.exContext.ids.empiId;
				}
				saveData.tpfvbData = this.tpbcForm.getFormData();
			}
		}
		if(this.exContext.args.highRiskType == "n"){
			saveData.tprcData.highRiskType="";
		}else{
			saveData.tprcData.highRiskType = this.exContext.args.highRiskType || '';
		}
		saveData.turnReport = this.exContext.args.turnReport || false;
		this.saveToServer(saveData)
	},
	saveToServer : function(saveData){
		this.tab.el.mask("正在保存数据...","x-mask-loading")
		this.op = this.tprcForm.aop;
		var saveCfg = {
			serviceId:this.saveServiceId,
			method:this.saveMethod,
			serviceAction:this.saveAction || "",
			op:this.op,
			module:this._mId,  //增加module的id
			body:saveData
		}
		util.rmi.jsonRequest(saveCfg,
			function(code,msg,json){
				this.tab.el.unmask()
				this.saving = false
				if(code > 300){
					this.processReturnMsg(code,msg,this.saveToServer,[saveData],json.body);
					this.fireEvent("exception", code, msg, saveData); // **进行异常处理
					return
				}
				Ext.apply(this.data,saveData);
				if(json.body){
					var tprcResData = json.body.tprcResData;
					var tpfvbResData = json.body.tpfvbResData;
					var tpfvResData = json.body.tpfvResData;
					var controlData = json.body.controlData;
					if(controlData){
						this.exContext.control = controlData;
					}
					if(tprcResData){
						if(controlData){
							this.tprcForm.exContext.control = controlData;
						}
						this.tprcForm.initFormData(tprcResData);
					}
					if(tpfvbResData && this.tpbcForm){
						if(controlData){
							this.tpbcForm.exContext.control = controlData;
						}
						this.tpbcForm.initFormData(tpfvbResData);
					}
					if(tpfvResData && this.tpfvForm){
						if(controlData){
							this.tpfvForm.exContext.control = controlData;
						}
						this.tpfvForm.initFormData(tpfvResData);
					}
					var rebody={
						body:tprcResData
					};
					this.fireEvent("save",this.entryName,this.op,rebody,saveData.tprcData);
				}
				this.tprcForm.aop="update"
				this.op = "update"
			},
			this)//jsonRequest
	}
});