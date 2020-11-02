$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRTemperatureList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.group = "CJSJ";
	cfg.groupTextTpl = "<table width='90%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='20%'>&nbsp;&nbsp;<b>采集时间:{[values.rs[0].data.CJSJ]}</b></td></tr></table>"
	phis.application.emr.script.EMRTemperatureList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.application.emr.script.EMRTemperatureList,phis.script.SimpleList, {
	          
	        doXg : function(){
	        	this.onDblClick();
	        },
			onDblClick : function() {
				this.opener.tab.activate(0);
				var CJSJ = this.getSelectedRecord().data.CJSJ;
				this.form.getForm().reset();
				for(var i = 0;i < this.store.getCount();i++){
					var CJSJ2 = this.store.getAt(i).data.CJSJ;
					if(CJSJ == CJSJ2){
						var CJH = this.store.getAt(i).data.CJH;
						var CJSJ = this.store.getAt(i).data.CJSJ;
						var XMXB = this.store.getAt(i).data.XMXB;
						var XMMC =this.store.getAt(i).data.XMMC;
						var TZNR = this.store.getAt(i).data.TZNR;
						var XMH = this.store.getAt(i).data.XMH;
						var BZXX = this.store.getAt(i).data.BZXX;
						var d = Date.parseDate(CJSJ,'Y-m-d H:i:s');
						var hours = d.getHours();
						this.form.getForm().CJH = CJH;
						this.form.getForm().findField("CJSJ").setValue(d);
						// this.form.getForm().findField("SJ").setValue(d.format('H:i'));
						if(XMH == 1){ // 体温
							this.form.getForm().findField("XMXB").setValue(XMXB);
							this.form.getForm().findField("TW").setValue(TZNR);
						}else if(XMH == 2){
							this.form.getForm().findField("MB").setValue(TZNR);
						}else if(XMH == 3){
							this.form.getForm().findField("HX").setValue(TZNR);
						}else if(XMH == 4){
							this.form.getForm().findField("XL").setValue(TZNR);
						}else if(XMH == 5){
							this.form.getForm().findField("SSY").setValue(TZNR);
						}else if(XMH == 6){
							this.form.getForm().findField("SZY").setValue(TZNR);
						}else if(XMH == 7){
							this.form.getForm().findField("TZ").setValue(TZNR);
						}else if(XMH == 8){
							this.form.getForm().findField("SG").setValue(TZNR);
						}else if(XMH == 11){
							this.form.getForm().findField("BZLX").setValue(TZNR);
							this.form.getForm().findField("BZXX").setValue(BZXX);
						}else if(XMH == 31){
							this.form.getForm().findField("DB").setValue(TZNR);
						}else if(XMH == 32){
							this.form.getForm().findField("XB").setValue(TZNR);
						}else if(XMH == 108){
							this.form.getForm().findField("PS").setValue(TZNR);
						}else if(XMH == 33){
							this.form.getForm().findField("QTCL").setValue(TZNR);
						}else if(XMH == 145){
							this.form.getForm().findField("RL").setValue(TZNR);
						}
					}
				}
				
			}
		})
