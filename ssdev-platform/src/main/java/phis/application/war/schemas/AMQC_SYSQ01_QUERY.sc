<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="AMQC_SYSQ01" alias="抗菌药物申请单">
	<item id="BRKS" alias="申请科室" type="long" length="18">
		<dic id="phis.dictionary.department_zy" autoLoad="true" emptyText="全部" />
	</item>
	<item id="DJZT" alias="单据状态" type="int" length="1" defaultValue="1">
		<dic id="phis.dictionary.billStatus" emptyText="全部" />
	</item>
	<item id="SPJG" alias="审批结果" type="int" length="1"  >
		<dic id="phis.dictionary.approveResult" emptyText="全部"/>
	</item>
</entry>
