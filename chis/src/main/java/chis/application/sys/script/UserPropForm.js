$package("chis.application.conf.script.admin")

$import("chis.script.app.modules.form.SimpleFormView", "chis.script.BizTableFormView")

chis.application.conf.script.admin.UserPropForm = function(cfg) {
	cfg.width = 500;
	cfg.fldDefaultWidth = 300
	this.serviceId = "chis.userService"
	this.roleId = "jobId"
	this.unitId = "manaUnitId"
	this.post = "post"
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	cfg.colCount = 1
	chis.application.conf.script.admin.UserPropForm.superclass.constructor.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this)

}
Ext.extend(chis.application.conf.script.admin.UserPropForm, chis.script.BizTableFormView, {

			doNew : function(userId) {
				chis.application.conf.script.admin.UserPropForm.superclass.doNew.call(this)
				if (userId) {
					this.userId = userId
					this.data["userId"] = userId
				}
				var form = this.form.getForm()
				var fpost = form.findField(this.post) // 所属部门
				if (fpost) {
					fpost.tree.on("click", this.addValue, this)
					fpost.setValue({
								key : "",
								text : ""
							})
				}

			},
			addValue : function(node) {
				var parentNode = node.parentNode
				var form = this.form.getForm()
				var funit = form.findField(this.unitId)
				this.controlManageUnit(node.attributes.rid)
				funit.setValue({
							key : parentNode.id,
							text : parentNode.text
						})
				this.data[this.roleId] = node.attributes.rid
			},
			controlManageUnit : function(v) {
				var form = this.form.getForm()
				var funit = form.findField(this.unitId)
				if (v == '13') {
					funit.enable()
				} else {
					funit.disable()
				}
			},
			loadData : function() {
				var r = this.exContext[this.entryName]
				var data = {}
				Ext.apply(data, r.data)
				if (data) {
					var items = this.schema.items
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var id = it.id
						if (it.dic) {
							data[id] = {
								key : data[id],
								text : data[id + "_text"]
							}
						}
					}
				}
				this.doNew()
				this.initFormData(data)
				if (this.op == "create") {
					this.op = "update"
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemUserService",
							serviceAction : "checkUser",
							body : {
								userId : r.get("userId"),
								jobId : r.get("jobId"),
								manaUnitId : r.get("manaUnitId")
							}
						})
				if (result.json.body.userIdUsed == true) {
					this.form.getForm().findField("post").disable()
				} else {
					this.form.getForm().findField("post").enable()
				}
				this.controlManageUnit(this.form.getForm().findField(this.post)
						.getValue().substring(0, 2))
			},

			doSave : function() {
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
						var v = this.data[it.id]
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							if (it.dic) {
								values[it.id + "_text"] = v.text
							}
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							if (it.dic) {
								values[it.id + "_text"] = f.getRawValue()
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
						}
						values[it.id] = v;
					}
				}
				Ext.apply(this.data, values);

				this.saveToGrid(values)
			},

			saveToGrid : function(values) {
				var items = this.schema.items
				var n = items.length
				var fields = []
				for (var i = 0; i < n; i++) {
					var it = items[i]
					fields[i] = {
						name : it.id
					}
				}
				var record = Ext.data.Record.create(fields)
				var store = this.opener.grid.getStore()
				var post = values[this.post]
				if (!post) {
					return
				}

				var rr = new record(values)
				var fJobId = values.jobId
				var fManaUnitId = values.manaUnitId
				var initDataId = this.initDataId
				// modify by yuhua
				for (var i = 0; i < this.opener.grid.getStore().getCount(); i++) {
					var r = this.opener.grid.getStore().getAt(i);
					var lJobId = r.get("jobId");
					var lManaUnitId = r.get("manaUnitId");
					var id = r.get("id")
					var canModify = false;
					if (this.op == "create") {
						if (fJobId == lJobId) {
							if (fManaUnitId == lManaUnitId) {
								Ext.Msg.alert("错误", "相关职位已存在,无法保存");
								return
							}
						} else {
							if ((fJobId == "01" && lJobId == "05")
									|| (fJobId == "05" && lJobId == "01")) {
								if (fManaUnitId == lManaUnitId) {
									Ext.Msg.alert("错误",
											"同一用户同一团队中不能同时为团队长和责任医生!");
									return
								}
							}
						}
					} else {
						if (fJobId == lJobId) {
							if (this.initDataId != id) {
								if (fManaUnitId == lManaUnitId) {
									Ext.Msg.alert("错误", "相关职位已存在,无法保存");
									return
								}
							}
						} else {
							if ((fJobId == "01" && lJobId == "05")
									|| (fJobId == "05" && lJobId == "01")) {
								if (this.initDataId != id) {
									if (fManaUnitId == lManaUnitId) {
										Ext.Msg.alert("错误",
												"同一用户同一团队中不能同时为团队长和责任医生!");
										return
									}
								} else {
									canModify = true
								}
							}
						}
					}
				}
				if (this.op != "create" && canModify) {
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.systemUserService",
								serviceAction : "checkUser",
								body : {
									userId : values.userId,
									jobId : fJobId,
									manaUnitId:fManaUnitId
								}
							})
					if (result.json.body.userIdUsed == true) {
						Ext.Msg.alert("错误", "该角色正在被使用，当前操作无法执行");
						return
					}
				}

				if (this.op == "create") {
					if (this.findById(post) == -1) {
						store.add(rr)
						this.op = "update"
						this.doCancel();
					} else {
						Ext.Msg.alert("错误", "相关职位已存在,无法保存");
					}
				} else {
					var r = this.exContext[this.entryName]
					var n = store.indexOf(r)
					if (this.findById(post) != -1 && this.findById(post) != n) {
						Ext.Msg.alert("错误", "相关职位已存在,无法保存");
						return
					}
					store.remove(r)
					store.insert(n, rr)
					this.doCancel();
				}

				this.exContext[this.entryName] = rr
				this.win.hide()
			},
			findById : function(v) {
				var store = this.opener.grid.getStore()
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var rr = store.getAt(i)
					if (rr.get(this.post) == v) {
						return i
					}
				}
				return -1
			}
		})