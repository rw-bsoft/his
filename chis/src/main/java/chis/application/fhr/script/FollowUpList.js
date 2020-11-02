/**
 * 随访列表
 * 
 * @author zhouw
 */
$package("chis.application.fhr.script");

$import("chis.script.BizSimpleListView", "chis.script.EHRView",
		"chis.application.fhr.script.AllVisitModels");

chis.application.fhr.script.FollowUpList = function(cfg) {
	cfg.westWidth = 150;
	chis.application.fhr.script.FollowUpList.superclass.constructor.apply(this,
			[cfg]);
	Ext.apply(this, chis.application.fhr.script.AllVisitModels)
}
Ext.extend(chis.application.fhr.script.FollowUpList,
		chis.script.BizSimpleListView, {
			loadData : function() {
				// this.requestData.cnd||
				var formDataValue = this.cndField.getValue();
				var toDataValue = this.cndField1.getValue();
				if (this.requestData.formDataValue) {
					delete this.requestData.formDataValue;
				}
				if (this.requestData.toDataValue) {
					delete this.requestData.toDataValue;
				}
				if (formDataValue != null && formDataValue !== ""
						&& formDataValue !== 0) {
					formDataValue = formDataValue.format('Y-m-d');
					this.requestData.formDataValue = formDataValue;
				}
				if (toDataValue != null && toDataValue !== ""
						&& toDataValue != 0) {
					toDataValue = toDataValue.format('Y-m-d');
					this.requestData.toDataValue = toDataValue;
				}
				this.requestData.cnd = ["eq", ["$", "familyId"],
						["s", this.exContext.args.initDataId || '']];
				chis.application.fhr.script.FollowUpList.superclass.loadData
						.call(this);
			},
			onDblClick : function(){
				this.doUpdateInfo();
			},
			doUpdateInfo : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.empiId = r.get("empiId");
				this.recordId = r.get("recordId");
				this.visitId = r.get("visitId");
				var businessType = r.get("businessType");
				// 各种专档随访的情况
				// 高血压//HypertensionRecordListView
				if (businessType == "1") {
					this.recordStatus = r.get("status");
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.hypertensionService",
								serviceAction : "checkUpGroupRecord",
								method : "execute",
								empiId : this.empiId
							});
					if (result.code != 200) {
						this.processReturnMsg(result.code, result.msg);
						return null;
					}
					if (result.json.haveRecord) {
						this.activeTab = 2;
					} else {
						this.activeTab = 1;
					}
					this.showEhrViewWinGXY();
				}
				// 糖尿病随访//DiabetesRecordListView
				if (businessType == "2") {
					this.activeTab = 2;
					this.showEhrViewWinTNB();
				}
				if (businessType == "3") {//老版本肿瘤模块，该编号已经无用
					//无
				}
				// 老年人 OldPeopleVisitList.js
				if (businessType == "4") {
					this.showModuleLNR(r.data);
				}
				// 儿童询问
				if (businessType == "5") {
					this.activeTab = 2;
					this.birthday = r.get("birthday");
					this.showEHRViewChildrenCheck(this.empiId);
				}
				if(businessType == "6"){//儿童体格检查
					this.activeTab = 3;
					this.birthday = r.get("birthday");
					this.showEHRViewChildrenCheck(this.empiId);
				}
				if(businessType == "7"){//体弱儿童随访
					this.activeTab = 1;
					this.showEhrViewDebilityChildrenVisit();
				}
				// 孕妇随访
				if (businessType == "8") {
					this.showModuleYFSF(this.empiId,this.recordId,this.visitId);
				}
				if(businessType == "9"){//孕妇高危随访
					//暂缺陷（业务不明）
				}
				// 精神随访 TumourHighRiskVisitView.js//已经弄好了
				if (businessType == "10") {
					var data = {};
					data.empiId = r.get("empiId");
					data.manaDoctorId = r.get("manaDoctorId");
					data.op = "update";
					this.onEmpiSelected(data);
				}
				if(businessType == "11"){//高血压询问
					//无
				}
				if(businessType == "12"){//糖尿病疑似随访
					//无
				}
				if(businessType == "13"){//高血压高危随访
					this.ShowEHRViewHypertensionRiskVisit(this.empiId);
				}
				// 离休干部随访 rvc.app
				if (businessType == "14") {
					this.showModuleLXGB(this.empiId);
				}
				if(businessType == "15"){//肿瘤高危随访
					this.empiId = r.get("empiId");
					this.recordStatus = r.get("status");
					this.highRiskType = r.get("highRiskType")||'';
					this.activeTab = 2;
					var planId = r.get("planId");
					this.THRID = r.get("recordId");
					if(this.empiId == ''){
						MyMessageTip.msg("提示","计划中empiId为空！",true);
						return;
					}
					this.showEhrViewWinTHRVisit(planId);
				}
				if(businessType == "16"){//肿瘤现患随访
					this.empiId = r.get("empiId");
					this.TPRCID = r.get("recordId");
					this.highRiskType = r.get("highRiskType") ||'';
					var dieFlag = r.get("dieFlag");
					this.recordStatus = r.get("status");
					this.activeTab = 1;
					this.showEhrViewWinTPRVisit();
				}
				if(businessType == "17"){//糖尿病高危管理
					this.ShowEHRViewDiabetesOGTTVisit(this.empiId);
				}
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!(it.queryable == "true")) {
						continue
					}
					fields.push({
								value : i,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return fields;
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = new Ext.form.ComboBox({
							store : store,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : '选择查询字段',
							selectOnFocus : true,
							editable : false,
							width : 120,
							hidden : true
						});
				combox.on("select", this.onCndFieldSelect, this)
				this.cndFldCombox = combox
				var curDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
				var startDateValue = new Date(curDate.getFullYear(),0,1);
				var cndField = new Ext.form.DateField({
							width : 100,
							selectOnFocus : true,
							id : 'fromData',
							name : "fromData",
							xtype : 'datefield',
							emptyText : "请选择日期",
							value : startDateValue,
							format : 'Y-m-d'
						})
				var cndField1 = new Ext.form.DateField({
							width : 100,
							selectOnFocus : true,
							id : 'toData',
							name : "toData",
							xtype : 'datefield',
							emptyText : "请选择日期",
							value : curDate,
							format : 'Y-m-d'
						})
				var texth = new Ext.form.Label({
							xtype : "label",
							forId : "window",
							text : "→"
						});
				var texth1 = new Ext.form.Label({
							xtype : "label",
							forId : "window",
							text : "计划开始日期:"
						});
				this.texth1 = texth1;
				this.texth = texth;
				this.cndField = cndField
				this.cndField1 = cndField1
				var queryBtn = new Ext.Toolbar.Button({
							iconCls : "query"
						})
				this.queryBtn = queryBtn;
				queryBtn.on("click", this.doCndQuery, this);
				return [texth1, "&nbsp;&nbsp;&nbsp;", cndField, texth,
						cndField1, queryBtn, "      "]
			},
			doCndQuery : function() {
				var formDataValue = this.cndField.getValue();
				var toDataValue = this.cndField1.getValue();
				var data = {};
				if (this.requestData.formDataValue) {
					delete this.requestData.formDataValue;
				}
				if (this.requestData.toDataValue) {
					delete this.requestData.toDataValue;
				}
				if (formDataValue != null && formDataValue !== ""
						&& formDataValue !== 0) {
					formDataValue = formDataValue.format('Y-m-d');
					this.requestData.formDataValue = formDataValue;
				}
				if (toDataValue != null && toDataValue !== ""
						&& toDataValue != 0) {
					toDataValue = toDataValue.format('Y-m-d');
					this.requestData.toDataValue = toDataValue;
				}
				this.requestData.cnd = this.requestData.cnd
						|| ["eq", ["$", "familyId"],
								["s", this.exContext.args.initDataId || '']];
				chis.application.fhr.script.FollowUpList.superclass.loadData
						.call(this);
			},
			onEmpiSelected : function(data) {
				this.empiId = data.empiId;
				if (data.op && data.op == "update") {
					var manaDoctorId = data.manaDoctorId;
					if (manaDoctorId != this.mainApp.uid && !this.mainApp.fd
							&& this.mainApp.uid != 'system') {
						Ext.MessageBox.alert("提示", "非该精神病档案责任医生，没有权限查看！");
						return;
					}
					if (this.mainApp.fd && manaDoctorId != this.mainApp.fd
							&& manaDoctorId != this.mainApp.uid) {
						Ext.MessageBox
								.alert("提示", "非该精神病档案责任医生或相关医生助理，没有权限查看！");
						return;
					}
				}
				// 显示EHRView
				this.showEHRViewMuleJSB();
			}
		})
