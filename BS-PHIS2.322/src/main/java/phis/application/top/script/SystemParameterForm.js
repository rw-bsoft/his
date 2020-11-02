$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.TableForm")

com.bsoft.phis.pub.SystemParameterForm = function(cfg) {
	cfg.colCount = 2;
	cfg.saveServiceId = "systemParamsSave";
	com.bsoft.phis.pub.SystemParameterForm.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(com.bsoft.phis.pub.SystemParameterForm,
				com.bsoft.phis.TableForm, {
				
				
				})