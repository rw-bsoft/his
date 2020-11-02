$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRMedicalRecordTemplatesManageList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.removeByFiled = "LBMC";
	phis.application.emr.script.EMRMedicalRecordTemplatesManageList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRMedicalRecordTemplatesManageList,
		phis.script.SimpleList, {
			doRefresh : function() {
				this.refresh();
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.keys.length; i++) {
						title += r.get(this.schema.keys[i])
					}
				}
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.emrManageService",
							serviceAction : "getBLLBInfo",
							body : r.data
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				} else {
					if (ret.json.num && ret.json.num > 0) {
						MyMessageTip.msg("提示", "有子类别,无法删除", true);
						return;
					}
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
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("正在删除数据...");
				var pkey = r.id;
				if (this.isCompositeKey) {
					pkey = {};
					for (var i = 0; i < this.schema.keys.length; i++) {
						pkey[this.schema.keys[i]] = r.get(this.schema.keys[i]);
					}
				}
				var removeRequest = this.getRemoveRequest(r)
				var removeCfg = {
					serviceId : this.removeServiceId,
					method : this.removeMethod,
					pkey : pkey,
					body : removeRequest,
					schema : this.entryName,
					action : "remove", // 按钮标识
					module : this.grid._mId
					// 增加module的id
				}
				this.fixRemoveCfg(removeCfg);
				phis.script.rmi.jsonRequest(removeCfg,
						function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.updatePagingInfo()
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
								this.opener.tree.tree.root.reload();
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
								this.opener.tree.tree.root.reload();
							}
						}, this)
			}
		})
