<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MPI_Certificate" alias="EMPI个人基本信息">
	<item id="certificateId" alias="编号" hidden="true" type="string" length="16" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPI" type="string" length="32" hidden="true" />
	<item id="certificateTypeCode" alias="证件类型" defaultValue="08" type="string" length="2" not-null="true">
		<dic id="phis.dictionary.certificate"/>
	</item>
	<item id="certificateNo"    alias="证件号码"  type="string" length="20"  not-null="true" width="160"/>
</entry>