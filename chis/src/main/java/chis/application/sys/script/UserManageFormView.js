$package("chis.application.conf.script.admin")
$import("chis.script.app.modules.form.TableFormView",
		"util.dictionary.DictionaryLoader",
		"util.widgets.TreeField"
		)

chis.application.conf.script.admin.UserManageFormView = function(cfg) {
	cfg.colCount = 2;
	cfg.width = 550
	cfg.saveServiceId = "chis.userSave"
	cfg.entryName = "SystemUsers"
	chis.application.conf.script.admin.UserManageFormView.superclass.constructor.apply(this, [cfg])
	this.on("winShow",this.onWinShow,this)
	
}

Ext.extend(chis.application.conf.script.admin.UserManageFormView, chis.script.app.modules.form.TableFormView, {
			onReady:function(){
				chis.application.conf.script.admin.UserManageFormView.superclass.onReady.call(this);
				var password = this.form.getForm().findField("password");
				if (password) {
					password.on("change", this.onPasswordChange, this)
				}
			},
			onPasswordChange : function(field) {
				var value = field.getValue();
				if (value.indexOf(" ") > -1) {
					field.reset();
					field.markInvalid("请输入密码,输入的密码中不能有空格");
				}
			},
			doNew : function() {
				chis.application.conf.script.admin.UserManageFormView.superclass.doNew.call(this)
				if(!this.initDataId){
					this.form.getForm().findField("logonName").enable()
				}else{
					this.form.getForm().findField("logonName").disable()
				}
			},
			onWinShow:function(){
				this.win.doLayout();
			}
			,
			loadModuleCfg : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id
						})
				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				return result.json.body;
			}
			,
			doSave:function(){
				chis.application.conf.script.admin.UserManageFormView.superclass.doSave.call(this)
			}
			,
			saveToServer:function(saveData){
				saveData.manaUnitId = this.manaUnitId
				saveData.jobTitle = this.jobTitle
				chis.application.conf.script.admin.UserManageFormView.superclass.saveToServer.call(this,saveData)
			},
			getFormData:function(){
				if (this.saving) {
					return
				}
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
						var v = this.data[it.id] // ** modify by yzh 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							// end
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
					values.manaUnitId = this.manaUnitId
					values.jobTitle = this.jobTitle
					return values;
				}
		})