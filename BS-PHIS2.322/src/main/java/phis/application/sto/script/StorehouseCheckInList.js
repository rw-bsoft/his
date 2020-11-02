/**
 * 采购入库右边list
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleRightList");

phis.application.sto.script.StorehouseCheckInList = function(cfg) {
	cfg.cnds = ['eq', ['$', 'RKPB'], ['i', 1]]
	cfg.refStorehouseListPrint="phis.application.sto.STO/STO/STO0303";
	phis.application.sto.script.StorehouseCheckInList.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseCheckInList,
				phis.application.sto.script.StorehouseMySimpleRightList, {
		getQueryCnd : function(tag,dates) {
		if(tag==1){
		return [
							'and',
							[
									'ge',
									['$', "str(RKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[0]]],
							[
									'le',
									['$', "str(RKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[1]]]];
		}else{
		return ['eq', ['$', 'a.RKFS'], ['i', this.selectValue]]
		}
	},
	getInitDataBody:function(r){
				var initDataBody = {};
				initDataBody["XTSB"] = r.data.XTSB;
				initDataBody["RKFS"] = r.data.RKFS;
				initDataBody["RKDH"] = r.data.RKDH;
				initDataBody["YSDH"] = r.data.YSDH;
				return initDataBody;
	},
	doExport: function(){
		var r = this.getSelectedRecord()
        if (r == null) {
            MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
            return;
        }
        var pages="phis.prints.jrxml.StorehouseIn2";
		var url="resources/"+pages+".print?silentPrint=3&type=3";
        url += "&temp=" + new Date().getTime() + "&xtsb=" + r.data.XTSB
				+ "&rkfs=" + r.data.RKFS+ "&rkdh=" + r.data.RKDH+ "&fdjs=" + r.data.FDJS
        var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		return printWin;
	},
	doPrint : function() {
        var module = this.createModule("storehouseinprint",
                this.refStorehouseListPrint) 
        var r = this.getSelectedRecord()
        if (r == null) {
            MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
            return;
        }
        module.xtsb = r.data.XTSB;
        module.rkfs = r.data.RKFS;
        module.rkdh = r.data.RKDH;
        module.pwd = r.data.PWD;
        module.fdjs = r.data.FDJS; 
        module.initPanel(); 
        module.doPrint(); 
    }
});