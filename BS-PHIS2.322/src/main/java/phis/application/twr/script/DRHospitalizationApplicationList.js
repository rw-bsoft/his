$package("phis.application.drc.script")

$import("phis.script.SimpleList")

/**
 *@author : chzhxiang
 *@date : 2013.08.14
 */
phis.application.drc.script.DRHospitalizationApplicationList = function(cfg) {
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
	phis.application.drc.script.DRHospitalizationApplicationList.superclass.constructor.apply(this, [cfg])
}
var recordIds = {};
var datas = {};

Ext.extend(phis.application.drc.script.DRHospitalizationApplicationList,
		phis.script.SimpleList, {
			loadData : function() {
				this.setRequestMsg();
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
			setRequestMsg : function(){
				this.requestData.cnd = ['and',['eq',['$','zyh'],['s',this.exContext.ZYH]],['eq',['$','yewulx'],['s','2']]];
			},
			
			onDblClick : function() {
				this.refresh();
			},
			
			doCancel : function() {
				this.getWin().hide();
			},
			
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('mzyfp');"
			},

			doPrint : function() {
				var r = this.getSelectedRecord();
				var zhuanzhendh = r.data.ZHUANZHENDH
				var empiid = r.data.EMPIID
				var pages="HospitalReqOrder";
			    var url="resources/"+pages+".print?silentPrint=1&execJs="
				   + this.getExecJs() + '&zhuanzhendh='+zhuanzhendh+'&empiid='+empiid;
			   
			    var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//预览
				LODOP.PREVIEW();

			},
			
			onDblClick : function(){
				var r = this.getSelectedRecord();
				r.data["YEWULX"] = "2";
				var m = this.midiModules["HospitalXxRecord"];
				if (!m) {
					$import("phis.application.drc.script.HospitalXxRecordModule");
				}
					m = new phis.application.drc.script.HospitalXxRecordModule({
								entryName : "phis.application.drc.schemas.DR_CLINICXXRECORDLHISTORY",
								title : "住院转住院信息",
								height : 450,
								modal : true,
								mainApp : this.mainApp,
								baseInfo : r.data
							});
				this.midiModules["HospitalXxRecord"] = m;
				var win = m.getWin();
				win.setPosition(300, 100);
				win.show();
			}
		})