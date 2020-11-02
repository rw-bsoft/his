$package("chis.application.scm.sign.script")
$import("chis.script.BizCombinedModule2","chis.script.util.helper.Helper")

chis.application.scm.sign.script.PersonalSignContractModule = function (cfg) {
    cfg.itemWidth = 260;
    cfg.autoLoadData=false;
    chis.application.scm.sign.script.PersonalSignContractModule.superclass.constructor.apply(this, [cfg]);
    this.haveRecord = false;
}

Ext.extend(chis.application.scm.sign.script.PersonalSignContractModule, chis.script.BizCombinedModule2, {
    initPanel: function () {
        var panel = chis.application.scm.sign.script.PersonalSignContractModule.superclass.initPanel.call(this);
        this.panel = panel;
        if(!this.actions){
            this.actions[0] = {id:"pscList" , ref:"chis.application.scm.SCM/SCM/SCM01_0101"}
            this.actions[1] = {id:"SCModule" , ref:"chis.application.scm.SCM/SCM/SCM01_0102"}
        }
        this.pscList = this.midiModules[this.actions[0].id];
        this.pscList.opener = this;
        this.pscList.on("newSC", this.onNewSC, this);
	    this.pscList.on("renewSC", this.onReNewSC, this);
        this.pscList.on("loadData", this.onPSCListLoadData, this);
        this.pscGrid = this.pscList.grid;
        this.pscGrid.on("rowclick", this.onPSCListRowClick, this);
        this.pscList.on("remove", this.refreshList, this);
        this.scModule = this.midiModules[this.actions[1].id];
        this.scModule.opener = this;
        this.scModule.on("sciFormSave", this.onSCModuleSave, this);
        this.pscList.father = this;
        return panel;
    },
   //刷新列表数据
	refreshList : function() {
		if (this.father) {
			this.father.refreshList();
		} 
		if (this.pscList) {
			this.pscList.refresh();
		}
	},
    onNewSC: function () {
    	var idcard ="";
    	var age = 0;
//    	if(this.title && this.title=="签约服务"){//phis端
    		idcard = this.exContext.empiData.idCard;
    		age = this.exContext.empiData.age;
/*    	}else{//chis端
    		idcard =  this.father.pscForm.idCard.value;
    		var birthday = Date.parseDate(this.father.pscForm.birthday.value,"Y-m-d");
    		var nowDate = new Date();
    		var diff = nowDate.getTime() - birthday.getTime();
    		// 向下取整  例如 10岁 20天 会计算成 10岁
    		// 如果要向上取整 计算成11岁，把floor替换成 ceil
    		age = Math.floor(diff / 1000 / 60 / 60 / 24 / 365);
    	}*/
    	if(age>6 && idcard==""){
            return MyMessageTip.msg("提示","身份证号不能为空，请实名签约！",true);
    	}
        if (this.scModule) {
            this.scModule.pData = this.pData;
            //刷新当前签约列表 的empid
            this.pscList.exContext.ids.empiId = this.pData.favoreeEmpiId;
            this.exContext.args.SCID = "";
            this.scModule.doNew(true);
            this.scModule.setSaveButtonDisable(false);
        }
    },
		 onReNewSC:function(){
     /** zhj **/
        if (this.scModule) {
        	//this.scModule.pData.signFlag_text="续约";
        	this.exContext.args.peopleFlag=this.pData.peopleFlag;
            this.scModule.pData.signFlag=({key: "3", text: "续约"});
            this.scModule.pData = this.pData;
            //刷新当前签约列表 的empid
//            this.scModule.pData.signFlag_text="续约";
//            this.scModule.pData.signFlag=({key: "3", text: "续约"});
//            this.pData.signFlag_text="续约";
            // this.exContext.args.pData.signFlag=({key: "3", text: "续约"});
         //    this.exContext.args.pData.signFlag=({key: "3", text: "续约"});
//           this.pscList.exContext.ids.signFlag=({key: "3", text: "续约"});
//           this.exContext.args.signStatus=({key: "3", text: "续约"});
//           this.exContext.args.signFlag_text="续约";
            this.pscList.exContext.ids.empiId = this.pData.favoreeEmpiId;
            this.exContext.args.SCID = "";
           
            var oldValues = this.scModule.copyOldValues();
            this.scModule.doNew(true);
            this.scModule.scpList.store.add(oldValues)
            this.scModule.scpList.grid.getSelectionModel().selectRecords(oldValues)
            // this.scModule.setSaveButtonDisable(false);
        }

    },				 
    onPSCListLoadData: function (store) {
        if (!store || store.getCount() == 0) {
            if (this.scModule) {
                this.exContext.args.SCID = "";
                this.scModule.doNew(true);
            }
            return;
        }
        //去除该段逻辑，改为无论何时可续约
        // var bool = true
        // var today = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
        // for (var i = 0; i < store.getCount(); i++) {
        //     var r1 = store.getAt(i)
        //     var stopDate = null;
        //     if (r1.get("stopDate")) {
        //         stopDate = Date.parseDate(r1.get("stopDate"), "Y-m-d")
        //     }
        //     var endDate = Date.parseDate(r1.get("endDate"), "Y-m-d")
        //     if (!stopDate && endDate.getTime() + 86400000 > today.getTime()) {
        //         bool = false
        //     }
        // }
        // if (this.pscList) {
        //     this.pscList.setBtnStatus(bool, "new");
        // }
        var SCID = this.exContext.args.SCID;
        if (SCID) {
            var selectIndex = store.find("SCID", SCID);
            this.pscList.selectedIndex = selectIndex;
            this.pscList.selectRow(selectIndex);
        }
        if (!this.pscList.selectedIndex) {
            this.pscList.selectedIndex = 0;
        }
        var r = store.getAt(this.pscList.selectedIndex);
        this.process(r);
    },
    onPSCListRowClick: function (grid, index, e) {
        var r = this.pscList.getSelectedRecord();
        var SPIID = r.id;
        var result = util.rmi.miniJsonRequestSync({
            serviceId: "chis.signContractRecordService",
            serviceAction: "checkContractRecord",
            method: "execute",
            SPIID: SPIID

        });
        if (result.json.body && result.json.body > 0) {
        	this.haveRecord=true;
        }
        this.process(r);
    },
    process: function (r) {
        if (!r) {
            return;
        }
        this.haveRecord = r.data.SignFlag ==1?false : true;
        var rData = this.castListDataToForm(r.data, this.pscList.schema);
        this.pData = rData;
        this.exContext.args.SCID = r.get("SCID");
        if (this.scModule) {
            Ext.apply(this.scModule.exContext, this.exContext);
            this.scModule.pData = this.pData;
            this.scModule.initCnd=null;
            this.scModule.loadData();
        }
        //设计右表单"保存签约"按钮，左边列表上"删除签约"状态
//		var signFlag = r.get("signFlag");
//		// if(signFlag=="1"){
//		// 	this.scModule.setSaveButtonDisable(false);
//		// }else{
//		// 	this.scModule.setSaveButtonDisable(true);
//		// }
//		//签约生效超过一天不让删除，当天签约，当天又解约的可以删除
//		var today = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
//		var beginDate = Date.parseDate(r.get("beginDate"),'Y-m-d');
//		if (beginDate.getTime() + 86400000 > today.getTime()) {
//			this.pscList.setBtnStatus(true, "remove");
//		}else{
//			this.pscList.setBtnStatus(false, "remove");
//		}
    },
    doNew: function () {

    },
    loadData: function () {
        Ext.apply(this.pscList.exContext, this.exContext);
        Ext.apply(this.scModule.exContext, this.exContext);
        this.pData = this.exContext.args.pData || '';
        var toolBar = this.pscList.grid.getTopToolbar();
        this.toolBar = toolBar;
        var newBtn = toolBar.find("cmd", "new");
        if (!this.pData) {
            this.pData = this.getPData();
        }
        if (this.scModule) {
            this.scModule.pData = this.pData;
        }
        if (this.exContext.args.signFlag == "2") {
            newBtn[0].hide();
        } else {
            newBtn[0].show();
        }
        this.pscList.requestData.cnd = ['eq', ['$', 'a.favoreeEmpiId'], ['s', this.exContext.ids.empiId || '']];
        this.pscList.loadData();
        // this.scModule.doNew();
    },
    getPData: function () {
        var sex = {"1": "男", "2": "女", "9": "未说明的性别", "0": "未知的性别"}
        var data = this.exContext.empiData;
        if (this.pData) {
            data = this.pData;
        }
        var pData = {};
        pData.personName = data.personName;
        pData.sexCode = {"key": data.sexCode, "text": sex[data.sexCode]};
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
        pData.year={key:"1",text:"1年"};
        return pData;
    },
    onSCModuleSave: function (data) {
/*        this.fireEvent("chisSave");
        if(data.length && data.length>0 && data[0].SCID){
            //this.exContext.args.SCID = SCID;
            this.pscList.refresh();
        }*/
        this.refreshList();
    }
});