$package("phis.application.sup.script")

$import("phis.script.TableForm")
/**
 * 转科管理新增修改界面表单
 * 
 * @author gaof
 */
phis.application.sup.script.TransferManagementDetailForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.sup.script.TransferManagementDetailForm.superclass.constructor
			.apply(this, [ cfg ])
}
Ext.extend(phis.application.sup.script.TransferManagementDetailForm,
		phis.script.TableForm, {

		})