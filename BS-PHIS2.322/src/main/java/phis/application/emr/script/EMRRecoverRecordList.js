$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRRecoverRecordList = function(cfg) {
	cfg.selectFirst = false;
	cfg.disablePagingTbr = true;
	this.gridCreator = Ext.Panel;
	phis.application.emr.script.EMRRecoverRecordList.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeclose", this.beforeClose, this);
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(phis.application.emr.script.EMRRecoverRecordList, phis.script.SimpleList,
		{
			getActiveXPanel : function() {
				var ocxStr = ""
				if (Ext.isIE) {
					ocxStr = "<div style='display:none'><OBJECT id='emrOcx_hide' name='emrOcx_hide' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'></OBJECT></div>"
				} else {
					ocxStr = "<div><OBJECT id='emrOcx_hide' TYPE='application/x-itst-activex' WIDTH='0' HEIGHT='0' clsid='{FFAA1970-287B-4359-93B5-644F6C8190BB}'></OBJECT></div>"
				}
				var panel = new Ext.Panel({
							frame : true,
							border : false,
							html : ocxStr
						});
				return panel;
			},
			onWinShow : function() {
				var emr = document.getElementById("emrOcx_hide");
				if (emr) {
					emr.FunActiveXInterface('BsNewDocument', '', '');
				}
				this.emr = emr;
			},
			onDblClick : function() {
				this.doRenew();
			},
			doClose : function() {
				this.win.hide();
			},
			beforeClose : function() {
				// 删除缓存
				if (!this.notRemoveRecord) {
					for (var i = 0; i < this.store.getCount(); i++) {
						this.emr.FunActiveXInterface('BsEditAutoData', '1',
								this.store.getAt(i).get("BLBH") + '#'
										+ this.mainApp.uid);
					}
				}
				this.notRemoveRecord = false;
				return true;
			},
			doPreview : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要预览的模版记录 !", true);
					return;
				}
				// 根据mbbh获取模版内容
				var data = r.data;
				this.emr.FunActiveXInterface("BsEditAutoData", '0', data.BLBH
								+ "#" + this.mainApp.uid);
				var uft8Text = this.emr.WordData
				var s = this.emr.FunActiveXInterface("BsPreviewAsHtml",
						(data.BLLX == 1 ? '2' : '1'), uft8Text);
				var url = this.emr.StrReturnData;
				if (url) {
					window.open(url);
				}
			},
			// 恢复
			doRenew : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要恢复的记录!", true);
					return;
				}
				// 校验恢复文档的有效性
				var node = r.get("Node");
				node.BLBH = r.get("BLBH");
				node.BLLX = r.get("BLLX");
				node.text = r.get("BLMC");
				node.key = r.get("BLLB");
				this.opener.openEmrEditorModule(node, r.data);
				if (this.store.getCount() == 1) {
					this.notRemoveRecord = true;
					this.win.hide();
				} else {
					this.store.remove(r);
				}
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								collapsible : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : false,
								maximizable : false,
								shadow : false,
								modal : true,
								items : [this.initPanel(),
										this.getActiveXPanel()]
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
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
				return win;
			}
		})
