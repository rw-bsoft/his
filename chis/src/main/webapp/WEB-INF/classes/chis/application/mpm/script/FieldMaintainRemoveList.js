$package("chis.application.mpm.script")
$import("chis.script.BizSelectListView")
chis.application.mpm.script.FieldMaintainRemoveList = function(cfg) {
	chis.application.mpm.script.FieldMaintainRemoveList.superclass.constructor.apply(this,
			[cfg]);
	this.disablePagingTbr = false;
	this.on("winShow", this.onWinShow, this)
};
Ext.extend(chis.application.mpm.script.FieldMaintainRemoveList, chis.script.BizSelectListView, {

			initPanel : function(schema) {
				var grid =app.modules.list.SelectListView.superclass.initPanel
						.call(this, schema);
				this.grid = grid;
				if (this.win) {
					this.win.on("show", function() {
								if (this.sm) {
									this.sm.clearSelections();
								}
							}, this);
				}
				return grid;
			},

			onWinShow : function() {
				var cnd = []
				cnd.push(this.masterplateId)
				this.requestData.cnd = cnd;
				this.requestData.serviceId = "chis.templateService";
				this.requestData.serviceAction = "listRemoveField";
				this.refresh();
				this.clearSelect();
			},

			doRemove : function() {
				var records = this.getSelectedRecords();
				if (!records || records.length == 0) {
					return;
				} else {
					Ext.Msg.show({
								title : '确认批量删除记录',
								msg : '删除操作将无法恢复，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.removeField(records);
									}
								},
								scope : this
							})
				}
			},
			removeField : function(records) {
				var fieldIds = "";
				if (records.length > 0) {
					fieldIds = this.getFieldIds(records);
				}
				util.rmi.jsonRequest({
							serviceId : "chis.templateService",
							serviceAction : "removeSelectField",
							method:"execute",
							fieldIds : fieldIds,
							masterplateId : this.masterplateId
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.refresh();
							this.fireEvent("remove", this);
						}, this);
			},
			getFieldIds : function(records) {
				var fieldIds = "";
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					var fieldId = r.id;
					fieldIds = fieldIds + fieldId + ","
				}
				fieldIds = fieldIds.substring(0, fieldIds.length - 1);
				return fieldIds;
			},
			doCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			},
			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			}

		})