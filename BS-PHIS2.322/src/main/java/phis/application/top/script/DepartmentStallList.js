$package("com.bsoft.phis.pub")

/**
 * 员工维护form
 * 
 * @param {}
 *            cfg
 */

$import("com.bsoft.phis.SimpleList")

com.bsoft.phis.pub.DepartmentStallList = function(cfg) {
	com.bsoft.phis.pub.DepartmentStallList.superclass.constructor.apply(this,
			[cfg])

}
Ext.extend(com.bsoft.phis.pub.DepartmentStallList, com.bsoft.phis.SimpleList,
		{})