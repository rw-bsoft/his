﻿$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalToBedInformationList = function(cfg) {
	phis.application.hos.script.HospitalToBedInformationList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalToBedInformationList,
		phis.script.SimpleList, {

		});