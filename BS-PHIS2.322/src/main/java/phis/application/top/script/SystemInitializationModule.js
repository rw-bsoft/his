$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.SimpleModule", "phis.script.rmi.miniJsonRequestSync",
		"util.dictionary.TreeDicFactory", "util.dictionary.SimpleDicFactory")

com.bsoft.phis.pub.SystemInitializationModule = function(cfg) {
	cfg.width = 400;
	cfg.height = 300;
	com.bsoft.phis.pub.SystemInitializationModule.superclass.constructor.apply(
			this, [cfg])

}
Ext.extend(com.bsoft.phis.pub.SystemInitializationModule,
		com.bsoft.phis.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					this.tree.getLoader().load(this.tree.getRootNode());
					// this.afterLoad();
					return this.panel;
				}
				var root = new Ext.tree.AsyncTreeNode({
							text : "",
							type : 'dic',
							key : "",
							expandable : true,
							root : true
						})
				loader = new Ext.tree.TreeLoader({
							dataUrl : ClassLoader.appRootOffsetPath
									+ 'SystemInitialization.search'
						});
				loader.on("beforeload", function(treeLoader, node) {
							loader.baseParams.node_id = node.id;
							loader.baseParams.parent_id = node.getPath();
						}, this);

				var tree = new Ext.tree.TreePanel({
							// renderTo : 'tree-div',
							height : 300,
							width : 400,
							header : false,
							loader : loader,
							rootVisible : false,
							useArrows : true,
							autoScroll : true,
							animate : true,
							containerScroll : true,
							rootVisible : false,
							frame : true,
							root : root
						})
				this.tree = tree;
				this.tree.on("load", this.onTreeLoad, this);
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : 300,
							buttons : [{
										text : '初始化',
										handler : this.doCommit,
										iconCls : 'default',
										scope : this
									}, {
										text : '取消',
										iconCls : 'common_cancel',
										handler : this.doCancel,
										scope : this
									}],
							items : [{
										layout : "fit",
										split : true,
										region : 'center',
										bodyStyle : 'padding:5px 0',
										items : tree
									}]
						});

				this.panel = panel;
				return panel
			},
			onTreeLoad : function() {
				this.tree.expandAll();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : true
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				this.winModule = win;
				return win;
			},
			doCancel : function() {
				this.win.hide();
			},
			doCommit : function() {
				var datas = [];
				var node = this.tree.getRootNode();
				node.eachChild(function(node1) {
					if (2 == node1.id || 3 == node1.id) {
						node1.eachChild(function(node2) {
									if (node2.disabled == false
											&& node2.getUI().isChecked() == true) {
										var data = {}
										data.GROUPID = node1.id
										data.OFFICEID = node2.id;
										datas.push(data);
									}
								});
					} else {
						if (node1.disabled == false
								&& node1.getUI().isChecked() == true) {
							var data = {}
							data.GROUPID = node1.id
							data.OFFICEID = 0
							datas.push(data);
						}
					}
				});
				this.datas = datas;
				if (datas.length == 0) {
					Ext.MessageBox.alert('提示', '请选择后再初始化!');
					return;
				}

				/** *********************************************************** */
				if (!this.form) {
					this.form = new Ext.FormPanel({
								frame : true,
								labelWidth : 75,
								labelAlign : 'top',
								defaults : {
									width : '95%'
								},
								defaultType : 'textfield',
								shadow : true,
								items : [{
											fieldLabel : '请输入密码',
											name : 'psw',
											inputType : 'password'
										}]
							})
				} else {
					var form = this.form.getForm()
					this.Field = form.findField("psw");
					this.Field.setValue();
				}
				// this.Field.setValue();
				if (!this.chiswin) {
					var win = new Ext.Window({
								layout : "form",
								title : '请输入...',
								width : 300,
								height : 130,
								resizable : true,
								modal : true,
								iconCls : 'x-logon-win',
								constrainHeader : true,
								shim : true,
								// items:this.form,
								buttonAlign : 'center',
								closable : false,
								buttons : [{
											text : '确定',
											handler : this.systemInit,
											scope : this
										}, {
											text : '取消',
											handler : this.dochiswinhide,
											scope : this
										}]
							})
					this.chiswin = win
					this.chiswin.add(this.form);
				}
				this.chiswin.show();
				var form = this.form.getForm()
				this.Field = form.findField("psw");
				this.Field.focus(false, 50);
			},
			dochiswinhide : function() {
				this.chiswin.hide();
			},
			systemInit : function() {
				var form = this.form.getForm()
				this.Field = form.findField("psw");
				var text = this.Field.getValue();
				this.saving = true
				this.panel.el.mask("正在初始化...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "systemInitializationService",
							serviceAction : "saveSystemInit",
							body : this.datas,
							password : text
						}, function(code, msg, json) {
							this.panel.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer);
								return
							}
							if (json.body) {
								this.tree.getLoader().load(this.tree
										.getRootNode());
								this.chiswin.hide();
							}
						}, this)// jsonRequest
			}
		})