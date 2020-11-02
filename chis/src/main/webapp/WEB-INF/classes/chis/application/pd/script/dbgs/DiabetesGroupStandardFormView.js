$package("chis.application.pd.script.dbgs")

$import("app.modules.form.TableFormView")

chis.application.pd.script.dbgs.DiabetesGroupStandardFormView = function(cfg) {
	cfg.colCount = 2
	cfg.showButtonOnTop = true
	cfg.width = 540
	chis.application.pd.script.dbgs.DiabetesGroupStandardFormView.superclass.constructor.apply(
			this, [cfg]);
	this.on("save", this.onSave, this)
}

Ext.extend(chis.application.pd.script.dbgs.DiabetesGroupStandardFormView,
		app.modules.form.TableFormView, {
//			onBeforeSave : function(entryName, op, saveData) {
//				var cnd = []
//				var cnd1 = ['or', ['le', ['$', 'fbs1'], ['d', saveData.fbs1]],
//								  ['ge', ['$', 'fbs2'], ['d', saveData.fbs2]]]
//				var cnd2 = ['or', ['le', ['$', 'fbs2'], ['d', saveData.fbs1]],
//						          ['ge', ['$', 'fbs2'], ['d', saveData.fbs2]]]
//				var cnd3 = ['or', ['le', ['$', 'pbs1'], ['d', saveData.pbs1]],
//						          ['ge', ['$', 'pbs1'], ['d', saveData.pbs2]]]
//				var cnd4 = ['or', ['le', ['$', 'pbs2'], ['d', saveData.pbs1]],
//						          ['ge', ['$', 'pbs2'], ['d', saveData.pbs2]]]
//				var cnd5 = ['eq', ['$', 'bloodType'], ['d', saveData.bloodType]]
//				var cnd6 = []
//				var cnd = []
//				var cnd1 = ['or', ['and',['ge', ['$', 'fbs1'], ['d', saveData.fbs1]],
//								  ['le', ['$', 'fbs2'], ['d', saveData.fbs2]]],
//								  ['and',['ge', ['$', 'fbs2'], ['d', saveData.fbs1]],
//								  ['le', ['$', 'fbs2'], ['d', saveData.fbs2]]]]
//				var cnd2 = ['or', ['and',['ge', ['$', 'pbs1'], ['d', saveData.pbs1]],
//						          ['le', ['$', 'pbs1'], ['d', saveData.pbs2]]],
//						          ['and',['ge', ['$', 'pbs2'], ['d', saveData.pbs1]],
//						          ['le', ['$', 'pbs2'], ['d', saveData.pbs2]]]]
//				var cnd3 = ['eq', ['$', 'bloodType'], ['d', saveData.bloodType]]
//				var cnd4 = []
//				if (saveData.standardId) {
//					var cnd4 = ['ne', ['$', 'standardId'],
//							['s', saveData.standardId]]
//					cnd = ['and',cnd1,cnd2,cnd3,cnd4]
//				}else{
//					cnd = ['and',cnd1,cnd2,cnd3]
//				}
//				var result = util.rmi.miniJsonRequestSync({
//							serviceId : "chis.simpleQuery",
//							schema : "MDC_DiabetesGroupStandard",
//							cnd : cnd
//						})
//				if (result.json.totalCount > 0) {
//					alert("输入数据不符合规范，请检查后再输入")
//					return false
//				}
//			},
			onSave : function(entryName, op, json, data) {
				this.win.hide()
			}
		});