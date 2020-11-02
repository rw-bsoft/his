$package("phis.application.war.script")

$import("phis.script.TableForm", "util.Accredit")

phis.application.war.script.WardClinicsRefundForm = function(cfg) {
	cfg.hideTrigger = true;
	cfg.showButtonOnTop = false;
	cfg.modal = true;
	cfg.autoLoadData = false;
	cfg.labelWidth = 45;
	phis.application.war.script.WardClinicsRefundForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.war.script.WardClinicsRefundForm,
		phis.script.TableForm, {
			onReady : function() {
				// 监听ZYHM回车事件
				var zyhm = this.form.getForm().findField("ZYHM");
				if (zyhm) {
					zyhm.on("specialkey", this.onZyhmSpecialkey, this)
				}
				var brch = this.form.getForm().findField("BRCH");
				if (brch) {
					brch.setDisabled(false);
					brch.on("specialkey", this.onBrchSpecialkey, this)
				}
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				var loadRequest = this.getLoadRequest();
				if (!this.initDataId && !this.initDataBody && !loadRequest) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId || this.initDataBody, loadRequest)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				var loadCfg = {
					serviceId : this.loadServiceId,
					method : this.loadMethod,
					schema : this.entryName,
					pkey : this.initDataId || this.initDataBody,
					body : loadRequest,
					action : this.op, // 按钮事件
					module : this._mId
					// 增加module的id
				}
				this.fixLoadCfg(loadCfg);
				phis.script.rmi.jsonRequest(loadCfg, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								this.fireEvent("exception", code, msg,
										loadRequest, this.initDataId
												|| this.initDataBody); // **
								// 用于一些异常处理
								return
							}
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("loadData", this.entryName,
										json.body);
							} else {
								this.initDataId = null;
								// **
								// 没有加载到数据，通常用于以fieldName和fieldValue为条件去加载记录，如果没有返回数据，则为新建操作，此处可做一些新建初始化操作
								this.fireEvent("loadNoData", this);
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)
			},
			// 去除form光标跳转
			focusFieldAfter : function() {

			},
			onBrchSpecialkey : function(f, e) {
				var key = e.getKey()
				if (key == e.ENTER) {
					e.stopEvent()
					// 查询输入的ZYHM是否有效
					var brch = f.getValue();
					if (!brch) {
						Ext.Msg.alert("提示", "请输入有效的床位号码!", function() {
									var brch = this.form.getForm()
											.findField("BRCH");
									brch.focus();
								}, this)
						return;
					}
					var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "wardPatientManageService",
								serviceAction : "loadZyhByCwh",
								body : {
									BRCH : brch,
									JGID : this.mainApp['phisApp'].deptId
								}
							});
					var code = res.code;
					var msg = res.msg;
					var body = res.json.body;
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					// var zyhm = this.form.getForm().findField("ZYHM");
					if (body && body.length > 0) {
						var brxx = body[0]
						if (brxx.BRBQ != this.mainApp['phis'].wardId) {
							MyMessageTip.msg("提示", "不能操作非本病区病人!", true);
							return;
						}
						if (brxx.CYPB == 1) {
							MyMessageTip
									.msg("提示", "不能操作已通知出院病人!", true);
							return;
						}
						if (brxx.CYPB > 1) {
							MyMessageTip.msg("提示", "不能操作已出院病人!", true);
							return;
						}
						if (brxx.ZKZT) {
							MyMessageTip.msg("提示", "不能操作转科病人!", true);
							return;
						}
						// 判断当前医嘱是否修改
						if (this.opener.beforeClose()) {
							// 载入对应医嘱信息
							this.initDataId = brxx.ZYH;
							this.loadData();
							this.opener.usedList.initDataId = brxx.ZYH;
							this.opener.usedList.loadData();
						}
					} else {
						Ext.Msg.alert("提示", "未找到该床号对应的医嘱信息，请检查输入是否正确!",
								function() {
									var brch = this.form.getForm()
											.findField("BRCH");
									brch.focus();
								}, this)
						return;
					}
				}
			},
			onZyhmSpecialkey : function(f, e) {
				var key = e.getKey()
				if (key == e.ENTER) {
					e.stopEvent()
					// 查询输入的ZYHM是否有效
					var zyhm = f.getValue();
					if (!zyhm) {
						Ext.Msg.alert("提示", "请输入有效的住院号码!", function() {
									var zyhm = this.form.getForm()
											.findField("ZYHM");
									zyhm.focus();
								}, this)
						return;
					}
					var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "wardPatientManageService",
								serviceAction : "loadZyhByZyhm",
								body : {
									ZYHM : zyhm,
									JGID : this.mainApp['phisApp'].deptId
								}
							});
					var code = res.code;
					var msg = res.msg;
					var body = res.json.body;
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					// var zyhm = this.form.getForm().findField("ZYHM");
					if (body && body.length > 0) {
						var brxx = body[0]
						if (brxx.BRBQ != this.mainApp['phis'].wardId) {
							MyMessageTip.msg("提示", "不能操作非本病区病人!", true);
							return;
						}
						if (brxx.CYPB == 1) {
							MyMessageTip
									.msg("提示", "不能操作已通知出院病人!", true);
							return;
						}
						if (brxx.CYPB > 1) {
							MyMessageTip.msg("提示", "不能操作已出院病人!", true);
							return;
						}
						if (brxx.ZKZT) {
							MyMessageTip.msg("提示", "不能操作转科病人!", true);
							return;
						}
						// 判断当前医嘱是否修改
						if (this.opener.beforeClose()) {
							// 载入对应医嘱信息
							this.initDataId = brxx.ZYH;
							this.loadData();
							this.opener.usedList.initDataId = brxx.ZYH;
							this.opener.usedList.loadData();
						}
					} else {
						Ext.Msg.alert("提示", "未找到该住院号对应的医嘱信息，请检查输入是否正确!",
								function() {
									var zyhm = this.form.getForm()
											.findField("ZYHM");
									zyhm.focus();
								}, this)
						return;
					}
				}
			}
		});
