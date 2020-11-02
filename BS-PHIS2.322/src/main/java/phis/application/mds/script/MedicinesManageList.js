$package("phis.application.mds.script")

$import("phis.script.SimpleList", "phis.script.widgets.Strategy",
	"org.ext.ux.ColumnHeaderGroup")

phis.application.mds.script.MedicinesManageList = function(cfg) {
	cfg.winState = "center";// cfg.winState=[100,50]两个写法都可以
	phis.application.mds.script.MedicinesManageList.superclass.constructor
		.apply(this, [cfg])
}
Ext.extend(phis.application.mds.script.MedicinesManageList,
	phis.script.SimpleList, {
		// 打开调入页面
		doImport : function() {
			this.list = this.createModule("impList", this.impref);
			this.list.on("save", this.onSave, this);
			var p = this.list.getWin();
			p.show();
		},
		// 单击时改变作废按钮
		onRowClick : function() {
			var r = this.getSelectedRecord();
			if (r == null) {
				return;
			}
			this.changeButtonName(r.data.ZFPB);
		},
		onRenderer : function(value, metaData, r) {
			if (r.data.ZFPB == 1) {
				return "<img src='" + ClassLoader.appRootOffsetPath
					+ "resources/phis/resources/images/(00,04).png'/>"
			}
			return value;
		},
		//药品数据引入
		doLsjdr: function () {
			var drList = this.createModule("lsjdrList", "phis.application.mds.MDS/MDS/MDS01010106")
			// drList.on("qrdr", this.onQrdr, this);
			var win = drList.getWin();
			win.setWidth(1000);
			win.setHeight(600);
			win.add(drList.initPanel());
			win.show();
		},
		// onQrdr: function (ypxh) {
		// 	this.refresh();
		// 	var module = this.createModule("ypxx_module", "phis.application.mds.MDS/MDS/MDS010101");
		// 	module.on("save", this.onSave, this);
		// 	var win = module.getWin();
		// 	win.show();
		// 	module.initDataId = ypxh;
		// 	module.loadData();
		//
		// },
		// 作废和取消作废
		doInvalid : function() {
			var r = this.getSelectedRecord();
			if (r == null) {
				return;
			}
			var key = r.data.YPXH;
			var valeu = r.data.ZFPB;
			var title ='';
			if(valeu==1){
				title='取消药品作废';
			}else{
				title='作废药品';
			}

			Ext.Msg.show({
				title : '修改作废状态',
				msg : '' + title + '，是否继续?',
				modal : true,
				width : 200,
				buttons : Ext.MessageBox.OKCANCEL,
				multiline : false,
				fn : function(btn, text) {
					if (btn == "ok") {
						this.doChange(key,valeu);
					}
				},
				scope : this
			})
		},
		doChange : function(key,valeu) {
			var body = {};
			body["ypxh"] = key;
			body["zfpb"] = valeu;
			var ret = phis.script.rmi.miniJsonRequestSync({
				serviceId : this.serviceId,
				serviceAction : this.invalidActionId,
				body : body
			});
			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg, this.doInvalid);
			} else {
				this.refresh();
			}
		},
		// 加上鼠标移动提示记录是否已作废功能
		onReady : function() {
			phis.application.mds.script.MedicinesManageList.superclass.onReady
				.call(this);
			this.grid.on("mouseover", this.onMouseover, this);
			this.grid.on("keypress", this.onKeypress, this);
		},
		// 鼠标移动提示记录是否已作废功能
		onMouseover : function(e) {
			var index = this.grid.getView().findRowIndex(e.getTarget());
			if (index >= 0) {
				var record = this.store.getAt(index);
				if (record) {
					if (record.data.ZFPB == 1) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
							qtip : '药品已作废'
						}, false);
					}
				}
			}
		},
		// 上下时改变作废按钮
		onKeypress : function(e) {
			if (e.getKey() == 40 || e.getKey() == 38) {
				this.onRowClick();
			}
		},
		// 刚打开页面时候默认选中第一条数据,这时候判断下作废按钮
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
		// 右键 作废和取消作废相应改变
		onContextMenu : function(grid, rowIndex, e) {
			if (e) {
				e.stopEvent()
			}
			if (this.disableContextMenu) {
				return
			}
			this.grid.getSelectionModel().selectRow(rowIndex)
			var r = this.getSelectedRecord();
			var cmenu = this.midiMenus['gridContextMenu']
			if (!cmenu) {
				var items = [];
				var actions = this.actions
				if (!actions) {
					return;
				}
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var it = {}
					it.cmd = action.id
					it.ref = action.ref
					it.iconCls = action.iconCls || action.id
					it.script = action.script
					it.text = action.name
					if (action.id == "invalid") {
						if (r.data.ZFPB == 1) {
							it.text = "取消作废";
						}
					}
					it.handler = this.doAction
					it.scope = this
					items.push(it)
				}
				cmenu = new Ext.menu.Menu({
					items : items
				})
				this.midiMenus['gridContextMenu'] = cmenu
			} else {
				for (var i = 0; i < cmenu.items.length; i++) {
					if (cmenu.items.itemAt(i).cmd == "invalid") {
						if (r.data.ZFPB == 1) {
							cmenu.items.itemAt(i).setText("取消作废");
						} else {
							cmenu.items.itemAt(i).setText("作废");
						}
					}
				}

			}
			// @@ to set menuItem disable or enable according to buttons of
			// toptoolbar.
			var toolBar = this.grid.getTopToolbar();
			if (toolBar) {
				for (var i = 0; i < this.actions.length; i++) {
					var btn = toolBar.find("cmd", this.actions[i].id);
					if (!btn || btn.length == 0) {
						continue;
					}
					if (btn[0].disabled) {
						cmenu.items.itemAt(i).disable();
					} else {
						cmenu.items.itemAt(i).enable();
					}

				}
			}
			cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
		},
		// 转换按钮名字
		changeButtonName : function(zfpb) {
			var btn = this.grid.getTopToolbar().find("cmd", "invalid")[0];
			if (zfpb == 1) {
				if (btn.getText().indexOf("取消") > -1) {
					return false;
				}
				btn.setText(btn.getText().replace("作废", "取消作废"));
			} else {
				if (btn.getText().indexOf("取消") > -1) {
					btn.setText(btn.getText().replace("取消作废", "作废"));
				} else {
					return false;
				}
			}
			return true;
		},
		// 打印 dingcj
		doPrint : function() {
			var module = this.createModule("basicMediMsg",
				this.refbasicMediMsg);
			var r = this.getSelectedRecord()
			if (r == null) {
				MyMessageTip.msg("提示", "打印失败：无效的药品信息!", true);
				return;
			}
			// 得到检索的类别和值
			module.LB = this.cndFldCombox.getValue();
			module.VALUE = this.cndField.getValue();
			if(this.ZBY){
				module.ZBY=1
			}
			module.initPanel();
			module.doPrint();
		}

	})