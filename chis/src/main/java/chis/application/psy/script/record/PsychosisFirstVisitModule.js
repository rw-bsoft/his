$package("chis.application.psy.script.record");

$import("chis.script.BizCombinedModule2");

chis.application.psy.script.record.PsychosisFirstVisitModule = function(cfg) {
	// this.initCnd = ["eq", ["$", "a.status"], ["s", "0"]]
	cfg.autoLoadData = false;

	chis.application.psy.script.record.PsychosisFirstVisitModule.superclass.constructor.apply(this,
			[cfg]);
	this.layOutRegion = "north";
	this.height = 440;
	this.itemHeight = 360;
	this.itemCollapsible = false;
	this.frame = true;
	this.loadServiceId = "chis.psychosisVisitService";
	this.loadAction = "initalizeFirstVisitModule";
	this.on("loadData", this.onLoadData, this);
	this.entryName = "chis.application.psy.schemas.PSY_PsychosisFirstVisit"
};

Ext.extend(chis.application.psy.script.record.PsychosisFirstVisitModule, chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.psy.script.record.PsychosisFirstVisitModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("medicineSelect", this.onMedicineSelect, this);
				this.form.on("firstVisitFormSave",this.onFirstFormSave,this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("regainMedicineValue",this.onRegainMedicineValue,this);
				return panel;
			},
			onFirstFormSave : function(){
				if(this.saving){
					return
				}
				var body = {};
				if(!this.validate()){
					Ext.Msg.alert("提示", "首次随访的信息填写不完整");
					return;
				}else{
					body.firstVisitData = this.getFirstVisitData();
					body.firstVisitData.empiId = this.exContext.ids.empiId;
					body.firstVisitData.phrId = this.exContext.ids.phrId;
					body.firstVisitData.type = '0';
					body.medicineList = this.getMedicineList();
					this.fireEvent("firstVisitSave",body);
				}
			},
			onRegainMedicineValue : function(){
				this.form.regainMedicineValue();
			},
			getLoadRequest : function() {
				var body = {
					phrId : this.exContext.ids.phrId
				};
				return body;
			},

			onLoadData : function(entryName, data) {
				this.deleteMedicine = false;//初始化
				this.form.loadForMedicine = true;
				if(data[this.entryName+"_data"]){
					var visitId = data[this.entryName+"_data"].visitId;
				}
				this.onMedicineSelect(data.medicine,visitId);
				var pvrNum = data.pvrNum;
//				var pvpNum = data.pvpNum;
				if (pvrNum > 0) {
					this.form.op = "update";
//					if (pvpNum > 0) {
//						this.form.setVisitTypeDisable(true);
//					} else {
//						this.form.setVisitTypeDisable(false);
//					}
				} else {
					this.form.op = "create";
				}
			},
			
			onMedicineSelect : function(value,visitId) {
				var fvControl = true;
				if(visitId){
					fvControl = this.exContext.control.update;
				}else{
					fvControl = this.exContext.control.create;
				}
				if (fvControl == false || !value || value == "3" || value == "") {
					this.list.setMedicineManagerButtonState(false);
				} else {
					this.list.setMedicineManagerButtonState(true);
				}
				this.deleteMedicine = this.list.getDeleteMedicineState(value);
			},
			
			validate : function() {
				return this.form.validate();
			},

			getFirstVisitData : function() {
				var fromData = this.form.getFirstVisitData();
				fromData.deleteMedicine = this.deleteMedicine;
				return fromData;
			},
			regainFormMedicineValue : function(){
				this.form.regainMedicineValue();
			},
			
			getMedicineList : function() {
				return this.list.getMedicineList();
			},
			deleteListAllMedicine : function(){
				this.list.deleteAllMedicine();
			},
			
			onReady : function() {
                // ** 设置滚动条

                if (this.isAutoScroll == false || this.layOutRegion != "north") {
                    return;
                }
                if(this.isAtEHRView){
                    this.newHeight = Ext.getBody().getHeight()-116
                }else{
                    this.newHeight = this.getFormHeight();
                }
                this.panel.setWidth(this.getFormWidth());
                this.panel.setHeight(this.getFormHeight());
                var norHeight = this.itemHeight || this.height;
                // ** 如果上面部分的高度大于窗体的一半，则设置为(2/3)+6窗体高度
                if (norHeight > this.newHeight *( 2/3)+6) {
                    this.newHeight = this.newHeight *( 2/3)+6;
                } else {
                    this.newHeight = norHeight
                }
                this.panel.items.each(function(item) {
                            item.setWidth(this.getFormWidth());
                            item.setHeight(this.newHeight);
                            item.setAutoScroll(true);
                        }, this);
            }

		});