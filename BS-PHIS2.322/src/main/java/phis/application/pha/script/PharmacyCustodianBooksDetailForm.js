$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyCustodianBooksDetailForm = function(cfg) {
	phis.application.pha.script.PharmacyCustodianBooksDetailForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyCustodianBooksDetailForm,
		phis.script.TableForm, {
			
		})