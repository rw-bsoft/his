$package("phis.application.ivc.script");

$import("phis.script.TableForm");

phis.application.ivc.script.ClinicFeeForm2 = function(cfg) {
	cfg.colCount = 1;
	cfg.autoLoadData = false;
	cfg.height = 200;
	phis.application.ivc.script.ClinicFeeForm2.superclass.constructor.apply(
			this, [ cfg ]);
};
Ext.extend(phis.application.ivc.script.ClinicFeeForm2,phis.script.TableForm,{
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombiyined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		var schema = sc;
		if (!schema) {
			var re = util.schema.loadSync(this.entryName);
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel);
				return;
			}
		}
//		var ac = util.Accredit;
//		var defaultWidth = this.fldDefaultWidth || 200
//		var items = schema.items
//		if (!this.fireEvent("changeDic", items)) {
//			return
//		}
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
		};
		var msg = '<span id="SYBRCZXX" style="color:red;">&nbsp&nbsp&nbsp&nbsp上一病人操作信息：<br/>';
//		msg+="<table width='120px'><tr><td>总计金额：</td><td style='text-align:right;'>1.00&nbsp;￥</td></tr>";
//		msg+="<tr><td>自负金额：</td><td>1.00&nbsp;￥</td></tr>";
//		msg+="<tr><td>应&nbsp收&nbsp&nbsp款：</td><td>1.00&nbsp;￥</td></tr>";
//		msg+="<tr><td>缴&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp款：</td><td>1.00&nbsp;￥</td></tr>";
//		msg+="<tr><td>退&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp找：</td><td>1.00&nbsp;￥</td></tr></table>";
			msg +="</span>";
		table.items.push({
			xtype : "label",
			html : msg
		});
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
		};

		if (this.isCombined) {
			cfg.frame = true;
			cfg.shadow = false;
			// cfg.width = this.width
			// cfg.height = this.height
		} else {
			// cfg.autoWidth = true
			// cfg.autoHeight = true
		}
		this.initBars(cfg);
		Ext.apply(table, cfg);
		this.expansion(table);// add by yangl
		this.form = new Ext.FormPanel(table);
		this.form.on("afterrender", this.onReady, this);

		this.schema = schema;
		this.setKeyReadOnly(true);
		if (!this.isCombined) {
			this.addPanelToWin();
		}
		return this.form;
	},
	onReady : function() {

	},
	setSYBRMsg : function(){
		var msg = '&nbsp&nbsp&nbsp&nbsp上一病人操作信息：<br/>';
//		for(var i = 0;i<this.opener.opener.fkxxs.length; i++){
//			var fkxx = this.opener.opener.fkxxs[i];
//			msg +='<tr><td>&nbsp&nbsp&nbsp&nbsp'+fkxx.FKMC+'</td><td>&nbsp</td><td>'+fkxx.FKJE+'</td><tr/>';
//		}
//		msg +="</table>";
//		if(document.getElementById("SYBRCZXX")){
//			document.getElementById("SYBRCZXX").innerHTML=msg;
//		}
		if (this.opener.opener.opener.JSXX) {
			msg+="<table width='120px'><tr><td>总计金额：</td><td style='text-align:right;'>"+parseFloat(this.opener.opener.opener.JSXX.ZJJE).toFixed(2)+"&nbsp;￥</td></tr>";
			msg+="<tr><td>自负金额：</td><td style='text-align:right;'>"+parseFloat(this.opener.opener.opener.JSXX.ZFJE).toFixed(2)+"&nbsp;￥</td></tr>";
			msg+="<tr><td>应&nbsp收&nbsp&nbsp款：</td><td style='text-align:right;'>"+parseFloat(this.opener.opener.opener.JSXX.YSK).toFixed(2)+"&nbsp;￥</td></tr>";
			msg+="<tr><td>缴&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp款：</td><td style='text-align:right;'>"+parseFloat(this.opener.opener.opener.JSXX.JKJE).toFixed(2)+"&nbsp;￥</td></tr>";
			msg+="<tr><td>退&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp找：</td><td style='text-align:right;'>"+parseFloat(this.opener.opener.opener.JSXX.TZJE).toFixed(2)+"&nbsp;￥</td></tr></table>";
			document.getElementById("SYBRCZXX").innerHTML = msg;
		}else{
			document.getElementById("SYBRCZXX").innerHTML=msg;
		}
	}
				});