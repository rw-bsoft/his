$package("phis.application.war.script")

$import("phis.script.SimpleList","phis.script.SimpleForm")

phis.application.war.script.DoctorAdviceExecuteList = function(cfg) {
	cfg.disablePagingTbr = true; // 分页暂时不要
	cfg.autoLoadData =false;
	phis.application.war.script.DoctorAdviceExecuteList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.war.script.DoctorAdviceExecuteList,
		phis.script.SimpleList, {
			loadData : function(al_zyh) {
				var ZYH = 0;
				if(al_zyh){
					ZYH = al_zyh;
				}
				this.clear(); 
				this.requestData.serviceId = "phis.doctorAdviceExecuteService";
				this.requestData.serviceAction = "patientQuery";
				this.requestData.cnd = ZYH;
				
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
			}
			
		});