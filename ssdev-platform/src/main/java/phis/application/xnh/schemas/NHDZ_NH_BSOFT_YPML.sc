<?xml version="1.0" encoding="UTF-8"?>

<entry id="NHDZ_NH_BSOFT_YPML" tableName="NH_BSOFT_YPML" alias="农合药品目录" >
<item id="YPXH" alias="药品序号" length="18" not-null="true" pkey="true" type="string"/>
<item id="YPMC" alias="药品名称" length="120" type="string" queryable="true" selected="true" />
<item id="YPGG" alias="药品规格" length="40" type="string"/>
<item id="ZFBL" alias="自负比例" type="double"/>
<item id="YPLX" alias="药品类型" length="2" type="string"/>
<item alias="药品单位" id="YPDW" length="12" type="string"/>
<item alias="药品剂型" id="YPSX" length="60" type="string"/>
<item alias="自负判别" id="ZFPB" type="double"/>
<item alias="拼音代码" id="PYDM" length="12" type="string" queryable="true" />
<item alias="通用名称" id="TYMC" length="80" type="string"/>
<item alias="门诊自负比例" id="MZZFBL" type="double"/>
<item alias="注册编号" id="VERSION" length="20" type="string" hidden="true"/>
<item alias="备注" id="BZ" length="100" type="string" hidden="true"/>
</entry>
