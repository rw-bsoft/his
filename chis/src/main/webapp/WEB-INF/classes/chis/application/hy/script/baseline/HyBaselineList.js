$package("chis.application.hy.script.baseline")
$import("chis.script.BizSimpleListView", "chis.script.util.helper.Helper")

chis.application.hy.script.baseline.HyBaselineList = function (cfg) {
	this.needOwnerBar = true;
	chis.application.hy.script.baseline.HyBaselineList.superclass.constructor
        .apply(this, [cfg]);
    this.sex = {"1": "男", "2": "女", "9": "未说明的性别", "0": "未知的性别"}
}

Ext.extend(chis.application.hy.script.baseline.HyBaselineList,
    chis.script.BizSimpleListView, {
	createOwnerBar : function() {
		var manageLabel = new Ext.form.Label({
					html : "管辖机构:",
					width : 80

				});
		var manageField = this.createDicField({
					'id' : 'chis.@manageUnit',
					'showWholeText' : 'true',
					'includeParentMinLen' : '6',
					'render' : 'Tree',
					defaultValue : {
						"key" : this.mainApp.deptId,
						"text" : this.mainApp.dept
					},
					'parentKey' : this.mainApp.deptId,
					rootVisible : true,
					width : 120
				});
		manageField.on("blur", this.manageBlur, this);
		this.manageField = manageField;
		var dateLabel1 = new Ext.form.Label({
					html : "&调查日期:",
					width : 80
				});
		var startValue = new Date().getFullYear() + "-01-01";
		var dateField1 = new Ext.form.DateField({
					width : this.cndFieldWidth || 120,
					enableKeyEvents : true,
					emptyText : "开始日期",
					value : Date.parseDate(startValue, "Y-m-d"),
					name : "planDate1"
				});
		this.dateField1 = dateField1;
		var dateLabel2 = new Ext.form.Label({
					html : "&nbsp;->&nbsp;",
					width : 30
				});
		var dateField2 = new Ext.form.DateField({
					width : this.cndFieldWidth || 120,
					enableKeyEvents : true,
					emptyText : "结束日期",
					value : new Date(),
					name : "planDate2"
				});
		this.dateField2 = dateField2;
		this.dateField1.on("select", this.selectDateField1, this);
		this.dateField2.on("select", this.selectDateField2, this);
		var cnd = this.getOwnerCnd([]);
		if (this.requestData.cnd) {
			cnd = ['and', this.requestData.cnd, cnd]
		}
		this.requestData.cnd = cnd;
		return [manageLabel, manageField, dateLabel1, dateField1,
				dateLabel2, dateField2]
	},
	
	getOwnerCnd : function(cnd) {
		if (this.manageField.getValue() != null
				&& this.manageField.getValue() != "") {
			var manageUnit = this.manageField.getValue();
			if (typeof manageUnit != "string") {
				manageUnit = manageUnit.key;
			}
			var cnd1 = ['like', ['$', 'a.createUnit'],
					['s', manageUnit + "%"]];
			if (cnd.length == 0) {
				cnd = cnd1;
			} else {
				cnd = ['and', cnd1, cnd];
			}
		}
		if (this.dateField1.getValue() != null
				&& this.dateField2.getValue() != null
				&& this.dateField1.getValue() != ""
				&& this.dateField2.getValue() != "") {
			var date1 = this.dateField1.getValue();
			var date2 = this.dateField2.getValue();
			var cnd2 = [
					'and',
					[
							'ge',
							['$', 'a.createDate'],
							[
									'todate',
									[
											's',
											date1.format("Y-m-d")
													+ " 00:00:00"],
									['s', 'yyyy-mm-dd HH24:mi:ss']]],
					[
							'le',
							['$', 'a.createDate'],
							[
									'todate',
									[
											's',
											date2.format("Y-m-d")
													+ " 23:59:59"],
									['s', 'yyyy-mm-dd HH24:mi:ss']]]];
			if (cnd.length == 0) {
				cnd = cnd2;
			} else {
				cnd = ['and', cnd2, cnd];
			}
		} else if ((this.dateField1.getValue() == null || this.dateField1
				.getValue() == "")
				&& (this.dateField2.getValue() == null || this.dateField2
						.getValue() == "")) {

		} else if (this.dateField1.getValue() == null
				|| this.dateField1.getValue() == "") {
			MyMessageTip.msg("提示", "请选择开始日期！", true);
			return;
		} else if (this.dateField2.getValue() == null
				|| this.dateField2.getValue() == "") {
			MyMessageTip.msg("提示", "请选择结束日期！", true);
			return;
		}
		this.queryCnd = cnd;
		return cnd;
	},
	
    doCreateRecord: function () {
    	this.op = "create";
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
    	this.op = "update"; 
        var r = this.getSelectedRecord();
        if (!r) {
            return;
        }
		util.rmi.jsonRequest({
			serviceId : "chis.hypertensionBaselineService",
			serviceAction : "getHyBaselineByPkey",
			method:"execute",
			body:{
				"recordId":r.id,
			}
		},function(code,msg,json){	
			debugger;
			if(code >= 300){
				alert(msg)
			}else{
				var data = this.extend(json.body,r.data);
		        this.checkRecordExist(data);
			}
		},this);	
    },
	onSelectEMPI : function(pData) {	
		var empiId = pData.favoreeEmpiId;
		var birthDay = pData.birthday;
		var personName = pData.SecondPartyName;
		var jobId = this.mainApp.jobId;
		$import("chis.script.EHRView");
		var initModules = ['C_10'];
		module = new chis.script.EHRView({
					initModules : initModules,
					empiId : empiId,
					closeNav : true,
					mainApp : this.mainApp,
					width:document.body.clientWidth,
					height:document.body.clientHeight,
					needInitFirstPanel:true,
					exContext : {
						"birthDay" : birthDay
					}
				});	
        module.exContext.args.pData = pData;
        module.exContext.ids.empiId = empiId;
        if(this.op == "update"){
        	 module.exContext.record = this.getSelectedRecord();
        }      
		this.midiModules["BL_EHRView"] = module;
		module.on("save", this.refresh, this);
		module.getWin().show();
		if(this.midiModules["SC_MPI_Module"] != "undefined"){
			this.midiModules["SC_MPI_Module"].getWin().hide();
		}	
		module.activeTab = Object.keys(module.activeModules).length;
		module.refresh();
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
    },
    
	extend: function(target, source) {
       for (var obj in source) {
           target[obj] = source[obj];
       }
       return target;
	},
	
	doQuchong: function() {
		debugger;
		var cnd = this.getOwnerCnd([]);
		this.requestData.cnd = cnd;
		this.listAction = "listHyBaselinePlanQC";
		this.listServiceId = "chis.hypertensionBaselineService";
		this.requestData.serviceAction = this.listAction;
		this.requestData.serviceId = this.listServiceId;
		this.refresh()
	}
});