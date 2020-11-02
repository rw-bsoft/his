/**
 * 儿童出生缺陷监测表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.defect")
$import("chis.script.BizFieldSetFormView")
chis.application.cdh.script.defect.ChildrenDefectRecordForm = function(cfg) {
	cfg.fldDefaultWidth = 145
	cfg.autoFieldWidth = false;
	cfg.colCount = 4;
	cfg.labelWidth = 110;
	chis.application.cdh.script.defect.ChildrenDefectRecordForm.superclass.constructor.apply(
			this, [cfg])
	this.initServiceAction = "initChildDefect";
	this.on("loadNoData", this.onLoadNoData, this)
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(chis.application.cdh.script.defect.ChildrenDefectRecordForm,
		chis.script.BizFieldSetFormView, {

			onLoadNoData : function() {
				this.data.phrId = this.exContext.ids["CDH_HealthCard.phrId"];
				this.data["empiId"] = this.exContext.ids.empiId;
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initServiceAction,
							method:"execute",
							body : {
								"empiId" : this.exContext.ids.empiId
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.body) {
								this.initFormData(json.body);
								this.initDataId = null;
							}
						}, this)

			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"phrId" : this.exContext.ids["CDH_HealthCard.phrId"]
				};
			},

			onReady : function() {
				chis.application.cdh.script.defect.ChildrenDefectRecordForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();

				var twins = form.findField("twins");
				if (twins) {
					twins.on("select", this.setTwinsType, this);
				}

				var inducedAbortion = form.findField("inducedAbortion");
				if (inducedAbortion) {
					inducedAbortion.on("select", this.setDefectName, this);
				}

				var birthAddress = form.findField("birthAddress");
				if (birthAddress) {
					birthAddress.on("select", this.setDetailAddress, this);
				}

				var deformityConfirmDate = form
						.findField("deformityConfirmDate");
				if (deformityConfirmDate) {
					deformityConfirmDate.on("select", this.setAntenatalWeeks,
							this);
				}

				var diagnoseBasis = form.findField("diagnoseBasis");
				if (diagnoseBasis) {
					diagnoseBasis.on("select", this.setOtherBasis, this);
				}

				var defectDiagnose = form.findField("defectDiagnose");
				if (defectDiagnose) {
					defectDiagnose.on("select", this.setOtherDiagnose, this);
				}

				var illFlag = form.findField("illFlag");
				if (illFlag) {
					illFlag.on("select", this.setIllFlag, this);
				}

				var medicineFlag = form.findField("medicineFlag");
				if (medicineFlag) {
					medicineFlag.on("select", this.setMedicineFlag, this);
				}

				var otherFactors = form.findField("otherFactors");
				if (otherFactors) {
					otherFactors.on("select", this.setOtherFactors, this);
				}

				var consanguineousMarriage = form
						.findField("consanguineousMarriage");
				if (consanguineousMarriage) {
					consanguineousMarriage.on("select", this.setConMarriage,
							this);
				}

				var geneticDefect1 = form.findField("geneticDefect1");
				if (geneticDefect1) {
					geneticDefect1.on("valid", this.setRelationsField, this);
				}

				var geneticDefect2 = form.findField("geneticDefect2");
				if (geneticDefect2) {
					geneticDefect2.on("valid", this.setRelationsField, this);
				}

				var geneticDefect3 = form.findField("geneticDefect3");
				if (geneticDefect3) {
					geneticDefect3.on("valid", this.setRelationsField, this);
				}

				var defectiveChild = form.findField("defectiveChild");
				if (defectiveChild) {
					defectiveChild.on("valid", this.setDefectField, this);
				}

			},

			setDefectField : function(field) {
				var fieldName = field.getName();
				var value = field.getValue();
				this.setDefects(fieldName, value);
			},

			setDefects : function(value) {
				if (value && value != "") {
					this.changeFieldState(false, "defect1");
					this.changeFieldState(false, "defect2");
					this.changeFieldState(false, "defect3");
				}
			},

			setRelationsField : function(field) {
				var fieldName = field.getName();
				var value = field.getValue();
				this.setRelations(fieldName, value);
			},

			setRelations : function(fieldName, fieldValue) {
				if (fieldValue && fieldValue != "") {
					if (fieldName.indexOf("1") > -1)
						this.changeFieldState(false, "relations1");
					else if (fieldName.indexOf("2") > -1)
						this.changeFieldState(false, "relations2");
					else if (fieldName.indexOf("3") > -1)
						this.changeFieldState(false, "relations3");
				}
			},

			setDefectName : function(field) {
				var value = field.value
				var disable = true;
				if (value == "y") {
					disable = false;
				}
				this.changeFieldState(disable, "defectName");
			},

			setTwinsType : function(field) {
				var value = field.value
				var disable = true;
				if (value != "1") {
					disable = false;
				}
				this.changeFieldState(disable, "twinsType");
			},

			setDetailAddress : function(field) {
				var value = field.value
				var disable = true;
				if (value == "2") {
					disable = false;
				}
				this.changeFieldState(disable, "detailAddress");
			},

			setAntenatalWeeks : function(field) {
				var value = field.value
				var disable = true;
				if (value == "1") {
					disable = false;
				}
				this.changeFieldState(disable, "antenatalWeeks");
			},

			setOtherBasis : function(field) {
				var value = field.value
				var disable = true;
				if (value == "6") {
					disable = false;
				}
				this.changeFieldState(disable, "otherBasis");
			},

			setOtherDiagnose : function(field) {
				var value = field.value
				var valueArray = value.split(",");
				var disable = true;
				if (valueArray.indexOf("99") != -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherDiagnose");
			},

			setIllFlag : function(field) {
				var value = field.value
				var valueArray = value.split(",");
				var infectStatus = true;
				var otherIllStatus = true;
				if (valueArray.indexOf("2") != -1) {
					infectStatus = false;
				}
				if (valueArray.indexOf("9") != -1) {
					otherIllStatus = false;
				}
				this.changeFieldState(infectStatus, "infection");
				this.changeFieldState(otherIllStatus, "otherIllness");
			},

			setMedicineFlag : function(field) {
				var value = field.value
				var disable = true;
				if (value == "y") {
					disable = false;
				}
				this.changeFieldState(disable, "sulfa");
				this.changeFieldState(disable, "antibiotics");
				this.changeFieldState(disable, "contraceptives");
				this.changeFieldState(disable, "otherMedicine");
			},

			setOtherFactors : function(field) {
				var value = field.value
				var disable = true;
				if (value == "1") {
					disable = false;
					this.changeFieldState(disable, "drink");
					this.changeFieldState(!disable, "pesticides");
					this.changeFieldState(!disable, "ray");
					this.changeFieldState(!disable, "chemicals");
					this.changeFieldState(!disable, "others");
				} else if (value == "2") {
					disable = false;
					this.changeFieldState(disable, "pesticides");
					this.changeFieldState(!disable, "drink");
					this.changeFieldState(!disable, "ray");
					this.changeFieldState(!disable, "chemicals");
					this.changeFieldState(!disable, "others");
				} else if (value == "3") {
					disable = false;
					this.changeFieldState(disable, "ray");
					this.changeFieldState(!disable, "drink");
					this.changeFieldState(!disable, "pesticides");
					this.changeFieldState(!disable, "chemicals");
					this.changeFieldState(!disable, "others");
				} else if (value == "4") {
					disable = false;
					this.changeFieldState(disable, "chemicals");
					this.changeFieldState(!disable, "drink");
					this.changeFieldState(!disable, "pesticides");
					this.changeFieldState(!disable, "ray");
					this.changeFieldState(!disable, "others");
				} else if (value == "9") {
					disable = false;
					this.changeFieldState(disable, "others");
					this.changeFieldState(!disable, "drink");
					this.changeFieldState(!disable, "pesticides");
					this.changeFieldState(!disable, "ray");
					this.changeFieldState(!disable, "chemicals");
				}
			},

			setConMarriage : function(field) {
				var value = field.value
				var disable = true;
				if (value == "y") {
					disable = false;
				}
				this.changeFieldState(disable, "consanguineousRelations");
			},

			onLoadData : function(entryName, body) {
        
				var twins = body["twins"]
				if (twins) {
					var disable = true;
					if (twins.key != "1") {
						disable = false;
					}
					this.changeFieldState(disable, "twinsType");
				}

				var inducedAbortion = body["inducedAbortion"]
				if (inducedAbortion) {
					var disable = true;
					if (inducedAbortion.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "defectName");
				}

				var birthAddress = body["birthAddress"]
				if (birthAddress) {
					var disable = true;
					if (birthAddress.key == "2") {
						disable = false;
					}
					this.changeFieldState(disable, "detailAddress");
				}

				var deformityConfirmDate = body["deformityConfirmDate"]
				if (deformityConfirmDate) {
					var disable = true;
					if (deformityConfirmDate.key == "1") {
						disable = false;
					}
					this.changeFieldState(disable, "antenatalWeeks");
				}

				var diagnoseBasis = body["diagnoseBasis"]
				if (diagnoseBasis) {
					var disable = true;
					if (diagnoseBasis.key == "6") {
						disable = false;
					}
					this.changeFieldState(disable, "otherBasis");
				}

				var defectDiagnose = body["defectDiagnose"]
				if (defectDiagnose && defectDiagnose.key) {
					var disable = true;
					var valueArray = defectDiagnose.key.split(",");
					if (valueArray.indexOf("99") != -1) {
						disable = false;
					}
					this.changeFieldState(disable, "otherDiagnose");
				}

				var illFlag = body["illFlag"]
				if (illFlag) {
					var valueArray = illFlag.key.split(",");
					var infectStatus = true;
					var otherIllStatus = true;
					if (valueArray.indexOf("2") != -1) {
						infectStatus = false;
					}
					if (valueArray.indexOf("9") != -1) {
						otherIllStatus = false;
					}
					this.changeFieldState(infectStatus, "infection");
					this.changeFieldState(otherIllStatus, "otherIllness");
				}

				var medicineFlag = body["medicineFlag"]
				if (medicineFlag) {
					var disable = true;
					if (medicineFlag.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "sulfa");
					this.changeFieldState(disable, "antibiotics");
					this.changeFieldState(disable, "contraceptives");
					this.changeFieldState(disable, "otherMedicine");
				}

				var otherFactors = body["otherFactors"]
				if (otherFactors) {
					var disable = true;
					var value = otherFactors.key;
					if (value == "1") {
						disable = false;
						this.changeFieldState(disable, "drink");
						this.changeFieldState(!disable, "pesticides");
						this.changeFieldState(!disable, "ray");
						this.changeFieldState(!disable, "chemicals");
						this.changeFieldState(!disable, "others");
					} else if (value == "2") {
						disable = false;
						this.changeFieldState(disable, "pesticides");
						this.changeFieldState(!disable, "drink");
						this.changeFieldState(!disable, "ray");
						this.changeFieldState(!disable, "chemicals");
						this.changeFieldState(!disable, "others");
					} else if (value == "3") {
						disable = false;
						this.changeFieldState(disable, "ray");
						this.changeFieldState(!disable, "drink");
						this.changeFieldState(!disable, "pesticides");
						this.changeFieldState(!disable, "chemicals");
						this.changeFieldState(!disable, "others");
					} else if (value == "4") {
						disable = false;
						this.changeFieldState(disable, "chemicals");
						this.changeFieldState(!disable, "drink");
						this.changeFieldState(!disable, "pesticides");
						this.changeFieldState(!disable, "ray");
						this.changeFieldState(!disable, "others");
					} else if (value == "9") {
						disable = false;
						this.changeFieldState(disable, "others");
						this.changeFieldState(!disable, "drink");
						this.changeFieldState(!disable, "pesticides");
						this.changeFieldState(!disable, "ray");
						this.changeFieldState(!disable, "chemicals");
					}
				}

				var conMarriage = body["consanguineousMarriage"]
				if (conMarriage) {
					var disable = true;
					if (conMarriage.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "consanguineousRelations");
				}

				var geneticDefect1 = body["geneticDefect1"]
				this.setRelations("geneticDefect1", geneticDefect1);

				var geneticDefect2 = body["geneticDefect2"]
				this.setRelations("geneticDefect2", geneticDefect2);

				var geneticDefect3 = body["geneticDefect3"]
				this.setRelations("geneticDefect3", geneticDefect3);

				var defectiveChild = body["defectiveChild"]
				this.setDefects(defectiveChild);
			}
		});