$package("chis.application.conf.script.admin")

$import("util.dictionary.DictionaryBuilder","util.dictionary.DictionaryLoader")

chis.application.conf.script.admin.ManageUnitBuilder = function(cfg) {
//	this.codeRule = cfg.codeRule || "";
	this.dicAlias = cfg.dicAlias || "";
	this.dicId = cfg.dicId || "";
	chis.application.conf.script.admin.ManageUnitBuilder.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.admin.ManageUnitBuilder, util.dictionary.DictionaryBuilder,
		{
			initPanel : function() {
				if (!this.codeRule) {
					var manageUnitDic = util.dictionary.DictionaryLoader.load({
								id : 'manageUnit'
							})
					this.codeRule = manageUnitDic.properties.manageRule;
				}
				var tree = util.dictionary.TreeDicFactory.createTree({
							id : this.dicId,
							keyNotUniquely : true,
							parentKey : this.navParentKey || {},
							rootVisible : this.rootVisible || false
						})
				var form = this.createForm()
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : [{
										name : "addDic",
										text : '新建字典',
										iconCls : "add",
										handler : this.doNewDic,
										scope : this,
										hidden : true
									}, {
										name : "saveDic",
										text : '保存字典',
										disabled : true,
										iconCls : "save",
										handler : this.doSaveDic,
										scope : this,
										hidden : true
									}, {
										name : "removeDic",
										text : '删除字典',
										disabled : true,
										iconCls : "remove",
										handler : this.doRemoveDic,
										scope : this,
										hidden : true
									}, {
										text : '-',
										hidden : true
									}, {
										name : "addItem",
										text : '增加项',
										disabled : true,
										iconCls : 'treeInsert',
										handler : this.doInsertItem,
										scope : this
									}, {
										name : "addSubItem",
										text : '增加子项',
										disabled : true,
										iconCls : 'treeAppend',
										handler : this.doAppendItem,
										scope : this
									}, {
										name : "saveItem",
										text : '保存项',
										disabled : true,
										iconCls : 'save',
										handler : this.doSaveItem,
										scope : this
									}, {
										name : "removeItem",
										text : '删除项',
										disabled : true,
										iconCls : 'treeRemove',
										handler : this.doRemoveItem,
										scope : this
									}],
							items : [{
										layout : "fit",
										split : true,
										collapsible : true,
										title : '',
										region : 'west',
										width : 210,
										items : tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : form
									}]
						});
						//system同步按钮
				if(this.mainApp.uid=='system'){//add by lis 2010-10-12
					panel.getTopToolbar().addButton({
						name : 'sys',
						text : '同步',
						iconCls : 'connect',
						handler : this.doSys,
						scope : this,
						hidden : true // add by lyl 2011-2-22 reason: java program error
					})
				}
				tree.on("click", this.onTreeClick, this)
				tree.expand()
				this.tree = tree;
				this.panel = panel;
				return panel
			},
			createForm : function() {
				var propGrid = this.createPropGrid()
				var form = new Ext.FormPanel({
							labelWidth : 75,
							frame : true,
							bodyStyle : 'padding:2px 2px 0',
							width : 580,
							defaults : {
								width : '98%'
							},
							autoScroll : true,
							labelAlign : 'top',
							defaultType : 'textfield',
							items : [{
										fieldLabel : '父节点编码',
										name : 'parentKey',
										disabled : true
									}, {
										fieldLabel : '父节点名称',
										name : 'parentText',
										disabled : true
									}, {
										fieldLabel : '节点编码',
										name : 'key',
										allowBlank : false
									}, {
										fieldLabel : '节点名称',
										name : 'text',
										allowBlank : false
									}, propGrid, {
										fieldLabel : '字典编码',
										allowBlank : false,
										name : 'dicId',
										disabled : true,
										hidden : true,
										hideLabel : true
									}, {
										fieldLabel : '字典名称',
										name : 'dicAlias',
										disabled : true,
										allowBlank : false,
										hidden : true,
										hideLabel : true
									}, {
										fieldLabel : '编码规则',
										name : 'codeRule',
										disabled : true,
										hidden : true,
										hideLabel : true
									}]
						});
				this.form = form
				return form;
			},
			onTreeClick : function(node, e) {
				this.selectedNode = node
//				var n = node
//				while (n) {
//					if (n.attributes.type) {
//						break
//					}
//					n = n.parentNode
//				}
//				this.resetButtons()
				this.form.getForm().reset()
				this.disableAllFields()
				var toolbar = this.panel.getTopToolbar()
				var fields = this.getFields()
				fields.dicId.setValue(this.dicId)
				fields.dicAlias.setValue(this.dicAlias)
				fields.codeRule.setValue(this.codeRule)
				this.propGrid.getStore().removeAll()
//				if (node == n) {
//					fields.dicId.disable()
//					fields.dicAlias.enable()
//					// buttons.get(1).enable()
//					// buttons.get(2).enable()
//					toolbar.find("name", "addItem")[0].disable()
//					toolbar.find("name", "addSubItem")[0].enable()
//					toolbar.find("name", "saveItem")[0].disable()
//					toolbar.find("name", "removeItem")[0].disable()
//				} else
				if (node.attributes.key.length >= 11) {
					toolbar.find("name", "addItem")[0].enable()
					toolbar.find("name", "addSubItem")[0].disable()
					toolbar.find("name", "saveItem")[0].enable()
					toolbar.find("name", "removeItem")[0].enable()
				} else if (node.attributes.key.length == 6) {
					toolbar.find("name", "addItem")[0].disable()
					toolbar.find("name", "addSubItem")[0].enable()
					toolbar.find("name", "saveItem")[0].disable()
					toolbar.find("name", "removeItem")[0].disable()
				} else if (node.attributes.key.length < 6) {
					toolbar.find("name", "addItem")[0].disable()
					toolbar.find("name", "addSubItem")[0].disable()
					toolbar.find("name", "saveItem")[0].disable()
					toolbar.find("name", "removeItem")[0].disable()
				} else {
					// buttons.get(1).disable()
					// buttons.get(2).disable()
					toolbar.find("name", "addItem")[0].enable()
					toolbar.find("name", "addSubItem")[0].enable()
					toolbar.find("name", "saveItem")[0].enable()
					toolbar.find("name", "removeItem")[0].enable()
				}

				fields.key.setValue(node.attributes.key)
				fields.text.setValue(node.attributes.text)
				fields.text.enable()
				var parentNode = node.parentNode
				if (parentNode) {
					fields.parentKey.setValue(node.parentNode.attributes.key)
					fields.parentText.setValue(node.parentNode.text)
				}
				this.propGrid.show()
				var attributes = this.selectedNode.attributes
				for (var id in attributes) {
					if (id == "key" || id == "text" || id == "parent"
							|| id == "leaf" || id == "loader" || id == "id"
							|| id == "folder" || id == "root"
							|| id == "expandable" || id == "type"
							|| id == "nav") {
						continue
					}
					var _id = id
					if (id == "pyCode") {
						_id = "拼音码"
					}
					if (id == "mCode") {
						_id = "速记码"
					}
					this.addProp(_id, attributes[id])
				}
				node.expand()
			},
			processItemRemove : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.simpleQuery",
							schema : "SystemUsersProfile",
							cnd : ["eq", ["$", "manaUnitId"],
									["s", this.selectedNode.attributes.key]]
						})

				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				if (result.json.totalCount > 0) {
					Ext.MessageBox.alert("信息提示", "该管理单元下尚有用户，无法删除")
					return
				}

				chis.application.conf.script.admin.ManageUnitBuilder.superclass.processItemRemove
						.call(this)
			},
			doSys : function() {
				this.panel.el.mask("正在同步，请稍后……")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemUserService",
							serviceAction : "insertIntoManaUnit"
						})
				this.panel.el.unmask()
				if(result.code==200)
					Ext.MessageBox.alert("信息提示", "同步完成！")
			}
		})