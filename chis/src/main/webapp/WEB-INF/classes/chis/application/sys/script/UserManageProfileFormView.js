$package("chis.application.conf.script.admin")
$import("chis.script.app.modules.form.TableFormView", "util.dictionary.DictionaryLoader",
		"util.widgets.TreeField", "chis.application.conf.script.admin.UserManaUnitReplaceFormView")

chis.application.conf.script.admin.UserManageProfileFormView = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 400
	cfg.labelWidth = 100
	cfg.fldDefaultWidth = 250
	cfg.autoFieldWidth = false
	chis.application.conf.script.admin.UserManageProfileFormView.superclass.constructor.apply(this,
			[cfg])
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.conf.script.admin.UserManageProfileFormView,
		chis.script.app.modules.form.TableFormView, {
			doAdd : function() {
				if (!this.validate()) {
					return
				}
				var data = {}
				var jobTitleF = this.form.getForm().findField("jobTitle")
				var manaUnitIdF = this.form.getForm().findField("manaUnitId")
				var regionCodeF = this.form.getForm().findField("regionCode")
				var jobTitle = {}
				var manaUnitId = {}
				var regionCode = {}
				jobTitle.key = jobTitleF.getValue()
				jobTitle.text = jobTitleF.getRawValue()
				manaUnitId.key = manaUnitIdF.getValue()
				manaUnitId.text = manaUnitIdF.getRawValue()
				regionCode.key = regionCodeF.getValue()
				regionCode.text = regionCodeF.getRawValue()
				data = {
					jobTitle : jobTitle,
					manaUnitId : manaUnitId,
					regionCode : regionCode
				}
				this.d = data
				if (this.op == "update") {
					if(!this.oldRec){
						this.fireEvent("modify", data)
						return ;
					}
					
					var oldJob = this.oldRec.jobTitle;
					var oldUnit = this.oldRec.manaUnitId;
					if (oldJob != jobTitle.key || oldUnit != manaUnitId.key) {
						this.form.el.mask("正在查询,请稍后...")
						util.rmi.jsonRequest({
									serviceId : "chis.systemUsersService",
									serviceAction : "checkUser",
									body : {
										userId : this.record.data.logonName,
										jobTitle : oldJob
									}
								}, function(code, msg, json) {
									this.form.el.unmask()
									if (code != 200) {
										this.processReturnMsg(code, msg);
										return;
									}
									var body = json.body;
									if (body.userIdUsed == true) {
										replaceFormView = new chis.application.conf.script.admin.UserManaUnitReplaceFormView({
											mainApp :this.mainApp,
											manaUnitId:oldUnit,
											logonName:this.record.data.logonName,
											jobTitle : oldJob
										});
										replaceFormView.form= undefined;
										replaceFormView.win= undefined;
										replaceFormView.on("save",this.onSave,this)
										replaceFormView.initPanel()
										replaceFormView.getWin().show();
									} else{
										this.fireEvent("modify", data)
									}
								}, this)
					}else{
						this.fireEvent("modify", data)
					}
				} else {
					this.fireEvent("add", data)
				}
			},
			onSave:function(entryName, op, json, data){
				this.d.newDoctor = data.newDoctor
				this.d.newManaUnitId = data.newManaUnitId
				this.d.oldJobTitle = data.oldJobTitle
				this.fireEvent("modify", this.d)
			}
			,
			onWinShow : function() {
				if (this.op == "update") {
					var jobTitleF = this.form.getForm().findField("jobTitle")
					var manaUnitIdF = this.form.getForm()
							.findField("manaUnitId")
					var regionCodeF = this.form.getForm()
							.findField("regionCode")
					var jobTitle = {}
					var manaUnitId = {}
					var regionCode = {}
					jobTitle.key = this.record.data.jobTitle
					jobTitle.text = this.record.data.jobTitle_text
					manaUnitId.key = this.record.data.manaUnitId
					manaUnitId.text = this.record.data.manaUnitId_text
					regionCode.key = this.record.data.regionCode
					regionCode.text = this.record.data.regionCode_text
					jobTitleF.setValue(jobTitle)
					// jobTitleF.disable()
					jobTitleF.fireEvent("select", jobTitleF)
					manaUnitIdF.setValue(manaUnitId)
					regionCodeF.setValue(regionCode)
				} else {
					this.roleName = null;
					this.doNew()
					// this.form.getForm().findField("jobTitle").enable()
				}
			},
			onReady : function() {
				var form = this.form.getForm();

				var i = 0
				var jobTitle = form.findField("jobTitle");
				if (jobTitle) {
					jobTitle.on("select", function(field) {
								this.roleName = field.getValue();
								this.form.getForm().findField("manaUnitId")
										.setValue("")
							}, this);
					jobTitle.store.on("load",
							function(store, records, options) {
								var length = records.length
								if (this.mainApp.jobtitleId == 'system') {
									return
								} else if (this.mainApp.jobtitleId == "06") {
									for (var i = 0; i < length; i++) {
										var r = records[i]
										if (r.get("key") == "03"
												|| r.get("key") == "06"
												|| r.get("key") == "09"
												|| r.get("key") == "10"
												|| r.get("key") == "11"
												|| r.get("key") == "12"
												|| r.get("key") == "13"
												|| r.get("key") == "14") {
											store.remove(r)
										}
									}
									store.commitChanges()
								} else {
									for (var i = 0; i < length; i++) {
										var r = records[i]
										if (r.get("key") == "03"
												|| r.get("key") == "09"
												|| r.get("key") == "10"
												|| r.get("key") == "11"
												|| r.get("key") == "12"
												|| r.get("key") == "13"
												|| r.get("key") == "14") {
											store.remove(r)
										}
									}
									store.commitChanges()
								}
							}, this);
					jobTitle.on("change", function(field, newValue, oldValue) {
								var manaUnit = form.findField("manaUnitId");
								manaUnit.reset();
								manaUnit.validate();
							});
				}

				var manaUnitId = form.findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.on("beforeselect", this.filtermanaUnitId, this);
				}
			},
			filtermanaUnitId : function(comb, node) {
				if (!node || !this.roleName)
					return false;

				var keySize = node.attributes["key"].length;
				var mp = node.attributes["isMunicipality"];
				if (this.roleName == "13") {
					if (mp)
						return true;
					else if (keySize <= 4 || keySize == 6)
						return true;
					else
						return false;
				}
				if (this.roleName == "11" || this.roleName == "09") { // 市CDC,市妇保
					if (mp)
						return true;
					else if (keySize <= 4)
						return true;
					else
						return false;
				} else if (this.roleName == "12" || this.roleName == "10") {// ,区CDC,区妇保
					if (keySize == 6)
						return true;
					else
						return false;
				} else if (this.roleName == "04" || this.roleName == "06"
						|| this.roleName == "07" || this.roleName == "08"
						|| this.roleName == "14" || this.roleName == "15"
						|| this.roleName == "16") { // 中心主任,儿保医生,妇保医生,网络管理员,防保科长
					if (keySize == 9)
						return true;
					else
						return false;
				} else if (this.roleName == "05" || this.roleName == "01"
						|| this.roleName == "02" || this.roleName == "03") { // 团队长,责任医生,责任护士,公卫医生
					if (keySize >= 11) {
						return true;
					} else
						return false;
				}
			}
		})