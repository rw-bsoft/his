$package("phis.application.pcm.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.pcm.script.PrescriptionCommentsCQForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.colCount = 4
	this.labelWidth = 55
	phis.application.pcm.script.PrescriptionCommentsCQForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsCQForm,
		phis.script.TableForm, {
			doNew : function() {
				phis.application.pcm.script.PrescriptionCommentsCQForm.superclass.doNew
						.call(this);
				var date = new Date();
				this.form.getForm().findField("CFRQF").setValue(date
						.getFirstDateOfMonth().format("Y-m-d"))
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.queryXtcsAction
						});
						if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return 
						}
					this.form.getForm().findField("CYSL").setValue(ret.json.CFDPCQSL)	
			}
			// 重写为了去掉->后面的:
//			initPanel : function(sc) {
//				if (this.form) {
//					if (!this.isCombined) {
//						this.addPanelToWin();
//					}
//					return this.form;
//				}
//				var schema = sc
//				if (!schema) {
//					var re = util.schema.loadSync(this.entryName);
//					if (re.code == 200) {
//						schema = re.schema;
//					} else {
//						this.processReturnMsg(re.code, re.msg, this.initPanel)
//						return;
//					}
//				}
//				var ac = util.Accredit;
//				var defaultWidth = this.fldDefaultWidth || 200
//				var items = schema.items
//				if (!this.fireEvent("changeDic", items)) {
//					return
//				}
//				var colCount = this.colCount;
//
//				var table = {
//					layout : 'tableform',
//					layoutConfig : {
//						columns : colCount,
//						tableAttrs : {
//							border : 0,
//							cellpadding : '2',
//							cellspacing : "2"
//						}
//					},
//					items : []
//				}
//				if (!this.autoFieldWidth) {
//					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
//							* colCount
//					table.layoutConfig.forceWidth = forceViewWidth
//				}
//				var size = items.length
//				for (var i = 0; i < size; i++) {
//					var it = items[i]
//					if ((it.display == 0 || it.display == 1)
//							|| !ac.canRead(it.acValue)) {
//						continue;
//					}
//					var f = this.createField(it)
//					if (it.id == "CFRQT") {
//						f.labelSeparator = ""
//					}
//					f.index = i;
//					f.anchor = it.anchor || "100%"
//					delete f.width
//
//					f.colspan = parseInt(it.colspan)
//					f.rowspan = parseInt(it.rowspan)
//
//					if (!this.fireEvent("addfield", f, it)) {
//						continue;
//					}
//					table.items.push(f)
//				}
//
//				var cfg = {
//					buttonAlign : 'center',
//					labelAlign : this.labelAlign || "left",
//					labelWidth : this.labelWidth || 80,
//					frame : true,
//					shadow : false,
//					border : false,
//					collapsible : false,
//					// autoWidth : true,
//					autoHeight : true,
//					autoScroll : true,
//					floating : false
//				}
//
//				if (this.isCombined) {
//					cfg.frame = true
//					cfg.shadow = false
//					// cfg.width = this.width
//					cfg.height = this.height
//				} else {
//					// cfg.autoWidth = true
//					cfg.autoHeight = true
//				}
//				if (this.disAutoHeight) {
//					delete cfg.autoHeight;
//				}
//				this.initBars(cfg);
//				Ext.apply(table, cfg)
//				this.expansion(table);// add by yangl
//				this.form = new Ext.FormPanel(table)
//				this.form.on("afterrender", this.onReady, this)
//
//				this.schema = schema;
//				this.setKeyReadOnly(true)
//				if (!this.isCombined) {
//					this.addPanelToWin();
//				}
//				return this.form
//			},
			
		})