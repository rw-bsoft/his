$package("chis.application.fhr.script")

$import("app.modules.form.TableFormView", "util.widgets.TreeField")

chis.application.fhr.script.FamilyContractBaseFormView = function(cfg) {
	cfg.width = 830
	cfg.colCount = 2
	cfg.autoFieldWidth = true
	cfg.fldDefaultWidth = 180
	cfg.actions = {}
	this.entryName = "chis.application.fhr.schemas.EHR_FamilyContractBase"
	chis.application.fhr.script.FamilyContractBaseFormView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.FamilyContractBaseFormView,
		app.modules.form.TableFormView, {
			loadData : function() {
				chis.application.fhr.script.FamilyContractBaseFormView.superclass.loadData.call(this);
				this.form.getForm().findField("FC_Stop_Date").enable();
				this.form.getForm().findField("FC_Stop_Reason").enable();
			},
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]

						if (v != undefined) {
							f.setValue(v)
						} else {
							f.setValue();
						}
						if (it.update == false) {
							f.disable();
						}
					}
				}
				this.setKeyReadOnly(true)
			},
			onReady : function() {
				chis.application.fhr.script.FamilyContractBaseFormView.superclass.onReady.call(this)
				var form = this.form.getForm();
				var beginDate = form.findField("FC_Begin");
				this.onChangeBeginDate();
				beginDate.on("change", this.onChangeBeginDate, this);
				form.findField("FC_Stop_Date").on("change",this.onStopDateChange, this)
//				this.FC_Repre = form.findField("FC_Repre");
				this.FC_RepreName = form.findField("FC_RepreName");
				this.FC_Phone = form.findField("FC_Phone");
				this.FC_Party = form.findField("FC_Party");
				this.FC_Party2 = form.findField("FC_Party2");
				var FC_FamilyTeamId= form.findField("FC_FamilyTeamId");
				if(FC_FamilyTeamId){
					this.FC_FamilyTeamId=FC_FamilyTeamId;
					this.FC_FamilyTeamId.on("select", this.changedoctor,this);
				}
				var FC_FamilyDoctorId=form.findField("FC_FamilyDoctorId");
				if(FC_FamilyDoctorId){
					this.FC_FamilyDoctorId=FC_FamilyDoctorId;
					this.FC_FamilyDoctorId.on("select", this.changeFC_FamilyTeamId,this);
				}
				var villageDoctorId=form.findField("villageDoctorId");
				if(villageDoctorId){
					this.villageDoctorId=villageDoctorId;
					this.villageDoctorId.on("select", this.changevillageDoctorId,this);
				}
				var hospitalDoctorId=form.findField("hospitalDoctorId");
				if(hospitalDoctorId){
					this.hospitalDoctorId=hospitalDoctorId;
					this.hospitalDoctorId.on("select", this.changehospitalDoctorId,this);
				}
			},
			changedoctor: function() {
				var dic={};
					dic.id="chis.dictionary.familydoctors";
					dic.filter="['and',['eq',['$','item.properties.familyTeamId'],['s','"+this.FC_FamilyTeamId.value+"']]]";
					
				if(this.villageDoctorId){
					this.villageDoctorId.store.proxy = new util.dictionary.HttpProxy({
									method : "GET",
									url : util.dictionary.SimpleDicFactory.getUrl(dic)
								})
					this.villageDoctorId.store.load();
				}
				if(this.hospitalDoctorId){
					this.hospitalDoctorId.store.proxy = new util.dictionary.HttpProxy({
									method : "GET",
									url : util.dictionary.SimpleDicFactory.getUrl(dic)
								})
					this.hospitalDoctorId.store.load();
				}
			},
			changeFC_FamilyTeamId : function() {
				var value=this.FC_FamilyDoctorId.value;
				if (!value) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getFamilyTeam",
							method : "execute",
							body : {
								FamilyDoctorId : value
							}
						});
				this.form.getForm().findField("FC_FamilyTeamId").setValue(result.json.familyTeam);
			},
			changevillageDoctorId : function() {
				var value=this.villageDoctorId.value;
				if (!value) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getDoctorinfo",
							method : "execute",
							body : {
								DoctorId : value
							}
						});
				if(result.json && result.json.unit )
					this.form.getForm().findField("villageUnitId").setValue(result.json.unit);
				if(result.json && result.json.phone )
					this.form.getForm().findField("villageDoctorPhone").setValue(result.json.phone.phone);
			},
			changehospitalDoctorId : function() {
				var value=this.hospitalDoctorId.value;
				if (!value) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getDoctorinfo",
							method : "execute",
							body : {
								DoctorId : value
							}
						});
				if(result.json && result.json.unit )
					this.form.getForm().findField("hospitalUnitId").setValue(result.json.unit);
				if(result.json && result.json.phone )
					this.form.getForm().findField("hospitalDoctorPhone").setValue(result.json.phone.phone);
			},
			onStopDateChange : function() {
				var form = this.form.getForm();
				var value = form.findField("FC_Stop_Date").getValue();
				if (value) {
					form.findField("FC_Stop_Reason").enable();
				} else {
					form.findField("FC_Stop_Reason").disable();
				}
			},
			onChangeBeginDate : function() {
				var form = this.form.getForm();
				var beginDate = form.findField("FC_Begin");
				var d = beginDate.getValue();
				if (!d || d == null) {
					d = new Date();
				}
				d.setYear(d.getFullYear() + 1);
				form.findField("FC_End").setValue(d);
				if (this.op == "create") {
					form.findField("FC_Stop_Date").disable();
					form.findField("FC_Stop_Reason").disable();
				} else if (this.op == "update") {
					form.findField("FC_Stop_Date").enable();
					form.findField("FC_Stop_Reason").enable();
				}
			},
			setDefaultValue : function() {
				var familyId = this.familyId;
//				var FC_Repre = this.FC_Repre;
//				FC_Repre.store.removeAll();
//				FC_Repre.store.proxy = new Ext.data.HttpProxy({
//					method : "GET",
//					url : util.dictionary.SimpleDicFactory.getUrl({
//						id : "chis.dictionary.familyMember",
//						filter :"['eq',['$','item.properties.familyId'],['s',"+ familyId + "]]"
//					})
//				})
//				FC_Repre.store.load();
				var FC_Party2 = this.FC_Party2;
				if (this.initDataId) {
					return;
				}
				util.rmi.jsonRequest({
							serviceId : "chis.familyRecordService",
							serviceAction : "queryContractContent",
							method : "execute",
							familyId : familyId
						}, function(code, msg, json) {
							if (json.body == null)
								return;
							this.FC_Party.setValue(json.body.FC_Party);
							this.FC_RepreName.setValue(json.body.FC_RepreName);
//							this.FC_Party2.setValue(json.body.FC_Repre);
							if(this.FC_Phone){
								this.FC_Phone.setValue(json.body.FC_Phone);
							}
							this.FC_Repre=json.body.FC_Repre;
						}, this)
			},
			doNew : function() {
				chis.application.fhr.script.FamilyContractBaseFormView.superclass.doNew.call(this)
				this.onChangeBeginDate();
				this.setDefaultValue()
			},
			getFormData : function() {
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items

				Ext.apply(this.data, this.exContext)

				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id]
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
						}

						if (v == null || v === "") {
							if (!it.pkey && it["not-null"] && !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空")
								return;
							}
						}
						values[it.id] = v;
					}
				}
				if (values["FC_End"] < values["FC_Begin"]) {
					Ext.Msg.alert("提示", "到期日期不能小于签约日期")
					return;
				}
				if (values["FC_Stop_Date"]) {
					if (values["FC_Begin"] > values["FC_Stop_Date"]) {
						Ext.Msg.alert("提示", "解约日期不能小于签约日期")
						return;
					}
				}
				return values;
			}
		});