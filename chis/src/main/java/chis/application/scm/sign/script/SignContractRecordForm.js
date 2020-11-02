$package("chis.application.scm.sign.script")
$import("chis.script.BizTableFormView","util.widgets.LookUpField","chis.script.util.Vtype","chis.script.util.query.QueryModule")

chis.application.scm.sign.script.SignContractRecordForm=function(cfg){
	chis.application.scm.sign.script.SignContractRecordForm.superclass.constructor.apply(this,[cfg]);
	this.labelWidth=100;
    //this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.scm.sign.script.SignContractRecordForm,chis.script.BizTableFormView,{
	onReady : function(){
		 var frm = this.form.getForm();
		 var SPNameFld = frm.findField("SecondPartyName");
		 if(SPNameFld){
		 	SPNameFld.on("lookup",this.doSPNameQuery,this);
		 	SPNameFld.on("clear", this.doSPNameClear, this);
		 }
		var peopleFlag = frm.findField("peopleFlag");
		if (peopleFlag) {
			peopleFlag.on("select", this.onSelectPeopleFlag, this);
		}		
	},
	onSelectPeopleFlag : function(item,index) {
        //2019-10-22 zhaojian 公卫系统签约校验人群分类是否包含健康档案图标分类
		if(this.mainApp.jobId.indexOf("chis")==0 && item.value.length>0){
			 var icons = this.opener.opener.ehrview.icos;
			 var matecons = "";
			 if (icons) {
				var items = this.form.getForm().findField("peopleFlag").store.data.items;
				for (var j = 0; j < items.length; j++) {
					if(!items[j].json.properties){
						continue;
					}
					if(!items[j].json.properties.ehrviewnav_key){
						continue;
					}
					if(icons.indexOf(items[j].json.properties.ehrviewnav_key)>=0 && !items[j].data.checked){
						matecons += items[j].data.text+",";
					}
				}
			 }
			 if(matecons.length>0){
			 	MyMessageTip.msg("温馨提醒", "选择的人群分类与健康档案分类（"+matecons.substring(0,matecons.length-1)+"）有出入！", true);
			 }
		}
        //var pData = this.exContext.args.pData;
        var intendedPopulation_id = index.json.key;
        var msg = "";
        if (item.value != "10" && item.value.indexOf("10")>=0) {//一般人群不能与重点人群共存
            msg = "不能同时选择一般人群和重点人群！";
        }        
        var currentAge = this.exContext.empiData.age;
		currentAge++;//虚岁和健康档案对接
		var birthday = Date.parseDate(this.exContext.empiData.birthday, "Y-m-d");
		var monAge = chis.script.util.helper.Helper.getAgeMonths(birthday,this.getServerDate());
		//2019-11-22 zhaojian 就是根据业务要求：59岁11个月（719个月）就等于60岁
		if(monAge == 719){
			currentAge++;
		}
        if (intendedPopulation_id == "03") {//60岁及以上老年人
            if (currentAge < 60) {
            	msg = "当前居民年龄不适合签当前签约包！";
            }
        }
        if (intendedPopulation_id == "02") {//0-6岁儿童
            if (this.exContext.empiData.age > 6) {
                //不能选择儿童包
            	msg = "当前居民年龄不适合签当前签约包！";
            }
        }
        if (intendedPopulation_id == "01") {//孕产妇
            if (this.exContext.empiData.sexCode == "1") {
            	msg = "当前居民性别不适合签当前签约包！";
            }
        }
        if(msg != ""){
			MyMessageTip.msg("提示", msg, true);
            index.set(item.checkField, false);
			item.setValue(item.getCheckedValue());
            return;
        }
        this.fireEvent("rqflQuery", item.getValue());
	},
	getServerDate : function() {
		var serverDate = this.mainApp.serverDate;
		return new Date(Date.parse(serverDate.replace(/-/g, "/")));
	},
	onSCDateSelect : function(){
		var frm = this.form.getForm();
		var scDateFld = frm.findField("scDate");
		var beginDateFld = frm.findField("beginDate");
		 if(scDateFld && beginDateFld){
		 	beginDateFld.setMinValue(scDateFld.getValue());
		 }
	},
	onBeginDateSelect : function(){
		var frm = this.form.getForm();
		var beginDateFld = frm.findField("beginDate");
		var endDateFld = frm.findField("endDate");
		if(beginDateFld && endDateFld){
			var bDate = beginDateFld.getValue();
			if(bDate){
				var endDate = bDate;
				endDate.setYear(bDate.getFullYear() + 1);
				endDateFld.setValue(endDate);
			}
		}
	},
	doFNameQuery : function(field){
		 if (!field.disabled) {
            var FNQuery = this.midiModules["FNQuery"];
            if (!FNQuery) {
                FNQuery = new chis.script.util.query.QueryModule(
                    {
                        title: "人员查询",
                        autoLoadSchema: true,
                        isCombined: true,
                        autoLoadData: false,
                        isAutoScroll: false,
                        mutiSelect: false,
                        queryCndsType: "1",
                        selectFormColCount:3,
                        entryName: "chis.application.mpi.schemas.MPI_DemographicInfoQuery",
                        buttonIndex: 3,
                        width:735,
                        height:380,
                        itemHeight: 123
                    });
                this.midiModules["FNQuery"] = FNQuery;
                FNQuery.on("recordSelected", function (r) {
					if (!r) {
						return;
					}
					var rData = r[0].data;
					var frm = this.form.getForm();
					var favoreeNameFld = frm.findField("favoreeName");
					if(favoreeNameFld){
						 	favoreeNameFld.setValue(rData.personName);
					}
					this.data.favoreeEmpiId=rData.empiId;
                }, this);
            }
            var win = FNQuery.getWin();
            win.setPosition(250, 100);
            win.show();
//            var familyId = this.familyId;
//            if(familyId){//field.name=="FC_Repre" &&
//                var itemTypeFld = FNQuery.form.form.getForm().findField("familyId");
//                if(itemTypeFld){
//                    itemTypeFld.setValue(familyId);
//                    itemTypeFld.disable();
//                }
//            }
            FNQuery.form.doSelect();
        }
	},
	doFNameClear : function(){
		this.data.favoreeEmpiId="";
		var frm = this.form.getForm();
		var favoreeNameFld = frm.findField("favoreeName");
		if(favoreeNameFld){
			 	favoreeNameFld.setValue();
		}
	},
	doSPNameQuery : function(field){
		 if (!field.disabled) {
            var SPNQuery = this.midiModules["SPNQuery"];
            if (!SPNQuery) {
                SPNQuery = new chis.script.util.query.QueryModule(
                    {
                        title: "人员查询",
                        autoLoadSchema: true,
                        isCombined: true,
                        autoLoadData: false,
                        isAutoScroll: false,
                        mutiSelect: false,
                        queryCndsType: "1",
                        selectFormColCount:3,
                        entryName: "chis.application.mpi.schemas.MPI_DemographicInfoQuery",
                        buttonIndex: 3,
                        width:735,
                        height:380,
                        itemHeight: 123
                    });
                this.midiModules["SPNQuery"] = SPNQuery;
                SPNQuery.on("recordSelected", function (r) {
					if (!r) {
						return;
					}
					var rData = r[0].data;
					var frm = this.form.getForm();
					var SPNameFld = frm.findField("SecondPartyName");
					if(SPNameFld){
						 	SPNameFld.setValue(rData.personName);
					}
					this.data.SecondPartyEmpiId=rData.empiId;
                }, this);
            }
            var win = SPNQuery.getWin();
            win.setPosition(250, 100);
            win.show();
//            var familyId = this.familyId;
//            if(familyId){//field.name=="FC_Repre" &&
//                var itemTypeFld = FNQuery.form.form.getForm().findField("familyId");
//                if(itemTypeFld){
//                    itemTypeFld.setValue(familyId);
//                    itemTypeFld.disable();
//                }
//            }
            SPNQuery.form.doSelect();
        }
	},
	doSPNameClear : function(){
		this.data.SecondPartyEmpiId="";
		var frm = this.form.getForm();
		var SPNameFld = frm.findField("SecondPartyName");
		if(SPNameFld){
			 	SPNameFld.setValue();
		}
	},
	onIntendedPopulationSelect : function(theFld,sr){
		if(!theFld){
			var frm = this.form.getForm();
			theFld = frm.findField("intendedPopulation");
		}
	 	var value = theFld.getValue();
		var valueArray = value.split(",");
		if(valueArray.indexOf("3") != -1){
			theFld.clearValue();
			if (sr.data.key == "3") {
				theFld.setValue({
							key : "3",
							text : "儿童"
						});
			} else {
				theFld.setValue(sr.data.key);
			}
		}
		if (valueArray.indexOf("0") != -1) {
			theFld.clearValue();
			if (sr.data.key == "0") {
				theFld.setValue({
							key : "0",
							text : "非重点人群"
						});
			} else {
				theFld.setValue(sr.data.key);
			}
		}
		if (value == "") {
			theFld.setValue({
						key : "0",
						text : "非重点人群"
					});
		}
	},
	doONameQuery : function(field){
		if (!field.disabled) {
            var ONQuery = this.midiModules["ONQuery"];
            if (!ONQuery) {
                ONQuery = new chis.script.util.query.QueryModule(
                    {
                        title: "人员查询",
                        autoLoadSchema: true,
                        isCombined: true,
                        autoLoadData: false,
                        isAutoScroll: false,
                        mutiSelect: false,
                        queryCndsType: "1",
                        selectFormColCount:2,
                        entryName: "chis.application.fhr.schemas.EHR_FamilyRecordQuery",
                        buttonIndex: 3,
                        width:735,
                        height:380,
                        itemHeight: 123
                    });
                this.midiModules["ONQuery"] = ONQuery;
                ONQuery.on("recordSelected", function (r) {
					if (!r) {
						return;
					}
					var rData = r[0].data;
					var frm = this.form.getForm();
					var ONameFld = frm.findField("ownerName");
					if(ONameFld){
						 	ONameFld.setValue(rData.ownerName);
					}
					this.data.familyId=rData.familyId;
                }, this);
            }
            var win = ONQuery.getWin();
            win.setPosition(250, 100);
            win.show();
//            var familyId = this.familyId;
//            if(familyId){//field.name=="FC_Repre" &&
//                var itemTypeFld = FNQuery.form.form.getForm().findField("familyId");
//                if(itemTypeFld){
//                    itemTypeFld.setValue(familyId);
//                    itemTypeFld.disable();
//                }
//            }
            ONQuery.form.doSelect();
		}
	},
	doONameClear : function(){
		this.data.familyId="";
		var frm = this.form.getForm();
		var ONameFld = frm.findField("ownerName");
		if(ONameFld){
			 	ONameFld.setValue();
		}
	},
	onStopDateChange  : function(field){
		 var frm = this.form.getForm();
		 var stopDateFld = frm.findField("stopDate");
		 var stopReasonFld = frm.findField("stopReason");
		 if(stopDateFld){
		 	var stopDate = stopDateFld.getValue();
		 	if(stopDate){
		 		if(stopReasonFld){
		 			stopReasonFld.allowBlank = false;
					stopReasonFld["not-null"] = true;
					Ext.getCmp(stopReasonFld.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>解约原因:</span>");
					stopReasonFld.validate();		
		 		}
		 	}else{
		 		if(stopReasonFld){
			 		stopReasonFld.allowBlank = true;
						stopReasonFld["not-null"] = false;
						Ext.getCmp(stopReasonFld.id).getEl().up('.x-form-item')
								.child('.x-form-item-label')
								.update("解约原因:");
					stopReasonFld.validate();			
		 		}
		 	}
		 }
	},
	onStopReasonChange : function(){
		 var frm = this.form.getForm();
		 var stopReasonFld = frm.findField("stopReason");
		 var signFlagFld = frm.findField("signFlag");
		 if(stopReasonFld){
		 	var srv = stopReasonFld.getValue();
		 	if(srv){
		 		if(signFlagFld){
		 			signFlagFld.setValue({"key":"2","text":"解约"});
		 		}
		 	}else{
		 		if(signFlagFld){
		 			signFlagFld.setValue({"key":"1","text":"签约"});
		 		}
		 	}
		 }
	},
	setStopField : function(disable){
		var frm = this.form.getForm();
		var stopDateFld = frm.findField("stopDate");
		var stopReason = frm.findField("stopReason");
		if(disable){
			if(stopDateFld){
				stopDateFld.disable();
			}
			if(stopReason){
				stopReason.disable();
			}
		}else{
			if(stopDateFld){
				stopDateFld.enable();
			}
			if(stopReason){
				stopReason.enable();
			}
		}
	},
	doPrintProcotol : function() {
//		this.visitId = this.data["visitId"];
//		if (!this.visitId) {
//			this.visitId = "23333333";// 当this.visitId 为空的时候，随便设置值
//		}
		this.SCID = this.data["SCID"];
		if (!this.visitId) {
			this.visitId = "23333333";// 当this.visitId 为空的时候，随便设置值
		}
		var url = "resources/chis.prints.template.familyContractBaseNew.print?type="
			+ 1 + "&SCID=" + this.SCID;
		url += "&temp=" + new Date().getTime()
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
	},
	setCreateUnit : function(){
		 util.rmi.jsonRequest({
            serviceId: "chis.signContractRecordService",
            method: "execute",
            serviceAction: "getCreateUnit"
        }, function (code, msg, json) {
            if (code > 300) {
                return;
            }
            if (json.body && json.body.length>0) {
            	var createUnit = this.form.getForm().findField("createUnit");
            	var r = json.body[0];
            	createUnit.setValue({key: r.ORGANIZCODE, text: r.ORGANIZNAME});
            }
        }, this);		 
	},
	setSigPackage : function(){
		debugger;
		var empiid = this.exContext.ids.empiId;
		util.rmi.jsonRequest({
           serviceId: "chis.signContractRecordService",
           method: "execute",
           serviceAction: "getSigPackage",
           body:{
        	  "empiid" : empiid
           }
		
       }, function (code, msg, json) {
           if (code > 300) {
               return;
           }
           if (json.body) {
           	var r = json.body.SIGNSERVICEPACKAGES;
           	var signServicePackages = this.form.getForm().findField("signServicePackages");
           	signServicePackages.setValue(r);
           }
       }, this);		 
	}
});