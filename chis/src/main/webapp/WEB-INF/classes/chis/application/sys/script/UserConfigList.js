$package("chis.application.conf.script.admin")

$import("chis.script.BizSimpleListView", "util.dictionary.DictionaryLoader")

chis.application.conf.script.admin.UserConfigList = function(cfg) {
	cfg.createCls = "chis.application.conf.script.admin.UserConfigForm"// "chis.application.conf.script.adminManageModule"
	cfg.updateCls = "chis.application.conf.script.admin.UserConfigForm"
	cfg.title = "用户信息"
	this.userId = "userId"
	this.serviceId = "chis.userService"
	cfg.listServiceId = "chis.systemUserService"
	Ext.apply(this, app.modules.common)
	chis.application.conf.script.admin.UserConfigList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.admin.UserConfigList, chis.script.BizSimpleListView, {
			onReady : function() {
				this.requestData.serviceAction = "queryUsers"
				chis.application.conf.script.admin.UserConfigList.superclass.onReady.call(this)
			},

			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable) {
						continue
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return [];
				}
				fields.push({
							value : "post",
							text : "角色"
						})
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
							width : 100
						});
				combox.on("select", this.onCndFieldSelect, this)
				this.cndFldCombox = combox
				var cndField = new Ext.form.TextField({
							width : 150,
							selectOnFocus : true,
							name : "dftcndfld"
						})
				this.cndField = cndField
				var queryBtn = new Ext.Toolbar.SplitButton({
							text : '',
							iconCls : "query",
							prop : {},
							menu : new Ext.menu.Menu({
										items : {
											text : "高级查询",
											iconCls : "common_query",
											handler : this.doAdvancedQuery,
											scope : this
										}
									})
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			},

			onCndFieldSelect : function(item, record, e) {
				var tbar = this.grid.getTopToolbar()
				var tbarItems = tbar.items.items
				var itid = record.data.value
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (itid == "post") {
					it = item;
					it.id="post";
					it.refAlias = "b";
					it.type = "string";
					it.dic = {
						id : "positionDic",
						render : "Tree",
						onlySelectLeaf : "true",
						parentKey : "%user.manageUnit.id",
						filter : "['ne',['$map',['s','rid']],['s','system']]"
					};
					this.jobItem=it;
				}
				var field = this.cndField
				// field.destroy()
				field.hide();
				var f = this.midiComponents[it.id]
				if (!f) {
					if (it.dic) {
						it.dic.src = this.entryName + "." + it.id
						it.dic.defaultValue = it.defaultValue
						it.dic.width = 150
						f = this.createDicField(it.dic)
					} else {
						f = this.createNormalField(it)
					}
					f.on("specialkey", this.onQueryFieldEnter, this)
					this.midiComponents[it.id] = f
				} else {
					// f.on("specialkey", this.onQueryFieldEnter, this)
					// f.rendered = false
					f.show();
				}
				this.cndField = f
				tbarItems[2] = f
				tbar.doLayout()
			},

			doCndQuery : function(button, e, addNavCnd) {
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if(itid=="post"){
					it=this.jobItem;
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (it.xtype == "datefield") {
								v = v.format("Y-m-d")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd')"]
								cnd.push(['s', v])
							} else {
								v = v.format("Y-m-d H:i:s")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd HH:mm:ss')"]
								cnd.push(['s', v])
							}

							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			},

			getStoreFields : function(items) {
				var fields = []
				var ac = util.Accredit;
				var pkey = "";
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					var f = {}
					f.name = it.id
					switch (it.type) {
						case 'date' :
							break;
						case 'int' :
							f.type = "int"
							break
						case 'double' :
						case 'bigDecimal' :
							f.type = "float"
							break
						case 'string' :
							f.type = "string"
					}
					fields.push(f)
					if (it.dic) {
						fields.push({
									name : it.id + "_text",
									type : "string"
								})
					}
				}
				return {
					fields : fields
				}
			},

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemUserService",
							serviceAction : "checkUser",
							body : {
								userId : r.get("userId")
							}
						})
				if (result.json.body.userIdUsed == true) {
					Ext.Msg.alert("消息", "该角色正在被使用，当前操作无法执行");
					return
				}
				Ext.Msg.show({
							title : '确认删除用户[' + r.data[this.userId] + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},

			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("正在删除数据...")
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							body : r.data,
							cmd : "remove"
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			}

		})
