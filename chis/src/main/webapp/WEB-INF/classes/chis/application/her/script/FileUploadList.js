$package("chis.application.her.script");

$import("chis.script.BizSimpleListView");

chis.application.her.script.FileUploadList = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.closeAction = true;
	cfg.modal = true;
	chis.application.her.script.FileUploadList.superclass.constructor.apply(
			this, [cfg]);
	this.on("remove", this.onFileRemove, this);
};

Ext.extend(chis.application.her.script.FileUploadList,
		chis.script.BizSimpleListView, {
			loadData : function() {
				if (!this.setId) {
					this.store = new Ext.data.Store();
					return;
				}
				this.initCnd = ['and',
						['eq', ['$', 'exeId'], ['s', this.exeId || '']],
						['eq', ['$', 'setId'], ['s', this.setId || '']],
						['eq', ['$', 'recordId'], ['s', this.recordId || '']]];
				this.requestData.cnd = this.initCnd;
				chis.application.her.script.FileUploadList.superclass.loadData
						.call(this);
			},

			doFileDownLoad : function() {
				var r = this.getSelectedRecord();
				var fileId = r.get("fileId");
				window.open("resources/chis/resources/chisUrlProxy/*.emDownLoad?fileId="+ fileId);
			},

			onDblClick : function() {
				this.doFileDownLoad();
			},

			onFileRemove : function(entryName, op, json, data) {
				if (json.success) {
					this.refresh();
				}
			},

			doRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录',
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
						});
			}
		});