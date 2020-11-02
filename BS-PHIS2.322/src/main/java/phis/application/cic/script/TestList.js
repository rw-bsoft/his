/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("com.bsoft.phis.clinic")

$import("app.biz.BizListView")

com.bsoft.phis.clinic.TestList = function(cfg) {
	com.bsoft.phis.clinic.TestList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(com.bsoft.phis.clinic.TestList, app.biz.BizListView, {
			onDblClick : function(grid, index, e) {
//				var lastIndex = grid.getSelectionModel().lastActive;
//				var record = grid.store.getAt(lastIndex);
//				if (record) {
//					this.fireEvent("departmentChoose", record);
//				}
				var module = this.midiModules["HealthRecord_EHRView"];
						if (!module) {
							$import("com.bsoft.phis.clinic.EMRView");
							module = new com.bsoft.phis.clinic.EMRView({
										empiId : this.empiId,
										closeNav : true,
										mainApp : this.mainApp
									});
							this.midiModules["HealthRecord_EHRView"] = module;
							}
					module.getWin().show(); 
			}
			,
			onESCKey : function() {
				this.cndField.focus();
			}
		});