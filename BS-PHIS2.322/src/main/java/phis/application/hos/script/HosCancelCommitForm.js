$package("phis.application.hos.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.hos.script.HosCancelCommitForm = function(cfg) {
	cfg.colCount = 1;
	cfg.autoLoadData = false;
//	cfg.labelWidth = 65;
	// cfg.showButtonOnTop = true;
	cfg.height = 200;
	cfg.width = 500;
	phis.application.hos.script.HosCancelCommitForm.superclass.constructor
			.apply(this, [cfg])
//	this.showButtonOnTop = true;
}

Ext.extend(phis.application.hos.script.HosCancelCommitForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombiyined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
//				var items = schema.items
//				if (!this.fireEvent("changeDic", items)) {
//					return
//				}
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							border : 0,
							cellpadding : '1',
							cellspacing : "1"
						}
					},
					items : []
				}
//				if (!this.autoFieldWidth) {
//					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
//							* colCount
//					table.layoutConfig.forceWidth = forceViewWidth
//				} 
				var msg = '<br/>&nbsp&nbsp&nbsp&nbsp取消日结说明：<br/><br/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp1.取消结账日报请慎重'
					var navigatorName = "Microsoft Internet Explorer";
					if (navigator.appName == navigatorName) {
						msg += '<br/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2.每次取消只能取消最近的一次日终结帐'
					} else {
						msg += '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<br/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2.每次取消只能取消最近的一次日终结帐'
					}
					msg += '<br/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3.结账日报取消后不能恢复，只能重新结账';
//					msg += '<br/><br/>欲取消的结账日报：<br/>';
				table.items.push({
					xtype : "label",
					html : msg
				})
				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					autoScroll : true,
					// collapsible : false,
					// autoWidth : true,
					// autoHeight : true,
					floating : false
				}

				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					// cfg.width = this.width
					// cfg.height = this.height
				} else {
					// cfg.autoWidth = true
					// cfg.autoHeight = true
				}
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.expansion(table);// add by yangl
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)

				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			onReady : function() {

			}
		});