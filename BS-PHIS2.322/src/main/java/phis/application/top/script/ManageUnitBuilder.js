$package("com.bsoft.phis.pub")

$import("app.modules.config.unit.manageUnitBuilder")

com.bsoft.phis.pub.ManageUnitBuilder = function(cfg) {
	this.codeRule = "4,2,3,3";
	com.bsoft.phis.pub.ManageUnitBuilder.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(com.bsoft.phis.pub.ManageUnitBuilder,
		app.modules.config.unit.manageUnitBuilder, {
			initPanel : function() {
				var tree = util.dictionary.TreeDicFactory.createTree({
							id : this.dicId,
							parentKey : this.navParentKey || {},
							rootVisible : this.rootVisible || false
						})
				tree.autoScroll = true
				tree.on("click", this.onTreeClick, this)
				tree.on("load", this.onTreeLoad, this);
				this.tree = tree;
				var form = this.createForm()
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : [{
										text : '增加子项',
										disabled : true,
										iconCls : 'treeAppend',
										handler : this.doAppendItem,
										scope : this
									}, {
										text : '保存项',
										disabled : true,
										iconCls : 'save',
										handler : this.doSaveItem,
										scope : this
									}, {
										text : '删除项',
										disabled : true,
										iconCls : 'treeRemove',
										handler : this.doRemoveItem,
										scope : this
									}],
							items : [{
										layout : "fit",
										split : true,
										region : 'west',
										bodyStyle : 'padding:5px 0',
										width : 200,
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
				this.panel = panel;
				this.tree.on("contextmenu", this.onContextMenu, this);
				return panel
			},
			onTreeClick : function(node, e) {
				this.selectedNode = node
				this.form.getForm().reset()
				var fields = this.getFields()
				if (node.attributes.type == 'd') {
					if (node.childNodes.length == 0) {
						this.resetButtons()
						var buttons = this.panel.getTopToolbar().items
						buttons.get(0).disable()
						// buttons.get(1).disable()
					}
				} else {
					this.resetButtons();
				}
				if (node.parentNode) {
					this.selectedParentNode = node.parentNode;
					if (node.parentNode.text) {
						fields.parentKey.setValue(node.parentNode.id)
						fields.parentText.setValue(node.parentNode.text)
					}
				}

				var data = {}
				data['key'] = node.id
				data['dicId'] = this.dicId // "manageUnitDic"

				var cmd = "loadData"
				var result = this.saveToServer(cmd, data)
				if (result.code == 200) {
					var body = result.json.body
					for (var id in body) {
						if (id == "key") {
							fields.unitId.setValue(body[id])
							fields.unitId.disable()
							continue
						}
						if (id == "text") {
							fields.organizName.setValue(body[id])
							continue
						}
						if (fields[id]) {
							fields[id].setValue(body[id])
							if (id == "type") {
								fields[id].setDisabled(true);
							}
							if (id == "certificateType") {
								fields[id].setValue({
											key : body[id],
											text : body[id]
										})
							}
						}

					}
				}
				node.expand()
			},
			doInsertItem : function() {
				this.resetButtons()
				var fields = this.getFields();
				var buttons = this.panel.getTopToolbar().items
				buttons.get(0).disable()
				buttons.get(2).disable()
				// buttons.get(3).disable()

				var parent = null;

				fields.unitId.setValue("")
				fields.organizName.setValue("")
				fields.pyCode.setValue("")
				fields.type.setValue({
							key : "a",
							text : "市"
						})
				fields.type.setDisabled(true);
				fields.unitId.enable()
				this.saveItemOp = "insert"

			},
			doAppendItem : function() {
				this.selectedParentNode = this.selectedNode
				this.resetButtons()
				var fields = this.getFields();
				var buttons = this.panel.getTopToolbar().items
				buttons.get(0).disable()
				buttons.get(2).disable()

				var pKey = fields.unitId.getValue()
				var pText = fields.organizName.getValue()
				fields.parentKey.setValue(pKey)
				fields.parentText.setValue(pText)

				// fields.unitId.setValue("")
				var maxKey = this.getMaxKey(this.selectedParentNode, "append",
						this.codeRule);
				fields.unitId.setValue(maxKey)
				fields.organizName.setValue("")
				fields.pyCode.setValue("");
				var type = fields.type.getValue();
				var type_text = "";
				switch (type) {
					case "a" :
						type = "b", type_text = "区"
						break;
					case "b" :
						type = "c", type_text = "医院";
						break;
					case "c" :
						type = "d", type_text = "村站";
						break;
				}
				fields.type.setValue({
							key : type,
							text : type_text
						})
				fields.unitId.enable()
				this.saveItemOp = "append"
			},
			onTreeLoad : function(node) {
				if (node) {
					if (!this.select) {
						node.select();
						this.select = true;
						if (!isNaN(node.id)) {
							this.onTreeClick(node, this);
						}
					}
					if (node.getDepth() == 0) {
						if (node.hasChildNodes()) {
							node.firstChild.expand();
						}
					}
				} else {
					this.doInsertItem();
				}
			},
			createForm : function() {
				var combox = util.dictionary.SimpleDicFactory.createDic({
							id : 'unitType',
							width : 250,
							editable : false,
							defaultValue : 'area'
						})
				combox.name = 'type'
				combox.fieldLabel = '机构类型'
				combox.allowBlank = false
				this.manageType = combox
				var certificateCombox = util.dictionary.SimpleDicFactory
						.createDic({
									id : 'certificateType',
									width : 250,
									editable : false
								})
				certificateCombox.name = 'certificateType'
				// certificateCombox.fieldLabel = '证书类型'
				certificateCombox.allowBlank = true
				// this.manageType = certificateCombox
				var form = new Ext.FormPanel({
							labelWidth : 80,
							frame : true,
							bodyStyle : 'padding:5px 20px 20px 5px',
							// autoScroll:true,
							labelAlign : 'top',
							defaultType : 'textfield',
							items : [{
										width : '98%',
										fieldLabel : '上级机构编号',
										name : 'parentKey',
										disabled : true
									}, {
										width : '98%',
										fieldLabel : '上级机构名称',
										name : 'parentText',
										disabled : true
									}, {
										width : '98%',
										fieldLabel : '本机构编号',
										allowBlank : false,
										name : 'unitId',
										enableKeyEvents : true,
										listeners : {
											keyup : this.onKeyup,
											blur : function(e) {
												if (typeof(e.getValue()) == 'string') {
													e.setValue(e.getValue()
															.trim())
												}
											},
											scope : this
										}
									}, {
										width : '98%',
										fieldLabel : '本机构名称',
										name : 'organizName', // 'deptName',
										listeners : {
											blur : function(e) {
												if (typeof(e.getValue()) == 'string') {
													e.setValue(e.getValue()
															.trim())
												}
											},
											scope : this
										},
										allowBlank : false
									}, {
										width : '98%',
										fieldLabel : '机构拼音码',
										name : 'pyCode',
										disabled : true
									}, {
										xtype : 'panel',
										layout : "table",
										layoutConfig : {
											columns : 2
										},
										items : [{
													xtype : "panel",
													bodyStyle : "padding:4px 0px",
													width : '98%',
													html : "机构类型:&nbsp;",
													colspan : 2
												}, combox
										// ,
										// new Ext.Button({
										// iconCls : 'update',
										// text : '机构类型维护',
										// handler : this.doUpdateType,
										// scope : this
										// })
										]
									}
									//医保机构代码
//									, {
//										width : '98%',
//										fieldLabel : '医保编码',
//										allowBlank : true,
//										name : 'medicareId'
//									}, {
//										xtype : 'panel',
//										layout : "table",
//										layoutConfig : {
//											columns : 2
//										},
//										items : [{
//													xtype : "panel",
//													bodyStyle : "padding:4px 0px",
//													width : '98%',
//													html : "证书类型:&nbsp;",
//													colspan : 2
//												}, certificateCombox]
//									}
									]
						});
				this.form = form
				return form
			},
			onKeyup : function(f, e) {
				if (!this.isNumber(f.getValue())) {
					if (this.saveItemOp == "insert") {
						f.setValue(this.getMaxKey(this.selectedNode.parentNode,
								this.saveItemOp, this.codeRule))
					} else {
						f.setValue(this.getMaxKey(this.selectedNode,
								this.saveItemOp, this.codeRule))
					}
				} else {
					var v = f.getValue()
					if (this.saveItemOp == "insert") {
						var key = this.selectedNode.parentNode.attributes.key
						if (v.substring(0, key.length) != key) {
							f.setValue(this.getMaxKey(
									this.selectedNode.parentNode,
									this.saveItemOp, this.codeRule))
						}
					} else {
						var key = this.selectedNode.attributes.key
						if (v.substring(0, key.length) != key) {
							f.setValue(this.getMaxKey(this.selectedNode,
									this.saveItemOp, this.codeRule))
						}
					}
				}
			},

			isNumber : function(s) {
				var patrn = /^[0-9]*[1-9][0-9]*$/
				if (!patrn.test(s)) {
					return false;
				}
				return true
			},

			doSaveItem : function() {
				var fields = this.getFields();
				var pnode = this.selectedParentNode
				if (pnode.attributes.type == 'dic') {
					fields.parentKey.setValue("")
				}

				if (!fields.unitId.isValid()) {
					return
				}
				var data = {}
				var cmd = "updateItem"

				for (var id in fields) {
					var f = fields[id]
					if (id == "unitId") {
						data['key'] = f.getValue()
						if (f.getValue().trim() == 0) {
							Ext.Msg.alert("提示", "本机构编号不能为空");
							return;
						}
						continue
					}
					if (id == "organizName") {
						data['text'] = f.getValue()
						if (f.getValue().trim() == 0) {
							Ext.Msg.alert("提示", "本机构名称不能为空");
							return;
						}
						continue
					}
					data[id] = f.getValue()
				}
				data['dicId'] = this.dicId // "manageUnitDic"

				if (this.saveItemOp == "insert" || this.saveItemOp == "append") {
					cmd = "createItem"
				}

				var result = this.saveToServer(cmd, data)
				if (result.msg == "Created") {
					var parent = null
					if (this.saveItemOp == "insert") {
						parent = this.selectedNode.parentNode
					} else {
						parent = this.selectedNode
					}
					parent.leaf = false
					var node = parent.appendChild({
								id : data.key,
								text : data.text,
								leaf : true
							})
					for (var id in data) {
						if (id != "key" || id != "text") {
							node.attributes[id] = data[id]
						}
					}
					parent.expand()
					node.select()
					node.ensureVisible()
					this.onTreeClick(node)
					this.saveItemOp = null;
				} else {
					if (result.code == 200) {
						var node = this.selectedNode
						if (node) {
							node.setText(data.text)
							for (var id in node.attributes) {
								if (id == "key" || id == "text"
										|| id == "parent" || id == "leaf"
										|| id == "loader" || id == "id"
										|| id == "folder") {
									continue
								}
								delete node.attributes[id]
							}
							for (var id in data) {
								if (id != "key" || id != "text") {
									node.attributes[id] = data[id]
								}
							}
						}
					} else {
						if (result.code == 406) {
							alert("字典项目编码重复,新增失败...")
						} else {
							alert(result.msg)
						}
					}
				}
			}
		})
