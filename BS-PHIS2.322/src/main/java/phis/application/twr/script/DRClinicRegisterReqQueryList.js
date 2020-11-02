$package("phis.application.drc.script")

$import("phis.script.SimpleList")

/**
 *@author : chzhxiang
 *@date : 2013.08.14
 */
phis.application.drc.script.DRClinicRegisterReqQueryList = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	this.preview = [{
				value : "1",
				text : "网页预览"
			}, {
				value : "0",
				text : "PDF"
			}, {
				value : "2",
				text : "WORD"
			}, {
				value : "3",
				text : "EXCEL"
			}]
	phis.application.drc.script.DRClinicRegisterReqQueryList.superclass.constructor.apply(this, [cfg])
}
var recordIds = {};
var datas = {};

Ext.extend(phis.application.drc.script.DRClinicRegisterReqQueryList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.cnd = ['eq',['$','empiId'],['s',this.exContext.empiData.empiId]];
				this.clear();
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			
			doCancel : function(){
				var r = this.getSelectedRecord();
				this.datas = this.exContext.empiData
				this.datas["QUHAOMM"] = r.data["QUHAOMM"]
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : this.datas
						}, function(code, msg, json) {
							if (code < 300) {
								this.store.remove(r)
								MyMessageTip.msg("提示", "挂号预约取消成功!", true);
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this);
			},
			
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('ghyyfp');"
			},

			doPrint : function() {
				var r = this.getSelectedRecord();
				var quhaomm = r.data.QUHAOMM
				var empiid = r.data.EMPIID
				var pages="RegisterReqOrder";
				var url="resources/"+pages+".print?silentPrint=1&execJs="
				   + this.getExecJs() + '&quhaomm='+quhaomm+'&empiid='+empiid;
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.PREVIEW();// 预览

			},
			
			onDblClick : function(){
				var r = this.getSelectedRecord();
				var m = this.midiModules["clinicRegisterReq"];
				if (!m) {
					$import("phis.application.cic.script.ClinicRegisterReqModule");
				}
					m = new phis.application.cic.script.ClinicRegisterReqModule({
								entryName : "phis.application.drc.schemas.DR_CLINICZZREQRECORDHISTORY",
								title : "预约挂号信息",
								height : 450,
								modal : true,
								mainApp : this.mainApp,
								baseInfo : r.data
							});
				this.midiModules["clinicRegisterReq"] = m;
				var win = m.getWin();
				win.setPosition(300, 100);
				win.show();
			}
		})