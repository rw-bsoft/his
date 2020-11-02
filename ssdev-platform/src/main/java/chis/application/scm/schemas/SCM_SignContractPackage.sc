<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_SignContractPackage" alias="签约服务包记录">
	<item id="SCSPID" alias="签约服务包编号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
	<item id="empiId" alias="个人主索引" type="string" length="32" display="0"/>
	<item id="SCID" alias="签约记录编号" type="long" length="18" display="0"/>
	<item id="SPID" alias="服务包编号" type="string" length="20" display="0"/>
	<item id="SERVICETIMES" alias="服务次数" type="string" length="20" display="0"/>
	<item id="SPIID" alias="服务项编号" type="string" length="20" display="0"/>

	<item ref="b.packageName"/>
	<item ref="b.packageIntendedPopulation"/>
	<item ref="b.intro"/>
	<item ref="b.remark"/>

	<relations>
		<relation type="parent" entryName="chis.application.scm.schemas.SCM_ServicePackage">
			<join parent="SPID" child="SPID" />
		</relation>
	</relations>
</entry>
