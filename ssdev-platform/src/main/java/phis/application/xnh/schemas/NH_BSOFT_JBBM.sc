<?xml version="1.0" encoding="UTF-8" ?>
<entry alias="农合疾病" entityName="NH_BSOFT_JBBM"  sort="a.CODE">
<item id="CODE" alias="疾病编码" not-null="true" pkey="true" type="long"/>
<item id="NAME" alias="疾病名称" length="200" type="string" queryable="true" />
<item id="PYDM" alias="拼音代码" length="30" type="string" queryable="true"/>
<item id="ICD10" alias="ICD10" length="10" type="string"/>
<item id="WBDM" alias="五笔代码" length="30" type="string"/>
</entry>