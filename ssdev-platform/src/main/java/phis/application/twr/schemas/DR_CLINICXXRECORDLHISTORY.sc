<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICRECORDLHISTORY" tableName="DR_CLINICRECORDLHISTORY" alias="转诊历史记录">
    <!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" display="0">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" display="0" />
    <item id="MZHM" alias="门诊号码" type="string" length="32" fixed="false"/>
    <item id="ZHUANZHENDH" alias="转诊单号" length="20" fixed="false" type="string"/>
    <item id="STATUS" alias="转诊状态" length="2" fixed="false" type="string" />
    <item id="BINGRENXM" alias="病人姓名" type="string" length="32" fixed="false"/>
	<item id="BINGRENXB" alias="病人性别" type="string" length="32" fixed="false"/>
	<item id="BINGRENCSRQ" alias="病人出生日期" type="string" length="32" fixed="false"/>
	<item id="BINGRENNL" alias="病人年龄" type="string" length="32" fixed="false"/>
	<item id="BINGRENSFZH" alias="病人身份证号" type="string" length="32" fixed="false"/>
	<item id="BINGRENLXDH" alias="病人联系电话" type="string" length="32" fixed="false"/>
	<item id="BINGRENLXDZ" alias="病人联系地址" type="string" length="32" colspan="2" fixed="false"/>
	<item id="BINGRENFYLB" alias="费用类别" type="string" length="32" fixed="false"/>
    <item id="SHENQINGYS" alias="申请医生" length="20" fixed="false" type="string" />
    <item id="SHENQINGRQ" alias="申请日期" type="string" length="20" fixed="false" update="false" defaultValue="%server.date.date" not-null="true" />
	<item id="JIUZHENKLX" alias="就诊卡类型" type="string" length="32" fixed="false"/>
	<item id="JIUZHENKH" alias="就诊卡号" type="string" length="32" fixed="false"/>
	<item id="YIBAOKLX" alias="医保卡类型" type="string" length="32" fixed="false"/>
	<item id="YIBAOKXX" alias="医保卡信息" type="string" length="32" fixed="false"/>
	<item id="YEWULX" alias="业务类型" type="string" length="32" fixed="false"/>
	<item id="SHENQINGJGMC" alias="申请机构名称" type="string" length="32" colspan="2" fixed="false"/>
	<item id="SHENQINGJGLXDH" alias="申请机构电话" type="string" length="32" fixed="false"/>
	<item id="SHENQINGYSDH" alias="申请医生电话" type="string" length="32" fixed="false"/>
	<item id="ZHUANRUKSMC" alias="转入科室名称" type="string" length="32" fixed="false"/>
	<item id="ZHUANZHENYY" alias="转诊原因" type="string" length="32" colspan="3" fixed="false"/>
	<item id="BINQINGMS" alias="病情描述" type="string" length="32" colspan="3" fixed="false"/>
	<item id="ZHUANZHENZYSX" alias="转诊注意事项" type="string" length="32" colspan="3" fixed="false"/>
</entry>
