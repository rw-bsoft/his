<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICCHECKHISTORY" tableName="DR_CLINICCHECKHISTORY" alias="检查申请记录">
    <!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" display="0">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" display="0" />
    <item id="MZHM" alias="门诊号码" type="string" length="32" fixed="false"/>
    <item id="BINGRENXM" alias="病人姓名" type="string" length="32" fixed="false"/>
    <item id="BINGRENSFZH" alias="病人身份证号" type="string" length="32" fixed="false"/>
    <item id="JIANCHASQDH" alias="检查申请单号" length="20" width="120" fixed="false" type="string"/>
    <item id="STATUS" alias="送检状态" length="2" fixed="false" type="string" />
    <item id="SONGJIANKSMC" alias="送检机构" length="20" fixed="false" type="string" />
    <item id="SONGJIANYS" alias="送检医生" length="20" fixed="false" type="string" />
    <item id="SONGJIANRQ" alias="送检日期" type="date" length="20" fixed="false" update="false" defaultValue="%server.date.date" not-null="true" />
    <item id="JIUZHENKLX" alias="就诊卡类型" type="string" length="32" fixed="false"/>
	<item id="JIUZHENKH" alias="就诊卡号" type="string" length="32" fixed="false"/>
	<item id="SHOUFEISB" alias="收费识别" type="string" length="32" fixed="false"/>
	<item id="ZHENDUAN" alias="诊断" type="string" length="50" fixed="false"/>
	<item id="BINGRENTZ" alias="病人体征" type="string" length="50" colspan="3" fixed="false"/>
	<item id="BINGRENZS" alias="病人主诉" type="string" length="50" colspan="3" fixed="false"/>
	<item id="BINGQINGMS" alias="病情描述" type="string" length="50" colspan="3" fixed="false"/>
	<item id="QITAJC" alias="其它检查" type="string" length="50" colspan="3" fixed="false"/>
</entry>
