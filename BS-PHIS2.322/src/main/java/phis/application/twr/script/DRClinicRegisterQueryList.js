$package("phis.application.drc.script")

$import("phis.script.SimpleList")

/**
 *@author : chzhxiang
 *@date : 2013.08.14
 */
phis.application.drc.script.DRClinicRegisterQueryList = function(cfg) {
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
	phis.application.drc.script.DRClinicRegisterQueryList.superclass.constructor.apply(this, [cfg])
}
var recordIds = {};
var datas = {};

Ext.extend(phis.application.drc.script.DRClinicRegisterQueryList,
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
			
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('ghfp');"
			},

			doPrint : function() {
				var r = this.getSelectedRecord();
				var guahaoid = r.data.GUAHAOID
				var empiid = r.data.EMPIID
				var pages="RegisterOrder";
				var url="resources/"+pages+".print?silentPrint=1&execJs="
				    + this.getExecJs() + '&guahaoid='+guahaoid+'&empiid='+empiid;
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
				var m = this.midiModules["clinicRegister"];
				if (!m) {
					$import("phis.application.cic.script.ClinicRegisterModule");
				}
					m = new phis.application.cic.script.ClinicRegisterModule({
								entryName : "phis.application.drc.schemas.DR_CLINICZZRECORDHISTORY",
								title : "当天挂号信息",
								height : 450,
								modal : true,
								mainApp : this.mainApp,
								baseInfo : r.data
							});
				this.midiModules["clinicRegister"] = m;
				
				var win = m.getWin();
				win.setPosition(300, 100);
				win.show();
			}
		})