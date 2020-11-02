<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<entry alias="农合审批目录" entityName="NH_BSOFT_SPML" sort="">
<item alias="农合项目编码" id="HYBM" length="20" not-null="true"  type="string"/>
<item alias="项目类型" id="XMLX" not-null="true" pkey="true" type="double"/>
<item alias="医院编码" id="YYBM" length="20" not-null="true" pkey="true" type="string"/>
<item alias="项目名称" id="XMMC" length="80" type="string"/>
<item alias="项目规格" id="XMGG" length="20" type="string"/>
<item alias="自费比例" id="ZFBL" type="double"/>
<item alias="门诊自费比例" id="MZZFBL" type="double"/>
<item alias="审批结果" id="SPJG" length="2" type="string"/>
<item alias="机构编码" id="JGID" not-null="true" type="string" pkey="true"/>
</entry>
