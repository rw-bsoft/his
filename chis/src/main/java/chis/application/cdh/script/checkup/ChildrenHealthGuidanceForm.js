/**
 * 儿童体格检查健康指导表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup")
$import("chis.script.BizTableFormView","chis.application.mpi.script.CombinationSelect");
chis.application.cdh.script.checkup.ChildrenHealthGuidanceForm = function(cfg) {
	chis.application.cdh.script.checkup.ChildrenHealthGuidanceForm.superclass.constructor
			.apply(this, [cfg])
	this.querySchema = "chis.application.cdh.schemas.CDH_DictCorrection";
	this.on("loadNoData", this.onLoadNoData, this);
	this.on("loadData",this.onLoadData,this);
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(chis.application.cdh.script.checkup.ChildrenHealthGuidanceForm,
		chis.script.BizTableFormView, {

					onBeforeSave : function(entryName, op, saveData) {
				saveData.checkupId = this.exContext.args[this.checkupType
						+ "_param"].checkupId || this.exContext.args.checkupId;
				saveData.checkupType = this.checkupType;
				saveData.phrId = this.exContext.ids["CDH_HealthCard.phrId"];
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"checkupId" : this.exContext.args[this.checkupType
							+ "_param"].checkupId || this.exContext.args.checkupId,
					"checkupType" : this.checkupType,
					"checkupStage" :this.exContext.args[this.checkupType+"_param"].checkupStage
				};
			},
			
			onLoadData : function(entryName,body){
				var giSelect = body.giSelect;
				if(giSelect){
					this.showDataInSelectView(body.giList);
				}
			},
			
			showDataInSelectView : function(data){
				var HGRSelectView = this.midiModules["HGRSelectView"];
				if (!HGRSelectView) {
					var HGRSelectView = new chis.application.mpi.script.CombinationSelect({
								entryName : this.querySchema,
								disablePagingTbr : true,
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择儿童健康指导意见",
								width : 600,
								height : 400
							});
					HGRSelectView.on("onSelect", function(r) {
								var data = r.data;
								this.initFormData(this.reMakeData(data));
							}, this);
					HGRSelectView.initPanel();
					this.midiModules["HGRSelectView"] = HGRSelectView;
				}
				HGRSelectView.getWin().show();
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					var record = new Ext.data.Record(r);
					records.push(record);
				}
				HGRSelectView.setRecords(records);
			},
			
			reMakeData : function(data) {
				var initData = [];
				initData.phrId = this.exContext.ids["CDH_HealthCard.phrId"];
				initData.checkupId = this.exContext.args[this.checkupType
						+ "_param"].checkupId || this.exContext.args.checkupId;
				initData.checkupType = this.checkupType;
				initData.guidingIdea = data.suggestion;
				return initData;
			},

			onLoadNoData : function() {
				this.doNew();
			}

		})