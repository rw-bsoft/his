$package("phis.application.cic.script.frmz");
/**
 * 发热门诊维护form 2020.02.14
 */
$import( "phis.script.TableForm");

phis.application.cic.script.frmz.ClinicFeverPatientNouthForm = function(cfg) {
    this.labelWidth = 90;
    phis.application.cic.script.frmz.ClinicFeverPatientNouthForm.superclass.constructor.apply(
        this, [cfg])
}
Ext.extend(phis.application.cic.script.frmz.ClinicFeverPatientNouthForm,
    phis.script.TableForm, {

    });