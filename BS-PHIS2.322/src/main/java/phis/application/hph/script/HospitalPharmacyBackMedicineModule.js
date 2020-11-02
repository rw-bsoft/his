/**
 * 医嘱发药-下面module
 * 
 * @author caijy
 */
$package("phis.application.hph.script");

$import("phis.script.SimpleModule");

phis.application.hph.script.HospitalPharmacyBackMedicineModule = function(cfg) {
	 this.exContext={};
	 this.width = 1024;
	this.height = 550;
	phis.application.hph.script.HospitalPharmacyBackMedicineModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.hph.script.HospitalPharmacyBackMedicineModule,
		phis.script.SimpleModule, {
				initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : true,
							frame : false,
							height:this.height,
							layout : 'border',
							defaults : {
								border : true
							},
							items : [{
										layout : "fit",
										border : true,
										split : true,
										title : '',
										region : 'north',
										height : 250,
										items : this.getLeftList()
									}, {
										layout : "fit",
										border : true,
										split : true,
										title : '',
										region : 'center',
										items : this.getRightList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getLeftList : function() {
				this.topList = this.createModule("refTopList", this.refTopList);
				this.topList.on("recordClick",this.showRecord,this);
				return this.topList.initPanel();
			},
			getRightList : function() {
				this.underList = this.createModule("refUnderList", this.refUnderList);
				this.underList.on("recordClick",this.onUnderRecordClick,this);
				return this.underList.initPanel();
			},
			//上一级单击显示退药记录
			showRecord:function(tybq){
				var body={};
				body["TYBQ"]=tybq
			//this.underList.requestData.cnd=['and',['eq',['$','a.TYBQ'],['s',tybq]],this.underList.cnds];
				this.underList.requestData.body=body;
				this.underList.clearSelect();
			this.underList.loadData();
			},
			//单击发药记录显示明细
			onUnderRecordClick:function(r){
			this.fireEvent("recordClick",r)
			},
			//获取发药数据
			getData:function(){
			var record=this.underList.getSelectedRecords();
			var ret=new Array();
			for(var i=0;i<record.length;i++){
			ret.push(record[i].json);
			}
			return ret;
			},
			//发完药或者退病区后刷新页面
			doNew:function(){
				this.topList.refresh();
				this.underList.doNew();
			},
			getRecords:function(){
			var module=this.underList;
			var record= module.getSelectedRecords();
			var ret=new Array();
			for(var i=0;i<record.length;i++){
			ret.push(record[i].json);
			}
			return ret;
			}
		});