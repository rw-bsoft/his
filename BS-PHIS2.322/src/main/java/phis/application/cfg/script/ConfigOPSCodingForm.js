$package("phis.application.cfg.script");
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigOPSCodingForm = function (cfg) {
    cfg.modal = true;
    phis.application.cfg.script.ConfigOPSCodingForm.superclass.constructor.apply(this,
        [cfg])
};
Ext.extend(phis.application.cfg.script.ConfigOPSCodingForm, phis.script.TableForm, {
    onReady: function () {
        phis.application.cfg.script.ConfigOPSCodingForm.superclass.onReady
            .call(this)
        var form = this.form.getForm();

        var ssdm = form.findField("SSDM");
        if (ssdm) {
            ssdm.on("change",
                this.onICD10NumberChange, this);
        }
    },
    onICD10NumberChange: function (f) {
        f.setValue(f.getValue().toUpperCase());
    }
})