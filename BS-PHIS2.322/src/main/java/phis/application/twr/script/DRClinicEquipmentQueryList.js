$package("phis.application.twr.script")

$import("phis.script.SimpleList")

/**
 * @author : chzhxiang
 * @date : 2013.08.14
 */
phis.application.twr.script.DRClinicEquipmentQueryList = function(cfg) {
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
	phis.application.twr.script.DRClinicEquipmentQueryList.superclass.constructor
			.apply(this, [cfg])
}
var recordIds = {};
var datas = {};

Ext.extend(phis.application.twr.script.DRClinicEquipmentQueryList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.cnd = ['eq', ['$', 'empiId'],['s', this.exContext.empiId]];
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
			doCancel : function() {
				var r = this.getSelectedRecord();
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : r.data
						}, function(code, msg, json) {
							if (code < 300) {
								this.store.remove(r)
								/*
								 * this.fireEvent("remove", this.entryName,
								 * 'remove', json, r.data);
								 */
								MyMessageTip.msg("提示", "设备预约取消成功!", true);
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this);
			},
			
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('jcfp');"
			},

			doPrint : function() {
				var r = this.getSelectedRecord();
				var yuyuesqdbh = r.data.YUYUESQDBH
				var empiid = r.data.EMPIID
				var pages="EquimentOrder";
			    var url="resources/"+pages+".print?silentPrint=1&execJs="
			         + this.getExecJs() + '&yuyuesqdbh='+yuyuesqdbh+'&empiid='+empiid;
				
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
				var m = this.midiModules["EquipmentXxRecord"];
				if (!m) {
					$import("phis.application.drc.script.EquipmentXxRecordModule");
				}
					m = new phis.application.drc.script.EquipmentXxRecordModule({
								entryName : "phis.application.drc.schemas.DR_ClinicXxEquipmentHistory",
								title : "设备预约信息",
								height : 450,
								modal : true,
								mainApp : this.mainApp,
								baseInfo : r.data
							});
				this.midiModules["EquipmentXxRecord"] = m;
				var win = m.getWin();
				win.setPosition(300, 100);
				win.show();
			}
		})