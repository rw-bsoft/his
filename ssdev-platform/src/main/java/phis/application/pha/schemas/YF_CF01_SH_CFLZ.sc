<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01"  type="string" alias="处方审核列表">
	<item id="YFSB" alias="药房识别" length="18" type="long" display="0"
		not-null="1" defaultValue="%user.properties.pharmacyId" pkey="true" update="false" />
	<item id="JGID" alias="机构ID" length="20"  display="0" not-null="1" defaultValue="%user.manageUnit.id" />
	<item id="CFSB" alias="处方识别" display="0" type="long" length="18" not-null="1" isGenerate="false" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1000" />
		</key>
	</item>
	<item id="JGMC" alias="机构名称" type="string" length="40" />
	<item id="CFHM" alias="处方号码" type="string" length="10" />
	<item id="BRXM" alias="病人姓名" type="string" length="40" />
	<item id="KSMC" alias="就诊科室" type="string" length="50" />
	<item id="YSXM" alias="开方医生" type="string" length="40" />
	<item id="CFLX" alias="处方类型" type="int" display="0" />
</entry>