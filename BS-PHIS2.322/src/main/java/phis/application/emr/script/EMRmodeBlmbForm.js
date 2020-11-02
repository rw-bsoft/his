$package("phis.application.emr.script");

$import("phis.script.TableForm");

phis.application.emr.script.EMRmodeBlmbForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.emr.script.EMRmodeBlmbForm.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRmodeBlmbForm, phis.script.TableForm, {
			onReady : function() {
				phis.application.emr.script.EMRmodeBlmbForm.superclass.onReady
						.call(this);
				var datetime = Date.getServerDateTime();
				var jlsj = this.form.getForm().findField("JLSJ");
				var ysdm = this.form.getForm().findField("YSDM");
				jlsj.on("select", this.comboSelect, this);
				ysdm.on("select", this.comboSelect, this);
				jlsj.setValue(datetime);
				// ysdm.store.on("load", this.onWinShow, this);
				// this.on("winShow", this.onWinShow, this);
				this.onWinShow();
			},
			onWinShow : function() {
				if (this.bl01) {
					var jlsj = this.form.getForm().findField("JLSJ");
					var ysdm = this.form.getForm().findField("YSDM");
					jlsj.setValue(this.bl01.JLSJ);
					ysdm.setValue(this.bl01.SSYS);
					this.setYlValue();
				}
			},
			comboSelect : function() {
				if (this.bl01) {
					this.setYlValue();
				}
				this.fireEvent("changeYl")
			},
			setYlValue : function() {
				var form = this.form.getForm();
				var data = this.MBXX;
				var XSMC = data.XSMC
				var title = '';
				if (XSMC == "模板名称") {
					title = data.MBMC;
				} else if (XSMC == "科室名称") {
					title = "全科";
				} else {
					var xsmc = XSMC.split("+");
					for (var i = 0; i < xsmc.length; i++) {
						if (xsmc[i].indexOf("记录日期") >= 0) {
							var cgFmt = "";
							var fmt = xsmc[i].substring(xsmc[i].indexOf('{')
											+ 1, xsmc[i].indexOf('}'))
							if (fmt == 'yyyy年mm月dd日 hh:mm') {
								cgFmt = 'Y年m月d日 H:i'
							} else {
								cgFmt = 'Y-m-d H:i'
							}
							var jlrq = form.findField("JLSJ").getValue();
							var d = Date.parseDate(jlrq, 'Y-m-d H:i:s')
							title += d.format(cgFmt);
						} else if (xsmc[i].indexOf("类别名称") >= 0) {
							title += data.MBMC;
						} else if (xsmc[i].indexOf("医生姓名") >= 0) {
							title += form.findField("YSDM").getRawValue();
						} else if (xsmc[i].indexOf("换行符") >= 0) {
							title += '\r\n';
						} else {
							title += xsmc[i].replace(/\'/g, "");
						}
					}
				}
				form.findField("YL").setValue(title);
			},
			doSave : function() {
				var form = this.form.getForm();
				var doctor = form.findField("YSDM").getValue();
				var recordTime = form.findField("JLSJ").getValue();
				var title = form.findField("YL").getValue();
				if (!doctor || !recordTime) {
					MyMessageTip.msg("提示", "医生名称和记录时间不能为空!", true);
					return;
				}
				this.fireEvent("changeParaCation", doctor, recordTime, title);
				this.win.close();
			},
			doClose : function() {
				this.win.close();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
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
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : false,
								maximizable : false,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
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