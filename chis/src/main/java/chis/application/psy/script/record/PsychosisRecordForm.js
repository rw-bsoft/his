$package("chis.application.psy.script.record");
$import("util.Accredit", "chis.script.BizTableFormView", "app.modules.common");
chis.application.psy.script.record.PsychosisRecordForm = function(cfg) {
	cfg.entryName = "chis.application.psy.schemas.PSY_PsychosisRecord";
	// cfg.exContext.readOnly = false
	cfg.colCount = 4;
	cfg.labelWidth = 100;
	cfg.fldDefaultWidth = 145;
	cfg.autoFieldWidth = false;
	chis.application.psy.script.record.PsychosisRecordForm.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.psy.script.record.PsychosisRecordForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				this.initDataId = this.exContext.ids["PSY_PsychosisRecord.phrId"];
				chis.application.psy.script.record.PsychosisRecordForm.superclass.doNew
						.call(this);
				this.setFSIControlFldsDisable();
			},
			doCheck : function() {
				this.fireEvent("addModule");
			},
			/** 重写父类数据加载参数获取方式 * */
			setButton : function(m, flag) {
				if (this.empiId && this.phrId
						&& this.manaDoctorId != this.mainApp.uid
						&& this.mainApp.jobId != "system") {
					if (this.phrId) {
						Ext.Msg.alert("提示", "该病人责任医生非本人，不能新增接诊记录");
					}
					flag = false;
				}
				var btns;
				var btn;
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					btns = this.form.getTopToolbar();
				} else {
					btns = this.form.buttons;
				}
				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				}

			},

			getLoadRequest : function() {
				var body = {};
				if (this.empiId) {
					body.empiId = this.empiId;
				}
				if (this.initDataId) {
					body.pkey = this.initDataId;
				}
				if (!body.empiId && !body.pkey) {
					return null;
				}
				return body;
			},
			loadData : function() {
				this.loadServiceId = "chis.psychosisRecordService";
				this.loadAction = "initializePsyRecordForm";
				this.empiId = this.exContext.ids.empiId;
				this.initDataId = this.exContext.ids.phrId;
				chis.application.psy.script.record.PsychosisRecordForm.superclass.loadData
						.call(this);
				var form = this.form.getForm();
				form.findField("phrId").setValue(this.exContext.ids.phrId);
			},

			initFormData : function(data) {
				this.myOp = data.op;
				if (this.myOp == "create") {
					this.initDataId = null;
				}
				chis.application.psy.script.record.PsychosisRecordForm.superclass.initFormData
						.call(this, data);
				this.onPastSymptom();
				if (!this.initDataId) {
					this.setButton(["check"], false);
				} else {
					this.setButton(["check"], true);
				}
				this.onFamilySocialImpactFldChange();
			},

			saveToServer : function(saveData) {
				this.op = this.myOp;
				this.fireEvent("save", saveData);
			},
			setFieldDisable : function(disable, fieldId, hasValue) {
				var frm = this.form.getForm();
				var fld = frm.findField(fieldId);
				if (fld) {
					if (disable) {
						if (!hasValue) {
							fld.setValue();
						}
						fld.disable();
					} else {
						fld.enable();
					}
				}
			},
			onReady : function() {
				var form = this.form.getForm();
				var pastSymptomField = form.findField("pastSymptom");
				this.pastSymptomField = pastSymptomField;
				this.pastSymptomField.editable = false;
				pastSymptomField.on("select", this.onPastSymptom, this);

				form.findField("manaDoctorId").on("select",
						this.onManaDoctorIdSelect, this);

				var guardianAddress = form.findField("guardianAddress");
				if (guardianAddress) {
					guardianAddress.on("beforeselect", this.getFamilyRegion,
							this);
				}
				
				form.findField("doctorSign").on("select",this.onDoctorSignSelect,this);
				
				var fsiFld = form.findField("familySocialImpact");
				if(fsiFld){
					var fsiItems = fsiFld.items;
					for (var i = 0, len = fsiItems.length; i < len; i++) {
						var box = fsiItems[i];
						box.listeners = {
							'check' : function(checkedBox, checked) {
								this.onFamilySocialImpactFldItemCheck(checkedBox, checked);
							},
							scope : this
						}
					}
					fsiFld.on("change",this.onFamilySocialImpactFldChange, this);
				}
				
				chis.application.psy.script.record.PsychosisRecordForm.superclass.onReady
						.call(this);
			},

			onPastSymptom : function() {
				var v = this.pastSymptomField.getValue();
				var pastSymptomTextF = this.form.getForm()
						.findField("pastSymptomText");
				var vArray = v.split(",");
				if (vArray.indexOf("99") != -1) {
					pastSymptomTextF.enable();
				} else {
					pastSymptomTextF.setValue();
					pastSymptomTextF.disable();
				}
			},

			onManaDoctorIdSelect : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						});
				this.setManaUnit(result.json.manageUnit);
			},

			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("manaUnitId");
				if (!combox) {
					return;
				}

				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit);
					combox.disable();
				} else {
					combox.enable();
					combox.reset();
				}
			},
			
			onDoctorSignSelect : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						});
				this.setDoctorUnit(result.json.manageUnit);
			},
			setDoctorUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("doctorUnit");
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}
				combox.setValue(manageUnit);
			},

			getFamilyRegion : function(comb, node) {
				var isBottom = node.attributes['isBottom'];
				if (isBottom == 'y') {
					return true;
				} else {
					return false;
				}
			},
			
			onFamilySocialImpactFldItemCheck : function(checkedBox, checked){
				var frm = this.form.getForm();
				var boxGroup = frm.findField("familySocialImpact");
				if (!boxGroup) {
					return;
				}
				var fsiVal = boxGroup.getValue();
				if (fsiVal == "") {
					boxGroup.setValue('0');
				}
				if (fsiVal.indexOf("0") != -1) {
					if (checkedBox.inputValue == "0" && checked) {
						boxGroup.setValue("0")
					} else {
						var valueArray = fsiVal.split(',');
						for (var i = 0, len = valueArray.length; i < len; i++) {
							if (valueArray[i] == "0") {
								valueArray.splice(i, 1);
							}
						}
						boxGroup.setValue(valueArray.join(','));
					}
				}
			},
			onFamilySocialImpactFldChange : function(boxGroup, checkedBoxs){
				if (!boxGroup) {
					var frm = this.form.getForm();
					boxGroup = frm.findField("familySocialImpact");
				}
				var fsiVal = boxGroup.getValue();
				if(fsiVal.indexOf('1') != -1){
					this.setFieldDisable(false, "lightAffray");
				}else{
					this.setFieldDisable(true, "lightAffray");
				}
				if(fsiVal.indexOf('2') != -1){
					this.setFieldDisable(false, "causeTrouble");
				}else{
					this.setFieldDisable(true, "causeTrouble");
				}
				if(fsiVal.indexOf('3') != -1){
					this.setFieldDisable(false, "causeAccident");
				}else{
					this.setFieldDisable(true, "causeAccident");
				}
				if(fsiVal.indexOf('4') != -1){
					this.setFieldDisable(false, "selfHurt");
				}else{
					this.setFieldDisable(true, "selfHurt");
				}
				if(fsiVal.indexOf('5') != -1){
					this.setFieldDisable(false, "suicide");
				}else{
					this.setFieldDisable(true, "suicide");
				}
			},
			setFSIControlFldsDisable : function(){
				this.setFieldDisable(true, "lightAffray");
				this.setFieldDisable(true, "causeTrouble");
				this.setFieldDisable(true, "causeAccident");
				this.setFieldDisable(true, "selfHurt");
				this.setFieldDisable(true, "suicide");
			}
			
		});
