$package("chis.application.scm.sign.script")
$import("chis.script.BizSimpleListView", "chis.script.util.helper.Helper")

chis.application.scm.sign.script.SignContractRecordListView = function (cfg) {
    chis.application.scm.sign.script.SignContractRecordListView.superclass.constructor
        .apply(this, [cfg]);
    this.sex = {"1": "男", "2": "女", "9": "未说明的性别", "0": "未知的性别"}
}

Ext.extend(chis.application.scm.sign.script.SignContractRecordListView,
    chis.script.BizSimpleListView, {
        doCreateSC: function () {
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
        doStopSC: function () {
            var signFlag = "2";
            this.doModify(signFlag);
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
            // var empiId = pData.favoreeEmpiId;
            // var module = this.midiModules["SCM_PSC_Module"]
            // if (!module) {
            // 	$import("chis.script.EHRView");
            // 	var initModules = ['SCM_01'];
            // 	module = new chis.script.EHRView({
            // 				initModules : initModules,
            // 				empiId : empiId,
            // 				closeNav : true,
            // 				mainApp : this.mainApp
            // 			});
            // 	this.midiModules["SCM_PSC_Module"] = module;
            // 	module.on("save", this.refresh, this);
            // }else{
            // 	module.exContext.ids["empiId"] = empiId;
            // 	module.exContext.args.signFlag = signFlag;
            // 	module.refresh();
            // }
            // module.exContext.args={};
            // module.exContext.args.pData = pData;
            // module.exContext.args.signStatus = pData.signFlag;
            // module.exContext.args.SCID = pData.SCID;
            // module.exContext.args.signFlag = signFlag;
            // module.getWin().show();
            if (hasRecord == 0) {
                return MyMessageTip.msg("提示" , "请先建立个人档案并在当前机构挂号" ,true);
            } else if (hasRecord == 1) {
                return MyMessageTip.msg("提示" , "请先在当前机构挂号" ,true);
            } else if (hasRecord == 2) {
                return MyMessageTip.msg("提示" , "请先建立个人档案" ,true);
            } else if (hasRecord == 10) {
/*                var empiId = pData.favoreeEmpiId;
                var m = this.midiModules["SCM_PSC_Module"]
                if (!m) {
                    var m = this.createModule("SCM_PSC_Module", this.refModule);
                    this.midiModules["SCM_PSC_Module"] = m;
                }
                m.exContext.args = {};
                m.exContext.ids = {};
                m.exContext.args.pData = pData;
                m.exContext.args.signStatus = pData.signFlag;
                m.exContext.args.SCID = pData.SCID;
                m.exContext.args.signFlag = signFlag;
                m.exContext.ids.empiId = empiId;
                m.on("save", this.refresh, this);
                win = m.getWin();
                win.add(m.initPanel());
                m.loadData();
               	win.setHeight(document.body.clientHeight);
               	win.setWidth(document.body.clientWidth);
                win.show();
                var r ={};
                r.data=pData;*/
				this.onSelectEMPI(pData, signFlag);
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
        onDblClick: function (grid, index, e) {
            this.doModify("1");
        },
        doModify: function (signFlag) {
            if (signFlag != "2") {
                signFlag = "1";
            }
            var r = this.getSelectedRecord();
            if (!r) {
                return;
            }
            var pData = this.castListDataToForm(r.data, this.schema);
            var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
            var birth = Date.parseDate(pData.birthday, "Y-m-d");
            var monthAge = chis.script.util.helper.Helper.getAgeMonths(birth, currDate);
            pData.age = ~~(monthAge / 12);
/*            this.showSCWin(pData, signFlag);*/
			this.onSelectEMPI(pData, signFlag);
        },
		onSelectEMPI : function(pData, signFlag) {
			var empiId = pData.favoreeEmpiId;
			var birthDay = pData.birthday;
			var personName = pData.SecondPartyName;
			var jobId = this.mainApp.jobId;
			$import("chis.script.EHRView");
			var initModules = ['B_011','B_04', 'B_05'];
			if (jobId == "chis.04") {
				var initModules = ['B_01', 'B_02', 'B_04', 'B_05'];
			} else if (jobId == "chis.13") {
				var initModules = ['B_01'];
			}
			module = new chis.script.EHRView({
						initModules : initModules,
						empiId : empiId,
						closeNav : true,
						mainApp : this.mainApp,
						exContext : {
							"birthDay" : birthDay
						}
					});	
            //module.exContext.args = {};
            //module.exContext.ids = {};
            module.exContext.args.pData = pData;
            module.exContext.args.signStatus = pData.signFlag;
            module.exContext.args.SCID = pData.SCID;
            module.exContext.args.signFlag = signFlag;
            module.exContext.ids.empiId = empiId;
			this.midiModules["HealthRecord_EHRView"] = module;
			//module.on("save", this.refresh, this);
			module.getWin().show();	
			module.mainTab.add(module.getModuleCfg("B_90"));
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
    	showColor : function(value, metaData, r, row, col) {
    		debugger;
    		if(value==undefined){
    			return "";
    		}		
    		var nowdate = new Date();
            nowdate.setMonth(nowdate.getMonth()+1);
            var y = nowdate.getFullYear();
            var m = nowdate.getMonth()+1;
            var d = nowdate.getDate();
            var nextMonthDate = y+'-'+((m+"").length==1?("0"+m):m)+'-'+((d+"").length==1?("0"+d):d);
            nowdate.setMonth(nowdate.getMonth()-1);
            y = nowdate.getFullYear();
            m = nowdate.getMonth()+1;
            d = nowdate.getDate();
            var currentDate = y+'-'+((m+"").length==1?("0"+m):m)+'-'+((d+"").length==1?("0"+d):d);
    		//客户端电脑时间与签约结束时间对比判定
    		if(value=="签约" && r.data.endDate.length>0 && nextMonthDate>=r.data.endDate && currentDate<=r.data.endDate){
    			return "<font style='color:red'>即将到期</font>";
    		}else if(value=="签约" && r.data.endDate.length>0 && currentDate>r.data.endDate){
    			return "<font style='color:red'>已到期</font>";
    		}
    		else if(value=="解约"){
    			return "<font style='color:red'>"+value+"</font>";
    		}
    		return "<font style='color:#2AAA00'>"+value+"</font>";
    	},
    	checkHealthRecordExist : function(value, metaData, r, row, col) {
    		debugger;
    		if(value == null || value == ""){
    			value = "否"
    		}else{
    			value = "是"
    		}
    		return value
    	}
    });