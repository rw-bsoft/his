<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_SFMX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" length="18" not-null="1" type="long" pkey="true"/>
	<item id="SFXM" alias="收费项目" length="18" not-null="1" type="long" pkey="true"/>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
	<item id="ZJJE" alias="总计金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZFJE" alias="自负金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="FPHM" alias="发票号码" type="string" length="20" not-null="1"/>
</entry>
