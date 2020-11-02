$package("phis.application.cic.script");

$import("phis.script.TableForm", "phis.script.util.DateUtil");

phis.application.cic.script.IdrReport_HIV = function(cfg) {
	cfg.actions = [{
				id : "confirm",
				name : "保存",
				iconCls : "common_add"
			},{
				id : "close",
				name : "关闭",
				iconCls : "common_cancel"
			}];
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150;
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	//cfg.schema = "";
	phis.application.cic.script.IdrReport_HIV.superclass.constructor
			.apply(this, [ cfg ]);
	this.on("winShow", this.onWinShow, this);
};
Ext.extend(phis.application.cic.script.IdrReport_HIV,
	phis.script.TableForm,{
			doConfirm : function() {
				debugger
				var r = this.data;//IdrReport_HIV//this.opener.opener.getSelectedRecord();
				
				if (r == null) {
					return;
				}
				var _this = this;
				body = {};
				debugger
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='1'&&this.form.getForm().findField("ContactHistoryCode")!='1'){
					MyMessageTip.msg("提示", "最有可能感染途径为注射毒品时，接触史须有“注射毒品史”!", true);
				return;}
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='2'&&(this.form.getForm().findField("ContactHistoryCode")!='2'||this.form.getForm().findField("ContactHistoryCode")!='3')){
					MyMessageTip.msg("提示", "最有可能感染途径为异性传播时，接触史须有“非婚异性性接触史”或“配偶/固定性伴阳性”!", true);
				return;}
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='3'&&this.form.getForm().findField("ContactHistoryCode")!='4'){
					MyMessageTip.msg("提示", "最有可能感染途径为同性传播时，接触史须有“男男性行为史”!", true);
				return;}
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='4'&&this.form.getForm().findField("ContactHistoryCode")!='4'){
					MyMessageTip.msg("提示", "最有可能感染途径为性接触 + 注射毒品，接触史须有“注射毒品史＋（非婚异性性接触史,配偶/固定性伴阳性,男男性行为史三者之一或以上）!", true);
				return;}
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='5'&&this.form.getForm().findField("ContactHistoryCode")!='5'){
					MyMessageTip.msg("提示", "最有可能感染途径为采血(浆)，接触史须有“献血（浆）史”!", true);
				return;}
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='6'&&this.form.getForm().findField("ContactHistoryCode")!='6'){
					MyMessageTip.msg("提示", "最有可能感染途径为输血/血制品，接触史须有“输血/血制品史”!", true);
				return;}
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='7'&&this.form.getForm().findField("ContactHistoryCode")!='7'){
					MyMessageTip.msg("提示", "最有可能感染途径为母婴传播时，接触史须有“母亲阳性”!", true);
				return;}
				if(this.form.getForm().findField("PossibleInfectionRouteCode")=='8'&&this.form.getForm().findField("ContactHistoryCode")!='8'){
					MyMessageTip.msg("提示", "最有可能感染途径为职业暴露时，接触史须有“职业暴楼史”!", true);
				return;}
				if(this.form.getForm().findField("SpecimenSourceCode")=='20'&&this.form.getForm().findField("OtherSampleSource")==""){
					MyMessageTip.msg("提示", "样品来源编码为‘其他’时不能为空!", true);
				return;}
				if(this.form.getForm().findField("ContactHistoryCode")=='11'&&this.form.getForm().findField("OtherContactHistory")==""){
					MyMessageTip.msg("提示", "接触史为‘其他’时不能为空；接触史为其他最长20个字!", true);
				return;}
				if(this.form.getForm().findField("LaborTestConclusionCode")=='1'&&this.form.getForm().findField("ConfirmedTestPositiveDate")==""){
					MyMessageTip.msg("提示", "替代策略检测阳性时，确认（替代策略）检测阳性日期不能为空!", true);
				return;}
				if(this.form.getForm().findField("LaborTestConclusionCode")=='1'&&this.form.getForm().findField("ConfirmedTestPositiveOrgName")==""){
					MyMessageTip.msg("提示", "替代策略检测阳性时，确认（替代策略）检测单位不能为空!", true);
				return;}
				body["IDR_Report"] = this.getFormData();
				body["IDR_Report"].idCard = r.empiData.idCard;
				body["IDR_Report"].SpecimenSourceName = this.form.getForm().findField("SpecimenSourceCode").lastSelectionText;
				body["IDR_Report"].PossibleInfectionRouteName =this.form.getForm().findField("PossibleInfectionRouteCode").lastSelectionText;
				body["IDR_Report"].ContactHistoryName =this.form.getForm().findField("ContactHistoryCode").lastSelectionText;
				body["IDR_Report"].LaborTestConclusionName =this.form.getForm().findField("LaborTestConclusionCode").lastSelectionText;
				body["IDR_Report"].VenerealHistoryName =this.form.getForm().findField("VenerealHistoryCode").lastSelectionText;
				body["IDR_Report"].ChlamydialTrachomatisName =this.form.getForm().findField("ChlamydialTrachomatisCode").lastSelectionText;
				//body=this.data.empiData;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "verify_HIV",
					body : body
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg,
							this.onBeforeSave);
					return;
				} else {
		//			this.panel.el.unmask();
					Ext.Msg.alert("提示", "保存成功！");
					_this.doClose();
					if(_this.opener.opener){
						_this.opener.doClose();
						_this.opener.opener.refresh();
					}else{
						_this.opener.refresh();
					}
				}
			},
			onWinShow : function() {
				this.win.doLayout();
			},
			setButton : function() {
				if (!this.getTopToolbar()) {
					return;
				}
				var bts = this.getTopToolbar().items;
				if (this.op == "read") {
					bts.items[0].disable();
				} else {
					bts.items[0].enable();
				}
			},
//			initFormData : function(data) {
//				debugger
//				this.initDataId = ''
//				var form = this.form.getForm()
//				var items = this.schema.items
//				var n = items.length
//				for (var i = 0; i < n; i++) {
//					var it = items[i]
//					var f = form.findField(it.id)
//					if (f) {
//						var v = data[it.id]
//						if (v != undefined) {
//							if (f.getXType() == "checkbox") {
//								var setValue = "";
//								if (it.checkValue
//										&& it.checkValue.indexOf(",") > -1) {
//									var c = it.checkValue.split(",");
//									checkValue = c[0];
//									unCheckValue = c[1];
//									if (v == checkValue) {
//										setValue = true;
//									} else if (v == unCheckValue) {
//										setValue = false;
//									}
//								}
//								if (setValue == "") {
//									if (v == 1) {
//										setValue = true;
//									} else {
//										setValue = false;
//									}
//								}
//								f.setValue(setValue);
//							} else {
//								if (it.dic && v !== "" && v === 0) {
//									v = "0";
//								}
//								f.setValue(v)
//								if (it.dic && v != "0" && f.getValue() != v) {
//									f.counter = 1;
//									this.setValueAgain(f, v, it);
//								}
//							}
//						}
//						if (it.update == "false") {
//							f.disable();
//						}
//					}
//					this.setKeyReadOnly(true);
//				}
//			},
		doClose : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			}
	});