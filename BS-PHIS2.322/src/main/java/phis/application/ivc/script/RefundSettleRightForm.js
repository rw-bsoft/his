$package("phis.application.ivc.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.ivc.script.RefundSettleRightForm = function(cfg) {
	cfg.colCount = 1;
	cfg.autoLoadData = false;
//	cfg.labelWidth = 65;
	// cfg.showButtonOnTop = true;
	cfg.height = 200;
	cfg.width = 500;
	phis.application.ivc.script.RefundSettleRightForm.superclass.constructor
			.apply(this, [cfg])
//	this.showButtonOnTop = true;
}

Ext.extend(phis.application.ivc.script.RefundSettleRightForm,
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
				var msg = '<span id="yfpfkqk" style="color:red;">&nbsp&nbsp&nbsp&nbsp原发票付款情况：<br/><br/>';
				msg += '<table width="200">';
				for(var i = 0;i<this.opener.opener.fkxxs.length; i++){
					var fkxx = this.opener.opener.fkxxs[i];
					msg +='<tr><td>&nbsp&nbsp&nbsp&nbsp<span style="color:red;">'+fkxx.FKMC+'</span></td><td>&nbsp</td><td><span style="color:red;">'+fkxx.FKJE+'</span></td><tr/>';
				}
					msg +="</table></span>";
//				msg +="</span>";
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

			},
			setYfpMsg : function(){
				var msg = '<table width="200">&nbsp&nbsp&nbsp&nbsp原发票付款情况：<br/><br/>';
				for(var i = 0;i<this.opener.opener.fkxxs.length; i++){
					var fkxx = this.opener.opener.fkxxs[i];
					msg +='<tr><td>&nbsp&nbsp&nbsp&nbsp'+fkxx.FKMC+'</td><td>&nbsp</td><td>'+fkxx.FKJE+'</td><tr/>';
				}
				msg +="</table>";
				if(document.getElementById("yfpfkqk")){
					document.getElementById("yfpfkqk").innerHTML=msg;
				}
				
			}
		});