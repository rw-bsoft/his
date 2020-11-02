$package("phis.application.sup.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")
/**
 * 申领管理新增修改界面表单
 * 
 * @author gaof
 */
phis.application.sup.script.ApplyManagementDetailForm = function(cfg) {
    cfg.showButtonOnTop = false;
    phis.application.sup.script.ApplyManagementDetailForm.superclass.constructor.apply(
            this, [cfg])
}
Ext.extend(phis.application.sup.script.ApplyManagementDetailForm, phis.script.TableForm,
        {
            
        })