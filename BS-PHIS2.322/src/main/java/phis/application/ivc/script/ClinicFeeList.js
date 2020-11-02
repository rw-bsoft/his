/**
 * 待发药处方列表
 * 
 * @author : caijy
 */
$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.ClinicFeeList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = false;
	phis.application.ivc.script.ClinicFeeList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.ivc.script.ClinicFeeList, phis.script.SimpleList, {
	loadData : function() {
		var records = new Array();
		var gbdatas = [];
		var hjje = 0;
		for (var i = 0; i < this.opener.opener.list.store.getCount(); i++) {
			var r = this.opener.opener.list.store.getAt(i);
			if(!r.data.GBMC){
				continue;
			}
			hjje = (parseFloat(r.data.HJJE) + parseFloat(hjje)).toFixed(2);
			for(var j = 0 ; j < gbdatas.length ; j++){
				if(gbdatas[j]==r.data.GBMC){
					this.push = true;
					break;
				}
			}
			if(!this.push){
				gbdatas.push(r.data.GBMC);
			}else{
				this.push = false;
			}
		}
		
		for (var i = 0; i < gbdatas.length; i++) {
			var GBMC = gbdatas[i];
			var data = {}
			data.XMMC = GBMC;
			data.HJJE = 0;
			for(var j = 0; j < this.opener.opener.list.store.getCount(); j++) {
				var r = this.opener.opener.list.store.getAt(j);
				if(r.data.GBMC == GBMC){
					if(!isNaN(r.data.HJJE)){
						data.HJJE = (parseFloat(r.data.HJJE)+parseFloat(data.HJJE)).toFixed(2);
					}
				}
			}
			records.push(new Ext.data.Record(data));
		}
		this.store.removeAll();
		this.store.add(records);
		document.getElementById("MZSF_FYGB_FEE").innerHTML ="合计:&nbsp;&nbsp;&nbsp;&nbsp;"+hjje+"&nbsp;&nbsp;￥";
	},
	expansion : function(cfg) {
		// 底部 统计信息,未完善
		var label = new Ext.form.Label({
			html : "<div id='MZSF_FYGB_FEE' align='center' style='color:blue'>合计:&nbsp;&nbsp;&nbsp;&nbsp;0.00&nbsp;&nbsp;￥</div>"
		})
		cfg.bbar = [];
		cfg.bbar.push(label);
	}
});