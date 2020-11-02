$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.NumberConfigList = function(cfg) {
	cfg.selectFirst = false
	this.initCnd = ['and',['eq', ['$', 'PJLX'], ['d', cfg.PJLX]],['eq',['$','JGID'],['s',cfg.deptId]]]
	phis.application.ivc.script.NumberConfigList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.ivc.script.NumberConfigList, phis.script.SimpleList, {

			// add by liyl 2012-08-29 票据号码删除判断
		doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("QSHM") != r.get("SYHM")) {
					Ext.Msg.alert("提示", "起始号码与使用号码不相同 ,号码已使用,无法删除!");
					return false;
				}
				var title = r.id;
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
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
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
			selectRow : function(v) {
				if (!this.grid.hidden) {
					this.grid.el.focus()
				}
				try {
					if (this.grid && this.selectFirst) {
						var sm = this.grid.getSelectionModel()
						if (sm.selectRow) {
							sm.selectRow(v)
						}
						this.fireEvent("firstRowSelected", this)
					}
				} catch (e) {
				}
			}

		});