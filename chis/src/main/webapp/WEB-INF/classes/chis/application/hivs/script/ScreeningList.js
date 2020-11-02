$package("chis.application.hivs.script")
$import("chis.script.BizSimpleListView", "chis.script.util.helper.Helper")

chis.application.hivs.script.ScreeningList = function (cfg) {
    chis.application.hivs.script.ScreeningList.superclass.constructor
        .apply(this, [cfg]);
    this.sex = {"1": "男", "2": "女", "9": "未说明的性别", "0": "未知的性别"}
}

Ext.extend(chis.application.hivs.script.ScreeningList,
    chis.script.BizSimpleListView, {

	loadData: function(){
		if (this.mainApp.jobtitleId == "chis.01") {
			this.requestData.schema = "chis.application.hivs.schemas.HIVS_Screening_01";
		}
		chis.application.hivs.script.ScreeningList.superclass.loadData.call(this);	
	},
	
	doCheck:function(){
		debugger;
		var r = this.getSelectedRecord();
		var res = util.rmi.jsonRequest({
			serviceId : "chis.hIVSScreeningService",
			serviceAction : "updateCheckflag",
			method:"execute",
			op:this.op,
			body:{
				"id":r.id,
				"checkFlag":"1",
			}
		},function(code,msg,json){				
				Ext.MessageBox.hide()
				if(code < 300){
					MyMessageTip.msg("提示", "审核成功!", true);
					this.refresh();
					
				}else{
					alert(msg)
				}
		},this);
	},
	
    doCreateRecord: function () {
        var m = this.midiModules["SC_MPI_Module"];
        if (!m) {
            $import("chis.application.mpi.script.EMPIInfoModule");
            m = new chis.application.mpi.script.EMPIInfoModule({
                entryName: "chis.application.mpi.schemas.MPI_DemographicInfo",
                title: "个人基本信息查询",
                height: 450,
                modal: true,
                mainApp: this.mainApp
            });
            m.on("onEmpiReturn", this.checkRecordExist, this);
            m.on("close", this.active, this);
            this.midiModules["SC_MPI_Module"] = m;
        }
        var win = m.getWin();
        win.setPosition(250, 100);
        win.show();
    },
    
	onSave : function(entryName, op, json, data) {
		this.refresh();
	},

    checkRecordExist: function (data) {
        var pData = {};
        pData.personName = data.personName;
        pData.sexCode = {"key": data.sexCode, "text": this.sex[data.sexCode]};
        pData.birthday = data.birthday;
        pData.idCard = data.idCard;
        pData.workPlace = data.workPlace;
        var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
        var birth = Date.parseDate(data.birthday, "Y-m-d");
        var monthAge = chis.script.util.helper.Helper.getAgeMonths(birth, currDate);
        pData.age = ~~(monthAge / 12);
        pData.mobileNumber = data.mobileNumber;
        pData.phoneNumber = data.phoneNumber;
        pData.address = data.address;
        pData.favoreeEmpiId = data.empiId;
        pData.favoreeName = data.personName;
        pData.favoreePhone = data.phoneNumber;
        pData.favoreeMobile = data.mobileNumber;
        pData.firstPartyName = this.mainApp.uname;
        pData.SecondPartyEmpiId = data.empiId;
        pData.SecondPartyName = data.personName;
        pData.scDate = currDate;
        pData.beginDate = currDate;
        var endDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
        endDate.setYear(endDate.getFullYear() + 1);
        pData.endDate = endDate;
        this.showSCWin(pData, "1");
    },
    showSCWin: function (pData, signFlag) {
        var hasRecord = this.beforeShowSCWin(pData) || 10 ;
        if (hasRecord == 0) {
            return MyMessageTip.msg("提示" , "请先建立个人档案并在当前机构挂号" ,true);
        } else if (hasRecord == 1) {
            return MyMessageTip.msg("提示" , "请先在当前机构挂号" ,true);
        } else if (hasRecord == 2) {
            return MyMessageTip.msg("提示" , "请先建立个人档案" ,true);
        } else if (hasRecord == 10) {
			this.onSelectEMPI(pData);
        } else {
            return MyMessageTip.msg("提示" , "查询当前病人个人档案和挂号记录异常" , true);
        }
    },
    beforeShowSCWin: function (pData) {
        //todo 查询是否有个档和挂号记录 0：皆无 1：只有个档 2：只有挂号记录 9：未知异常 10: 皆有
        var hasEHRAGHRecord = util.rmi.miniJsonRequestSync({
            serviceId : this.serviceId,
            serviceAction : "checkEhrAGh",
            method:"execute",
            body : {
                "empiId":pData.favoreeEmpiId,
                "jgid" : this.mainApp.deptId
            }
        });
    },
    onDblClick: function () {
        this.doModify();
    },
    doModify: function() {   	
    	debugger;
        var r = this.getSelectedRecord();
        if (!r) {
            return;
        }
        var data = r.json;
        this.checkRecordExist(data);
    },
	onSelectEMPI : function(pData) {	
		debugger;
		var empiId = pData.favoreeEmpiId;
		var birthDay = pData.birthday;
		var personName = pData.SecondPartyName;
		var jobId = this.mainApp.jobId;
		$import("chis.script.EHRView");
		var initModules = ['HIVS_01'];
		if(typeof this.midiModules["SC_MPI_Module"] != "undefined"){
			this.midiModules["SC_MPI_Module"].getWin().hide();
		}		
		module = new chis.script.EHRView({
					initModules : initModules,
					empiId : empiId,
					closeNav : true,
					mainApp : this.mainApp,
					width:document.body.clientWidth,
					height:document.body.clientHeight,
					exContext : {
						"birthDay" : birthDay
					}
				});	
        module.exContext.args.pData = pData;
        module.exContext.ids.empiId = empiId;
        module.on("save", this.refresh, this);
        module.exContext.record = new Map();
        if(typeof this.getSelectedRecord() != "undefined" && empiId == this.getSelectedRecord().data.empiId){
            module.exContext.record = this.getSelectedRecord();
    		this.midiModules["HIVS_EHRView"] = module;		
    		module.getWin().show();
    		module.activeTab = Object.keys(module.activeModules).length;
    		module.refresh();
        }else{
    		util.rmi.jsonRequest({
    			serviceId : "chis.hIVSScreeningService",
    			serviceAction : "queryHIVSScreening",
    			method:"execute",
    			body:{
    				"empiId":pData.favoreeEmpiId,
    			}
    		},function(code,msg,json){	
    			debugger;
    			if(json.data != null){
    				module.exContext.record = json;
    			}
				if(code >= 300){
					alert(msg)
				}else{
    				this.midiModules["HIVS_EHRView"] = module;
    				module.getWin().show();
    				module.activeTab = Object.keys(module.activeModules).length;
    				module.refresh();
				}
    		},this);	
        }
	},
    createModule: function (moduleName, moduleId, exCfg) {
        var item = this.midiModules[moduleName]
        if (!item) {
            var moduleCfg = this.loadModuleCfg(moduleId);
            var cfg = {
                showButtonOnTop: true,
                border: false,
                frame: false,
                autoLoadSchema: false,
                isCombined: true,
                exContext: {}
            };
            Ext.apply(cfg, exCfg);
            Ext.apply(cfg, moduleCfg);
            var cls = moduleCfg.script;
            if (!cls) {
                return;
            }
            if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
                return;
            }
            $import(cls);
            item = eval("new " + cls + "(cfg)");
            item.setMainApp(this.mainApp);
            this.midiModules[moduleName] = item;
        }
        return item;
    }	
});