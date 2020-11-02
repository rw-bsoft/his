$package("chis.application.scm.sign.script")
$import("chis.script.BizCombinedModule2")
debugger;
chis.application.scm.sign.script.SignContractRecordModule = function (cfg) {
    cfg.itemCollapsible = false;
    cfg.layOutRegion = "north";
    cfg.width = 890;
    cfg.itemHeight = 115;
    cfg.modal = true;
    cfg.frame = true;
    chis.application.scm.sign.script.SignContractRecordModule.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.scm.sign.script.SignContractRecordModule, chis.script.BizCombinedModule2, {
    initPanel: function () {
        var panel = chis.application.scm.sign.script.SignContractRecordModule.superclass.initPanel.call(this);
        this.panel = panel;
        this.scpList = this.midiModules[this.actions[1].id];
        this.grid = this.scpList.grid;
        // this.grid.setAutoScroll(false);
        this.sciForm = this.midiModules[this.actions[0].id];
        this.scpList.on("saveList", this.onSCIListSave, this);
        this.scpList.on("stopList", this.onStopSCIListSave, this);
        this.sciForm.on("rqflQuery",this.scpList.doRqflQuery,this.scpList);
        this.scpList.sciForm = this.sciForm;
        this.scpList.opener = this;
        //2019-10-22 zhaojian 公卫系统签约校验人群分类是否包含健康档案图标分类
        this.sciForm.opener = this;
        return panel;

    },
    onReady: function () {
        // ** 设置滚动条
        // this.panel.items.each(function(item) {
        // 	debugger
        //     item.setAutoScroll(true);
        // }, this);
    },
    doNew: function (flag) {
    	debugger;
    	this.scpList.requestData.schema="chis.application.scm.schemas.SCM_SignContractCheckPackage";
    	this.scpList.requestData.cnd=[];
    	var strKeys = [];
    	var strValues = [];
    	if("undefined" != typeof icos){
        	var a = icos;
        	var b = this.pData.peopleFlag;
        	for(var i=0;i<a.length;i++){
        		if(["B_07","A09"].indexOf(a[i]) != -1){strKeys.push("03");strValues.push("60岁及以上老年人");}
        		else if(["C_01","A03"].indexOf(a[i]) != -1){strKeys.push("04");strValues.push("高血压");}
        		else if(["D_01","A07"].indexOf(a[i]) != -1){strKeys.push("05");strValues.push("糖尿病");}
        		else if(["RQ_01","A15"].indexOf(a[i]) != -1){strKeys.push("02");strValues.push("0-6岁儿童");}
        		else if(["RQ_02","A37"].indexOf(a[i]) != -1){strKeys.push("01");strValues.push("孕产妇");}
        		else if(["RQ_03","A35"].indexOf(a[i]) != -1){strKeys.push("06");strValues.push("精障");}
        		else if(["RQ_04"].indexOf(a[i]) != -1){strKeys.push("07");strValues.push("结核病");}
        		else if(["RQ_05","A38","A39","A40"].indexOf(a[i]) != -1){strKeys.push("08");strValues.push("残疾人");}
        		else if(["RQ_06"].indexOf(a[i]) != -1){strKeys.push("13");strValues.push("建档立卡");}
        		else if(["RQ_07"].indexOf(a[i]) != -1){strKeys.push("11");strValues.push("城乡低保");}
        		else if(["RQ_08"].indexOf(a[i]) != -1){strKeys.push("19");strValues.push("城乡特困");} //一般健康人群 
        		else if(["RQ_09"].indexOf(a[i]) != -1){strKeys.push("09");strValues.push("计生特殊");}
        		else if(["RQ_10"].indexOf(a[i]) != -1){strKeys.push("22");strValues.push("慢阻肺患者");}
        		else if(["RQ_11"].indexOf(a[i]) != -1){strKeys.push("12");strValues.push("优抚对象");}
        		else if(["RQ_12"].indexOf(a[i]) != -1){strKeys.push("18");strValues.push("计生特别扶助人员");}
        		else if(["RQ_14"].indexOf(a[i]) != -1){strKeys.push("21");strValues.push("创新创业人才");}
        		else if(["RQ_13","A32"].indexOf(a[i]) != -1){strKeys.push("17");strValues.push("离休干部");}
        		else if(["RQ_15"].indexOf(a[i]) != -1){strKeys.push("16");strValues.push("麻风病");}
        		else if(["RQ_16"].indexOf(a[i]) != -1){strKeys.push("20");strValues.push("肿瘤患者");}
        		else if(["RQ_17"].indexOf(a[i]) != -1){strKeys.push("15");strValues.push("白血病患者");}
        	}
        	if(a.length > 0){
        		strKeys = strKeys.join(',');
        		strValues = strValues.join(',');
                this.scpList.doRqflQuery(strKeys);
        	}
    	}
    	this.pData.peopleFlag=({key: strKeys, text: strValues});	
    	this.pData.signFlag=({key: "1", text: "签约"});
        this.pData.peopleFlag_text="";
		if(this.exContext.args.SCID!=""){
	    	if(this.pData.signFlag.key!=3){
	    	//this.scpList.intendedPopulationMap=[];
	    	this.scpList.selects=[];
	    	}
    	}//add by zhj
        if (this.sciForm && this.pData) {//表单初始化
            Ext.apply(this.sciForm.exContext, this.exContext);
            this.sciForm.doNew();
            if (flag) {
                var initFormData = {};
                
                initFormData.favoreeEmpiId = this.pData.favoreeEmpiId;
                initFormData.favoreeName = this.pData.favoreeName;
                initFormData.SecondPartyEmpiId = this.pData.SecondPartyEmpiId;
                initFormData.SecondPartyName = this.pData.SecondPartyName;
                initFormData.year = this.pData.year;
                this.sciForm.initFormData(initFormData);
                this.sciForm.setCreateUnit();
                this.sciForm.setSigPackage();
            }
        }
        if (this.scpList) {
        	this.scpList.requestData.schema="chis.application.scm.schemas.SCM_SignContractCheckPackage";
            this.scpList.grid.enable();
            var sf = this.exContext.args.signFlag;
            if (sf == "2") {
                sf = "3";
            }
            this.setFormFiledStatus(this.sciForm, sf);
            var toolBar = this.grid.getTopToolbar();
            this.toolBar = toolBar;
            // var saveBtn = toolBar.find("cmd", "save");
            // var stopBtn = toolBar.find("cmd", "stop");
            // Ext.apply(this.scpList.exContext,this.exContext);
            for (var id in this.scpList.selects) {
	            delete this.scpList.selects[id];
        	}
            document.getElementById("TOP_SHOW").innerHTML = "共选择0个服务包0个组套0项，合计总金额:0.00";
            this.scpList.loadData();
            // saveBtn[0].enable();
            // stopBtn[0].enable();
            // if(this.grid&&this.exContext.args.signFlag=="2"){
            // 	saveBtn[0].hide();
            // 	stopBtn[0].show();
            // }
            // if(this.grid&&this.exContext.args.signFlag=="1"){
            // 	saveBtn[0].show();
            // 	stopBtn[0].hide();
            // }
        }
    },
    setBtnStatus: function (s, iselse) {
        var toolBar = this.grid.getTopToolbar();
        this.toolBar = toolBar;
        if (toolBar) {
            var btn = toolBar.find("cmd", iselse);
            if (!btn || "" == btn)
                return
            if (!btn) {
                s = true;
            }
            if (s) {
                btn[0].show();
            } else {
                btn[0].hide();
            }
        }

    },
    //根据签约情况修改界面字段状态(1.不可选取，2可选取)
    setFormFiledStatus: function (form, arg, jyarg) {
        var form = form.form.getForm();
        
        var favoreeName = form.findField("favoreeName");
        var scDate = form.findField("scDate");
        var year = form.findField("year");
       var signFlag = form.findField("signFlag");
        var stopDate = form.findField("stopDate");
        var stopReason = form.findField("stopReason");
		 var peopleFlag = form.findField("peopleFlag");
        var createUser = form.findField("createUser");
        var createUser1 = form.findField("createUser").getValue();
        var createUnit = form.findField("createUnit");
        var userId=this.mainApp.uid;
       // var user=this.pData.createUser.key;
        
        
         
        if (arg == "3") {
            this.scpList.grid.enable();
            favoreeName.disable();
            scDate.enable();
            year.disable();
            signFlag.disable();
            stopDate.enable();
            stopReason.enable();
            createUser.enable();
           // createUnit.ensable();
        }
          if (!arg) {
            this.scpList.grid.enable();
            favoreeName.disable();
            scDate.enable();
            year.disable();
             if(!this.pData.signFlag){
             	signFlag.setValue({key: "1", text: "签约"});
            signFlag.disable();
            }else{
            	signFlag.setValue(this.pData.signFlag);
            signFlag.disable();
            }
             if(!this.pData.peopleFlag){
           
            signFlag.disable();
            }else{
            	peopleFlag.setValue(this.pData.peopleFlag);
            
            }
            signFlag.disable();
            stopDate.enable();
            stopReason.enable();
            createUser.enable();
            createUnit.enable();
        }
        if (arg == "2") {
         //   this.scpList.grid.disable();
            favoreeName.disable();
            scDate.enable();
            year.disable();
            signFlag.disable();
            stopDate.enable();
            stopReason.enable();
            createUser.enable();
            createUnit.enable();
        }
        if (arg == "1" && !jyarg) {
        	 this.scpList.requestData.schema="chis.application.scm.schemas.SCM_SignContractCheckPackage";
            this.scpList.grid.enable();
            favoreeName.enable();
            scDate.enable();
            year.enable();
          //  var signFlag=this.pData.signFlag
          //*****************************
           if(!this.pData.peopleFlag){
           
            signFlag.disable();
            }else{
            	peopleFlag.setValue(this.pData.peopleFlag);
            
            }           
		   if(!this.pData.signFlag){
            signFlag.disable();
            }else{
            	signFlag.setValue(this.pData.signFlag);
            signFlag.disable();
            }
            //**********************zhj
            stopDate.enable();
            stopReason.enable();
            createUser.enable();
            createUnit.enable();
        }
        if (arg == "1" && jyarg == "2") {
            this.scpList.grid.enable();
            favoreeName.disable();
            scDate.enable();
            year.disable();
            signFlag.disable();
            signFlag.setValue({key: "2", text: "解约"});
            stopDate.enable();
            stopReason.enable();
            createUser.enable();
           createUnit.enable();
        }
        if (arg == "1" && jyarg == "1") {
            this.scpList.grid.enable();
            favoreeName.enable();
            scDate.enable();
            year.enable();
            signFlag.disable();
             if(!this.pData.stopReason){
           
            stopDate.enable();
            }else{
            	stopDate.setValue("");
            
            }
            stopDate.enable();
            stopReason.enable();
            createUser.enable();
            createUnit.enable();
        }
         if(createUser1==userId){
        stopDate.enable();
        stopReason.enable();
        } //zhj
    },
    loadData: function () {
        if (this.sciForm) {//表单初始化
            Ext.apply(this.sciForm.exContext, this.exContext);
            if (this.pData) {
                this.sciForm.initFormData(this.pData);
                this.sciForm.validate();
            }
            this.setFormFiledStatus(this.sciForm, this.pData.signFlag.key, this.exContext.args.signFlag);
        }
        if (this.scpList) {
            Ext.apply(this.scpList.exContext, this.exContext);
            if(!this.flag){
            this.scpList.requestData.schema="chis.application.scm.schemas.SCM_SignContractCheckPackage_1";
            this.scpList.requestData.cnd = ['eq', ['$', 'a.LOGOFF'], ['i', 0]];
            this.scpList.requestData.cnd =   ['eq', ['$', 'd.SCID'], ['s', this.exContext.args.SCID]];
            }
            this.scpList.loadData();
            this.scpList.fireEvent("loadData", this);
            var signFlag = this.pData.signFlag.key;
            var toolBar = this.scpList.grid.getTopToolbar();
            var saveBtn = toolBar.find("cmd", "save");
            var stopBtn = toolBar.find("cmd", "stop");
            if (signFlag == "2") {
                if (saveBtn[0]) {
                    saveBtn[0].enable();
                }
                if (stopBtn[0]) {
                    stopBtn[0].enable();
                }
            } else {
                if (saveBtn[0]) {
                    saveBtn[0].enable();
                }
                if (stopBtn[0]) {
                    stopBtn[0].enable();
                }
            }
        }
    },
    onSCIListSave: function (listData) {
        var formData = this.sciForm.getFormData();
        if (!formData) {
            return;
        }
        formData.signServicePackages = this.sciForm.signServicePackages;
       // if(!formData.agreement){
         //   MyMessageTip.msg("提示", "请先确认协议书！", true);
           // return;
        //}
        if(this.opener.haveRecord == true){
            MyMessageTip.msg("提示", "已签约数据无法修改！", true);
            return;
        }
        if (formData.signFlag == "2") {
            MyMessageTip.msg("提示", "该居民已经解约！", true);
            return;
        }
        if (listData.length == 0) {
            MyMessageTip.msg("提示", "请选择要签约的包！", true);
            return;
        }
    	if(!this.scpList.validateSelectedServicePackages()){
    		return;
    	}
		Ext.Msg.confirm("消息提示","确定要保存吗？",function(btn){
			if(btn == "yes"){
		    	//2019-07-28 zhaojian 判定当前签约入口是chis还是phis，主要根据emrViewNan.dic中配置菜单的名称进行判定
		    	//if(this.opener.title && this.opener.title=="签约服务"){
		    	if(this.mainApp.jobId.indexOf("phis")==0){
		    		formData.loginSystem = "phis";
		    		formData.ksdm = this.mainApp.departmentId;
                    formData.jzxh = this.exContext.args.JZXH;
					formData.clinicId = this.exContext.ids.clinicId;
		    	}else{
		    		formData.loginSystem = "chis";
		    	}
		        this.scpList.mask("正在保存数据...");
		        var result = null;
		        util.rmi.miniJsonRequestAsync({
		            serviceId: "chis.signContractRecordService",
		            method: "execute",
		            serviceAction: "saveSignContractPlan",
		            listData: listData,
		            formData: formData
		        }, function (code, msg, json) {
		            this.scpList.unmask();
		            if (code > 300) {
		               	MyMessageTip.msg("提示", msg, true);
		                return;
		            } else {
		                //this.initFormData(json.body)
		                this.fireEvent("sciFormSave", json.body);
		               MyMessageTip.msg("提示", "签约成功！", true);
		               // frame: true,
		                this.opener.haveRecord = true;
		                //this.hisSave(listData, formData)
		            }
		        }, this);
			}
		},this);
    },
    onStopSCIListSave: function () {
        var formData = this.sciForm.getFormData();
        if (!formData) {
            return;
        }
        if (formData.signFlag == "2") {
            MyMessageTip.msg("提示", "该居民已经解约！", true);
            return;
        }
        if (!formData.SCID) {
            MyMessageTip.msg("提示", "请选择解约记录！", true);
            return;
        }
        if (!formData.stopReason) {
            MyMessageTip.msg("提示", "请填写解约原因！", true);
            return;
        }
        if (!formData.stopDate) {
            MyMessageTip.msg("提示", "请填写解约时间！", true);
            return;
        }
		Ext.Msg.confirm("消息提示","确认要解约吗？",function(btn){
			if(btn == "yes"){
		        this.scpList.mask("正在保存数据...");
		        util.rmi.jsonRequest({
		            serviceId: "chis.signContractRecordService",
		            method: "execute",
		            serviceAction: "stopSignStatus",
		            formData: formData
		        }, function (code, msg, json) {
		            this.scpList.unmask();
		            if (code > 300) {
		                MyMessageTip.msg("提示", msg, true);
		                return;
		            } else {
		                //this.initFormData(json.body)
		                this.fireEvent("sciFormSave", formData);
		                MyMessageTip.msg("提示", "解约成功！", true);
		                //this.leftList.loadData();
		            }
		        }, this);
			}
		},this);
    },
    setSaveButtonDisable: function (status) {
//		var btns = this.sciForm.form.getTopToolbar().items;
//		if (btns && btns.item(0)) {
//			if (status) {
//				btns.item(0).disable();
//			} else {        
//				btns.item(0).enable();
//			}
//		}
    },

    hisSave: function (listData, formData) {
        this.scpList.mask("正在保存数据...");
        util.rmi.jsonRequest({
            serviceId: "chis.signContractRecordService",
            method: "execute",
            serviceAction: "saveSignContractToHis",
            listData: listData,
            formData: formData
        }, function (code, msg, json) {
            this.scpList.unmask();
            if (code > 300) {
                this.fireEvent("hisSaveError", json.body);
                return;
            } else {
                //this.initFormData(json.body)
                this.fireEvent("hisSaveSuccess", json.body);
                MyMessageTip.msg("提示", "签约成功！", true);
            }
        }, this);
    },
    copyOldValues : function () {
        return this.scpList.copyOldValues();
    }
});