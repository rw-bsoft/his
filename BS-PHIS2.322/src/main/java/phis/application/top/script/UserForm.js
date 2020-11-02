$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.TableForm")

com.bsoft.phis.pub.UserForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.colCount = 2;
	com.bsoft.phis.pub.UserForm.superclass.constructor.apply(this, [cfg])
	this.loginname = "loginName";
	this.FEntryName = "SYS_Personnel_YH";
	this.YGBH = "YGBH";
	this.serviceId = "userManageService";
	this.actionId = "queryStaff";

}

Ext.extend(com.bsoft.phis.pub.UserForm, com.bsoft.phis.TableForm, {
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				this.initDataId = null;
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var f = form.findField(this.loginname)

				if (f) {

					if (!f.hasListener("lookup")) {
						f.on("lookup", this.doLookup, this)
						f.un("specialkey", this.onFieldSpecialkey, this)
						f.on("specialkey", function(f, e) {
									var key = e.getKey()
									if (key == e.ENTER) {
										this.doENTER(f)
									}
								}, this)
					}
					f.enable()
				}
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						// @@ 2010-01-07 modified by chinnsii, changed the
						// condition
						// "it.update" to "!=false"
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}
						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
					}
				}
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				this.focusFieldAfter(-1, 800)
				this.validate()
			},
			doLookup : function(field) {
				var v = field.getValue()
				this.loadPersonList(v)
			},
			doENTER : function(field) {
				var v = field.getValue();
				this.form.el.mask("正在查询...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.actionId,
							body : v
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (json.body.size != 1) {
								this.loadPersonList(v)
							} else {
								this.Backfill(json.body.data);
							}
						}, this);
			},
			loadPersonList : function(v) {
				var cnd = null
				//var ygbhsstr = "'";
//				for(var i = 0 ; i <this.ygbhs.length ; i++){
//					if(i == 0){
//						ygbhsstr += this.ygbhs[i];
//					}else{
//						ygbhsstr += "','"+this.ygbhs[i];
//					}
//				}
//				ygbhsstr += "'";
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "userManageService",
							serviceAction : "getYgbh"
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				var ygbhs  = result.json.ygbh;
				var ygbhsstr = "'";
				for(var i = 0 ; i <ygbhs.length ; i++){
					if(i == 0){
						ygbhsstr += ygbhs[i];
					}else{
						ygbhsstr += "','"+ygbhs[i];
					}
				}
				ygbhsstr += "'";
				this.ygbhsstr = ygbhsstr;
				if(ygbhs.length>0){
					if (v) {
						cnd = ['and',['like', ['$', this.YGBH], ['s', '%' + v + '%']],['not in',['$','YGBH'],[ygbhsstr]]];
					}else{
						cnd = ['not in',['$','YGBH'],[ygbhsstr]];
					}
				}else{
					if (v) {
						cnd = ['like', ['$', this.YGBH], ['s', '%' + v + '%']];
					}
				}
				var cmd = "gridlist";
				var cfg = {
					title : "员工信息",
					entryName :this.FEntryName, //"SYS_Personnel",
					autoLoadSchema : false,
					autoLoadData : true,
					showButtonOnTop : true,
					createCls : "com.bsoft.phis.pub.DepartmentStallForm",
					updateCls : "com.bsoft.phis.pub.DepartmentStallForm",
					initCnd : cnd,
					actions : [
						{
								id : "save",
								name : "确定"
							},
							{
								id : "canceled",
								name : "取消",
								iconCls : "common_cancel"
							}]
				}
				var cls = "com.bsoft.phis.pub.PersonQuery"
				var m = this.midiModules[cmd]
				if (!m) {
					$require(cls, [function() {
										var module = eval("new " + cls
												+ "(cfg)")
										module.opener = this
										module.setMainApp(this.mainApp)
										module.on("Backfill", this.Backfill,
												this)
										module.requestData.cnd = cnd;
										this.midiModules[cmd] = module;
										var lp = module.initPanel()
										this.openWin(cmd, 100, 50)
									}, this])
				} else {
					m.requestData.cnd = cnd
					m.refresh()
					this.openWin(cmd, 100, 50)
				}
			},
			openWin : function(cmd, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					if (xy) {
						win.setPosition(xy[0], xy[1])
					}
					win.setTitle(module.title)
					win.show()
					var form = this.form.getForm();
					var YGBH = form.findField(this.loginname);
				}
			},
			Backfill : function(data, win) {
				this.form.el.mask("正在查询...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "userAuthentication",
							body : data["YGBH"]
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var form = this.form.getForm();
							var YGBH = form.findField("loginName");
							YGBH.setValue(data["YGBH"]);
							YGBH.disable();
							var YGXM = form.findField("userName");
							YGXM.setValue(data["YGXM"]);
							YGXM.disable();
							var YGXB = form.findField("gender");
							YGXB.setValue(data["YGXB"]);
							YGXB.disable();
							this.initDataId = data["YGDM"];
							this.data["pyCode"] = data["PYDM"];
							//焦点定位到密码输入框
							var password = form.findField("password");
							if (win) {
								win.hide()
							}
						}, this);

			}
		})