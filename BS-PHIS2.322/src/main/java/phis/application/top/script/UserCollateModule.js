$package("com.bsoft.phis.pub")

/*
 * 科室员工维护界面module liyl 2012.3.29
 */

$import("app.modules.common", "com.bsoft.phis.SimpleList",
		"util.dictionary.TreeDicFactory")

com.bsoft.phis.pub.UserCollateModule = function(cfg) {
	// this.title = "机构用户管理"
	cfg.disablePagingTbr = true;
	this.autoLoadSchema = true
	this.activeModules = {}
	this.westWidth = cfg.westWidth || 250
	this.gridDDGroup = 'gridDDGroup'
	this.saveServiceId = "simpleSave"
	this.showNav = true
	this.width = 950;
	this.height = 450
	this.cmd = "create";
	cfg.winState = [300, 80]
	this.serverParams = {
		"serviceAction" : cfg.serviceAction
	}
	com.bsoft.phis.pub.UserCollateModule.superclass.constructor.apply(this,
			[cfg])

	this.on("winShow", this.onWinShow, this);
}
var userCollate_ctx = null
function doCollate() {
	userCollate_ctx.doCollate();
}
function delCollate() {

}
Ext.extend(com.bsoft.phis.pub.UserCollateModule, com.bsoft.phis.SimpleList, {
	warpPanel : function(grid) {
		if (!this.showNav) {
			return grid
		}
		var navDic = this.navDic
		var tf = util.dictionary.TreeDicFactory.createDic({
					dropConfig : {
						ddGroup : 'gridDDGroup',
						scope : this
					},
					id : navDic,
					// sliceType : 5,
					parentKey : this.navParentKey || {},
					rootVisible : this.rootVisible || false
				})
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [{
								layout : "fit",
								split : true,
								collapsible : true,
								title : '',
								region : 'west',
								width : this.westWidth,
								items : tf.tree
							}, {
								layout : "fit",
								split : true,
								title : '',
								region : 'center',
								width : 280,
								items : grid
							}]
				});
		this.tree = tf.tree
		grid.__this = this
		tf.tree.on("click", this.onCatalogChanage, this)
		this.tree.getLoader().on("load", this.onTreeLoad, this);
		this.tree.on("beforeexpandnode", this.onExpandNode, this);
		this.tree.on("beforecollapsenode", this.onCollapseNode, this);
		this.panel = panel;
		userCollate_ctx = this;
		this.listServiceId = "userCollateInfoQuery"
		return panel
	},
	doCollate : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			MyMessageTip.msg("提示", "请先选择需要对照的员工信息!", true);
			return;
		}
		if (r.get("USERID") != "") {
			var user = r.get("YGXM")
			MyMessageTip.msg("提示", "["+user + "]已经对照!", true);
			return
		}
		this.gw_ygxx = this.createModule("gy_ygxx", this.refGwygxx);
		if (!this.ygxx_win) {
			this.ygxx_win = this.gw_ygxx.getWin();
			this.ygxx_win.add(this.gw_ygxx.initPanel());
			this.gw_ygxx.on("doChoose", this.doChoose, this);
		}
		this.gw_ygxx.manaUnitId = r.get("JGID");

		this.ygxx_win.show();
	},
	attachTopLnkEnvents : function() {
//		var lnks = this.ehrPanel.body.query("div.x-ehrview-toplnk")
//		if (lnks) {
//			for (var i = 0; i < lnks.length; i++) {
//				var lnk = Ext.get(lnks[i])
//				alert(lnk.id)
//				//lnk.on("click", this.onTopLnkClick, this)
//			}
//		}
	},
	onWinShow : function() {
		this.win.maximize()
		//alert(Ext.encode(this.ehrPanel.body))
	},
	onDblClick : function(grid, index, e) {
		this.doCollate();
	},
	doChoose : function(record) {
		var r = this.getSelectedRecord();
		r.set("USERID", record.get("userId"));
		r.set("PASSWORD", record.get("password"));
		r.set("USERNAME", record.get("userName"));
		r.set("MANAUNITID", record.get("manaUnitId"));
		r.set("MANAUNITID_TEXT", record.get("manaUnitId_text"));
		r.set("JOBID", record.get("jobId"));
		r.set("JOBID_TEXT", record.get("jobId_text"));
		this.doSave(r);

	},
	doSave : function(record) {
		var values = record.data;
		Ext.apply(this.data, values);
		this.saveToServer(values)
	},
	chooseRenderer : function(v, params, record) {
		// var ret = "<input type='button' value='对照'
		// onClick='doCollate()' title='维护对照关系' />";
		// if (record.get("userId") != null && record.get("userId") !=
		// "") {
		// ret += "&nbsp;&nbsp;/&nbsp;&nbsp;<input type='button'
		// value='删除' onClick='delCollate()' title='删除对照关系' />";
		// }
		// return ret;
	},
	onTreeLoad : function(loader, node) {
		if (node) {
			if(!this.select){
				node.select();
				this.select = true;
				if (!isNaN(node.id)) {
					this.onCatalogChanage(node, this);
				}
			}
			if (node.getDepth() == 0) {
				if (node.hasChildNodes()) {
					node.firstChild.expand();
				}
			}
		}
		// 判断node是否有type
		if (node.hasChildNodes()) {
			node.eachChild(function(curNode) {
						if (!curNode.type) {
							curNode.setIcon(ClassLoader.appRootOffsetPath
									+ "images/close.png");
						}
					});
		}
	},
	onExpandNode : function(node) {
		if (node.getDepth() > 0 && !node.type) {
			node.setIcon(ClassLoader.appRootOffsetPath + "images/open.png");
			// 判断node是否有type
		}
	},
	onCollapseNode : function(node) {
		if (node.getDepth() > 0 && !node.type) {
			node.setIcon(ClassLoader.appRootOffsetPath + "images/close.png");
		}

	},
	onCatalogChanage : function(node, e) {
		// alert(Ext.encode(node.getUI().iconCls));
		var type = node.attributes.type
		var id = node.id;
		var initCnd = this.initCnd
		var queryCnd = this.queryCnd
		if (!isNaN(id)) {
			// 根据节点类型 判断是机构还是科室 科室按科室代码查询 机构按机构代码LIKE查询
			if (type) {
				// 特殊处理: 如果是医院类型,只查本院数据不检索下级村站数据
				if (type == 'c' || type == 'd') {
					cnd = ['eq', ['$', 'a.JGID'], ['s', id]]
				} else if (type == 'a' || type == 'b') {
					cnd = ['like', ['$', 'a.JGID'], ['s', id + '%']]
				} else {
					return;
				}
			} else {
				cnd = ['eq', ['$', 'a.KSDM'], ['s', id]]
			}
			// cnd = ['eq', ['$', 'a.KSDM'], ['s', node.id]]
			this.initCnd = cnd;
			this.navCnd = cnd
			this.requestData.cnd = cnd
			this.requestData.nodId = node.id
		}
		this.refresh()
	},
	// onTreeNotifyDrop : function(dd, e, data) {
	// var n = this.getTargetFromEvent(e);
	// var r = dd.dragData.selections[0];
	// var node = n.node
	// var ctx = dd.grid.__this
	//
	// if (!node.leaf || node.id == r.data[ctx.navField]) {
	// return false
	// }
	// var updateData = {}
	// updateData[ctx.schema.pkey] = r.id
	// updateData[ctx.navField] = node.attributes.key
	// ctx.saveToServer(updateData, r)
	// // node.expand()
	// },
	addPanelToWin : function() {
		if (!this.fireEvent("panelInit", this.grid)) {
			return;
		};
		var win = this.getWin();
		win.add(this.warpPanel(this.grid))
		win.doLayout()
	},
	saveToServer : function(saveData, r) {
		var entryName = "SYS_USERCOLLATE"

		if (!this.fireEvent("beforeSave", entryName, "create", saveData)) {
			return;
		}
		this.saving = true
		this.tree.el.mask("在正保存数据...", "x-mask-loading")
		phis.script.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					op : "create",
					schema : entryName,
					body : saveData
				}, function(code, msg, json) {
					this.tree.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData]);
						return
					} else {
						this.grid.store.remove(r)
						this.fireEvent("save", entryName, json.body, this.op)
					}
				}, this)// jsonRequest
	},
	// 刚打开页面时候默认选中数据,这时候判断下作废按钮
	onStoreLoadData : function(store, records, ops) {
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if (records.length == 0) {
			return
		}
		if (!this.selectedIndex || this.selectedIndex >= records.length) {
			this.selectRow(0);
			this.onRowClick();
		} else {
			this.selectRow(this.selectedIndex);
			this.selectedIndex = 0;
			this.onRowClick();
		}
	},
	doDelCollate : function() {
		var r = this.getSelectedRecord()
		if (r.get("USERID") == "") {
			var user = r.get("YGXM")
			MyMessageTip.msg("提示","["+user + "]还未对照!", true);
			return
		}
		this.entryName = "SYS_USERCOLLATE"
		this.doRemove();
	},
	doRemove : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		var title = r.data.YGXM;
		if (this.isCompositeKey) {
			title = "";
			for (var i = 0; i < this.schema.pkeys.length; i++) {
				title += r.get(this.schema.pkeys[i])
			}
		}
		// add by liyl 2012-06-17 提示信息增加名称显示功能
		if (this.removeByFiled && r.get(this.removeByFiled)) {
			title = r.get(this.removeByFiled);
		}
		Ext.Msg.show({
					title : '提示',
					msg : '确认取消[' + title + ']的对照吗?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.processRemove(r);
						}
					},
					scope : this
				})
	},
	processRemove : function(r) {
		// var r = this.getSelectedRecord()
		// if (r == null) {
		// return
		//		}
		if (!this.fireEvent("beforeRemove", this.entryName, r)) {
			return;
		}
		this.mask("在正取消对照...");
		var compositeKey;
		if (this.isCompositeKey) {
			compositeKey = {};
			for (var i = 0; i < this.schema.pkeys.length; i++) {
				compositeKey[this.schema.pkeys[i]] = r
						.get(this.schema.pkeys[i]);
			}
		}
		phis.script.rmi.jsonRequest({
			serviceId : this.removeServiceId,
			pkey : r.id,
			body : compositeKey,
			schema : this.entryName,
			action : "remove", // 按钮标识
			module : this.grid._mId
				// 增加module的id
			}, function(code, msg, json) {
			this.unmask()
			if (code < 300) {
				r.set("USERID", "");
				r.set("PASSWORD", "");
				r.set("USERNAME", "");
				r.set("MANAUNITID", "");
				r.set("MANAUNITID_TEXT", "");
				r.set("JOBID", "");
				r.set("JOBID_TEXT", "");
				this.fireEvent("remove", this.entryName, 'remove', json,
								r.data)
			} else {
				this.processReturnMsg(code, msg, this.doRemove)
			}
		}, this)
	},
	getWin : function() {
		var win = this.win
		var closeAction = "hide"
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : false,
						resizable : false,
						maximizable : false,
						shadow : false,
						modal : false
					})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			win.on("show", this.onWinShow, this)
			win.on("hide", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("beforehide", function() {
						this.fireEvent("beforeclose", this)
					}, this)
			this.win = win
		}
		//win.getEl().first().applyStyles("display:none;");
		return win;
	}

})